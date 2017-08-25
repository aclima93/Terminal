package pt.uc.student.aclima.device_agent.Messaging.Publisher;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Database.Entries.Configuration;
import pt.uc.student.aclima.device_agent.Database.Entries.EventfulAggregatedMeasurement;
import pt.uc.student.aclima.device_agent.Database.Entries.EventfulMeasurement;
import pt.uc.student.aclima.device_agent.Database.Entries.OneTimeMeasurement;
import pt.uc.student.aclima.device_agent.Database.Entries.PeriodicAggregatedMeasurement;
import pt.uc.student.aclima.device_agent.Database.Entries.PeriodicMeasurement;
import pt.uc.student.aclima.device_agent.Database.Tables.ConfigurationsTable;
import pt.uc.student.aclima.device_agent.Messaging.SslUtil;
import pt.uc.student.aclima.device_agent.R;

import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_MQTT_KEEP_ALIVE;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_MQTT_TIMEOUT;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_DEVICE_ID;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_BASE_PUBLISH_TOPIC;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_PASSWORD;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_PORT;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_PROTOCOL;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_URI;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.updateDeviceId;

public class PublisherIntentService extends IntentService {

    public static final String ACTION_PUBLISH_DATA = "pt.uc.student.aclima.device_agent.Messaging.Publisher.PublisherIntentService.action.PUBLISH_DATA";
    public static final String EXTRA_PUBLISH_DATA_SAMPLE_START_TIME = "pt.uc.student.aclima.device_agent.Messaging.Publisher.PublisherIntentService.extra.PUBLISH_DATA_SAMPLE_START_TIME";

    public PublisherIntentService() {
        super("PublisherIntentService");
    }

    /**
     * Starts this service to perform action PublishData with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionPublishData(Context context) {

        Intent intent = new Intent(context, PublisherIntentService.class);
        intent.setAction(ACTION_PUBLISH_DATA);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_PUBLISH_DATA.equals(action)) {
                handleActionPublishData();
            }
        }
    }

    /**
     * Handle action PublishData in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPublishData() {
        Log.d( "Publisher", "Publisher service called");

        Context context = getApplicationContext();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);

        if(context != null) {

            try {

                final Gson gson = new Gson();
                Configuration configuration = new DatabaseManager(context).getConfigurationsTable().getRowForName(EXTRA_PUBLISH_DATA_SAMPLE_START_TIME);
                Date sampleStartDate = simpleDateFormat.parse(configuration.getValue());
                Date sampleEndDate = new Date(); // current time

                List<PeriodicMeasurement> periodicMeasurementRows = new DatabaseManager(context).getPeriodicMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);
                publishData(context, "PeriodicMeasurement", gson.toJson(periodicMeasurementRows), sampleStartDate, sampleEndDate);

                List<EventfulMeasurement> eventfulMeasurementRows = new DatabaseManager(context).getEventfulMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);
                publishData(context, "EventfulMeasurement", gson.toJson(eventfulMeasurementRows), sampleStartDate, sampleEndDate);

                List<OneTimeMeasurement> oneTimeMeasurementRows = new DatabaseManager(context).getOneTimeMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);
                publishData(context, "OneTimeMeasurement", gson.toJson(oneTimeMeasurementRows), sampleStartDate, sampleEndDate);

                List<PeriodicAggregatedMeasurement> periodicAggregatedMeasurementRows = new DatabaseManager(context).getPeriodicAggregatedMeasurementsTable().getAllRowsOlderThan(sampleEndDate);
                publishData(context, "PeriodicAggregatedMeasurement", gson.toJson(periodicAggregatedMeasurementRows), sampleStartDate, sampleEndDate);

                List<EventfulAggregatedMeasurement> eventfulAggregatedMeasurementRows = new DatabaseManager(context).getEventfulAggregatedMeasurementsTable().getAllRowsOlderThan(sampleEndDate);
                publishData(context, "EventfulAggregatedMeasurement", gson.toJson(eventfulAggregatedMeasurementRows), sampleStartDate, sampleEndDate);

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("Publisher", "Publisher service failed to publish data.");
            }

        }
    }

    private void publishData(final Context context, final String subtopic, final String payload, final Date sampleStartDate, final Date sampleEndDate) {

        // dates as trings
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);
        final String stringSampleEndDate = simpleDateFormat.format(sampleEndDate);

        final ConfigurationsTable configurationsTable = new DatabaseManager(context).getConfigurationsTable();

        // get the device's (as unique as possible) ID
        Configuration deviceIdConfiguration = null;
        try {
            deviceIdConfiguration = configurationsTable.getRowForName(MESSAGING_DEVICE_ID);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String deviceId;
        if( deviceIdConfiguration == null || deviceIdConfiguration.getValue() == null || deviceIdConfiguration.getValue().isEmpty()) {
            deviceId = updateDeviceId(context, configurationsTable);
        }
        else{
            deviceId = deviceIdConfiguration.getValue();
        }

        /*
         * Messages are delivered to a subtopic corresponding to the ObjectType of the payload,
         * this way we can still send data with little regard for the specifics of the
         * deserialization process on the other end.
         *
         * Ideal code:
         * <code> final String topic = configurationsTable.getRowForName(MESSAGING_SERVER_BASE_PUBLISH_TOPIC).getValue() + "/" + deviceId + "/" + subtopic; </code>
         *
         * _BUT_
         *
         * Kafka does not allow for subtopics, so we need to do everything at root level topics, in order to later bridge the content between
         * MQTT and Kafka brokers more easily. Thus, we
         */

        // prepare the message that will be sent
        final Gson gson = new Gson();
        DeviceMessage deviceMessage = new DeviceMessage(subtopic, deviceId, payload);
        final String deviceMessageContent = gson.toJson(deviceMessage);

        // prepare the topic's URL
        final String topic = configurationsTable.getRowForName(MESSAGING_SERVER_BASE_PUBLISH_TOPIC).getValue();
        String protocol = configurationsTable.getRowForName(MESSAGING_SERVER_PROTOCOL).getValue();
        String uri = configurationsTable.getRowForName(MESSAGING_SERVER_URI).getValue();
        String port = configurationsTable.getRowForName(MESSAGING_SERVER_PORT).getValue();

        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context,
                protocol + "://" + uri + ":" + port, deviceId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MQTT", "Publisher: Connection was lost");
                cause.printStackTrace();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("MQTT", "Publisher: Message Arrived: " + topic + ": " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MQTT", "Publisher: Delivery Complete");

                // update the record of the last successful data publish
                boolean editSuccess = configurationsTable.editRowForName(EXTRA_PUBLISH_DATA_SAMPLE_START_TIME, stringSampleEndDate);
                if (!editSuccess) {
                    Log.e("Publisher", "Publisher service failed to edit configuration \'"+ EXTRA_PUBLISH_DATA_SAMPLE_START_TIME +"\' row.");
                }else {
                    // delete sent data from database
                    deleteSentData(context, subtopic, sampleStartDate, sampleEndDate);
                }
            }
        });

        // setup configuration options for MQTT connection
        MqttConnectOptions options = new MqttConnectOptions();

        Configuration timeoutConfiguration = configurationsTable.getRowForName(MESSAGING_MQTT_TIMEOUT);
        if(timeoutConfiguration != null && timeoutConfiguration.getValue() != null ) {
            options.setConnectionTimeout(Integer.valueOf(timeoutConfiguration.getValue()));
        }

        Configuration keepAliveConfiguration = configurationsTable.getRowForName(MESSAGING_MQTT_KEEP_ALIVE);
        if(keepAliveConfiguration != null && keepAliveConfiguration.getValue() != null ) {
            options.setKeepAliveInterval(Integer.valueOf(keepAliveConfiguration.getValue()));
        }

        try {


            if(protocol.equalsIgnoreCase("ssl")) {
                // specific SSL configuration options
                String password = configurationsTable.getRowForName(MESSAGING_SERVER_PASSWORD).getValue();

                // TODO: also store the content of these files in the database? might be best...
                options.setSocketFactory(SslUtil.getSocketFactory(
                        getResources().openRawResource(R.raw.mosquittoorg),
                        getResources().openRawResource(R.raw.client_crt),
                        getResources().openRawResource(R.raw.client_key),
                        password));
            }

            try {
                // detail the actions that should follow a connection & start SSL connection
                mqttAndroidClient.connect(options, null, new IMqttActionListener() {

                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("MQTT", "Publisher: Connection Success");
                        try {
                            Log.d("MQTT", "Publisher: Subscribing to " + topic);
                            mqttAndroidClient.subscribe(topic, 0);
                            Log.d("MQTT", "Publisher: Subscribed to " + topic);

                            Log.d("MQTT", "Publisher: Publishing message...");
                            mqttAndroidClient.publish(topic, new MqttMessage(deviceMessageContent.getBytes()));

                        } catch (MqttException ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("MQTT", "Publisher: Connection Failure");
                        exception.printStackTrace();
                    }
                });
            } catch (MqttException ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteSentData(Context context, String subtopic, Date sampleStartDate, Date sampleEndDate) {

        boolean success;

        // delete the data for the specified time frame, from the corresponding table
        switch (subtopic){
            case "PeriodicMeasurement":
                success = new DatabaseManager(context).getPeriodicMeasurementsTable().deleteAllRowsBetween(sampleStartDate, sampleEndDate);
                break;

            case "EventfulMeasurement":
                success = new DatabaseManager(context).getEventfulMeasurementsTable().deleteAllRowsBetween(sampleStartDate, sampleEndDate);
                break;

            case "OneTimeMeasurement":
                success = new DatabaseManager(context).getOneTimeMeasurementsTable().deleteAllRowsBetween(sampleStartDate, sampleEndDate);
                break;

            case "PeriodicAggregatedMeasurement":
                success = new DatabaseManager(context).getPeriodicAggregatedMeasurementsTable().deleteAllRowsOlderThan(sampleEndDate);
                break;

            case "EventfulAggregatedMeasurement":
                success = new DatabaseManager(context).getEventfulAggregatedMeasurementsTable().deleteAllRowsOlderThan(sampleEndDate);
                break;

            default:
                success = false;
                Log.d("Publisher", "No matching table to delete published content.");
                break;
        }

        if(success){
            Log.d("Publisher", "Deleted published content.");
        }
        else{
            Log.d("Publisher", "Failed to delete published content.");
        }

    }

}
