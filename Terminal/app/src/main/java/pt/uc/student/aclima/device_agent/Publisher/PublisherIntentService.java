package pt.uc.student.aclima.device_agent.Publisher;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
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
import pt.uc.student.aclima.device_agent.R;

import static pt.uc.student.aclima.device_agent.Database.Entries.Measurement.DELIMITER;

public class PublisherIntentService extends IntentService {

    public static final String ACTION_PUBLISH_DATA = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.action.PUBLISH_DATA";
    public static final String EXTRA_PUBLISH_DATA_SAMPLE_START_TIME = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.extra.PUBLISH_DATA_SAMPLE_START_TIME";

    public static final String EXTRA_MQTT_TIMEOUT = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.extra.MQTT_TIMEOUT";
    public static final String EXTRA_MQTT_KEEP_ALIVE = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.extra.MQTT_KEEP_ALIVE";

    public static final String PUBLISH_DEVICE_ID = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.PUBLISH_DEVICE_ID";
    public static final String PUBLISH_SERVER_PROTOCOL = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.PUBLISH_SERVER_PROTOCOL";
    public static final String PUBLISH_SERVER_URI = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.PUBLISH_SERVER_URI";
    public static final String PUBLISH_SERVER_PORT = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.PUBLISH_SERVER_PORT";
    public static final String PUBLISH_SERVER_BASE_TOPIC = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.PUBLISH_SERVER_BASE_TOPIC";
    public static final String PUBLISH_SERVER_PASSWORD = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.PUBLISH_SERVER_PASSWORD";

    public static final String DEFAULT_SERVER_BASE_TOPIC = "/COLLECTED_DATA";
    public static final String DEFAULT_SERVER_PASSWORD = "mosquitto";


    // MQTT + no encryption
    public static final String DEFAULT_SERVER_PROTOCOL = "tcp";
    public static final String DEFAULT_SERVER_URI = "10.3.2.9"; // Oryx machine
    public static final String DEFAULT_SERVER_PORT = "1883";


    /*
    // MQTT + no encryption
    public static final String DEFAULT_SERVER_PROTOCOL = "tcp";
    public static final String DEFAULT_SERVER_URI = "test.mosquitto.org";
    public static final String DEFAULT_SERVER_PORT = "1883";
    */

    /*
    // MQTT + SSL
    public static final String DEFAULT_SERVER_PROTOCOL = "ssl";
    public static final String DEFAULT_SERVER_URI = "test.mosquitto.org";
    public static final String DEFAULT_SERVER_PORT = "8883";
    */

    /*
    public static final String DEFAULT_SERVER_PROTOCOL = "ssl";
    public static final String DEFAULT_SERVER_URI = "test.mosquitto.org";
    public static final String DEFAULT_SERVER_PORT = "8884";
    */

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
                String stringSampleEndDate = simpleDateFormat.format(sampleEndDate); // current time

                List<PeriodicMeasurement> periodicMeasurementRows = new DatabaseManager(context).getPeriodicMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);
                publishData(context, "PeriodicMeasurement", gson.toJson(periodicMeasurementRows), stringSampleEndDate);

                List<EventfulMeasurement> eventfulMeasurementRows = new DatabaseManager(context).getEventfulMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);
                publishData(context, "EventfulMeasurement", gson.toJson(eventfulMeasurementRows), stringSampleEndDate);

                List<OneTimeMeasurement> oneTimeMeasurementRows = new DatabaseManager(context).getOneTimeMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);
                publishData(context, "OneTimeMeasurement", gson.toJson(oneTimeMeasurementRows), stringSampleEndDate);

                List<PeriodicAggregatedMeasurement> periodicAggregatedMeasurementRows = new DatabaseManager(context).getPeriodicAggregatedMeasurementsTable().getAllRowsOlderThan(sampleEndDate);
                publishData(context, "PeriodicAggregatedMeasurement", gson.toJson(periodicAggregatedMeasurementRows), stringSampleEndDate);

                List<EventfulAggregatedMeasurement> eventfulAggregatedMeasurementRows = new DatabaseManager(context).getEventfulAggregatedMeasurementsTable().getAllRowsOlderThan(sampleEndDate);
                publishData(context, "EventfulAggregatedMeasurement", gson.toJson(eventfulAggregatedMeasurementRows), stringSampleEndDate);

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("Publisher", "Publisher service failed to publish data.");
            }

        }
    }

    private void publishData(Context context, final String subtopic, final String dataContent, final String stringSampleEndDate) {

        final ConfigurationsTable configurationsTable = new DatabaseManager(context).getConfigurationsTable();

        Configuration deviceIdConfiguration = null;
        try {
            deviceIdConfiguration = configurationsTable.getRowForName(PUBLISH_DEVICE_ID);
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
         */
        final String topic = configurationsTable.getRowForName(PUBLISH_SERVER_BASE_TOPIC).getValue() + "/" + subtopic + "/" + deviceId;
        String protocol = configurationsTable.getRowForName(PUBLISH_SERVER_PROTOCOL).getValue();
        String uri = configurationsTable.getRowForName(PUBLISH_SERVER_URI).getValue();
        String port = configurationsTable.getRowForName(PUBLISH_SERVER_PORT).getValue();

        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context,
                protocol + "://" + uri + ":" + port, deviceId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MQTT", "Connection was lost");
                cause.printStackTrace();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("MQTT", "Message Arrived: " + topic + ": " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MQTT", "Delivery Complete");

                // update the record of the last successful data publish
                boolean editSuccess = configurationsTable.editRowForName(EXTRA_PUBLISH_DATA_SAMPLE_START_TIME, stringSampleEndDate);
                if (!editSuccess) {
                    Log.e("Publisher", "Publisher service failed to edit configuration \'"+ EXTRA_PUBLISH_DATA_SAMPLE_START_TIME +"\' row.");
                }else {
                    // TODO: delete sent data from database
                }
            }
        });

        // setup configuration options for MQTT connection
        MqttConnectOptions options = new MqttConnectOptions();

        Configuration timeoutConfiguration = configurationsTable.getRowForName(EXTRA_MQTT_TIMEOUT);
        if(timeoutConfiguration != null && timeoutConfiguration.getValue() != null ) {
            options.setConnectionTimeout(Integer.valueOf(timeoutConfiguration.getValue()));
        }

        Configuration keepAliveConfiguration = configurationsTable.getRowForName(EXTRA_MQTT_KEEP_ALIVE);
        if(keepAliveConfiguration != null && keepAliveConfiguration.getValue() != null ) {
            options.setKeepAliveInterval(Integer.valueOf(keepAliveConfiguration.getValue()));
        }

        try {


            if(protocol.equalsIgnoreCase("ssl")) {
                // specific SSL configuration options
                String password = configurationsTable.getRowForName(PUBLISH_SERVER_PASSWORD).getValue();

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
                        Log.d("MQTT", "Connection Success");
                        try {
                            Log.d("MQTT", "Subscribing to " + topic);
                            mqttAndroidClient.subscribe(topic, 0);
                            Log.d("MQTT", "Subscribed to " + topic);

                            Log.d("MQTT", "Publishing message...");
                            mqttAndroidClient.publish(topic, new MqttMessage(dataContent.getBytes()));

                        } catch (MqttException ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("MQTT", "Connection Failure");
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

    private String updateDeviceId(Context context, ConfigurationsTable configurationsTable){

        String deviceId = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        deviceId += DELIMITER + tm.getDeviceId();
        deviceId += DELIMITER + tm.getSimSerialNumber();
        deviceId += DELIMITER + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        boolean success = configurationsTable.addRow(PublisherIntentService.PUBLISH_DEVICE_ID, deviceId, new Date());
        if (!success) {
            Log.e("Configuration", "Configuration" + DELIMITER + PublisherIntentService.PUBLISH_DEVICE_ID + " service failed to add row.");
        }

        return deviceId;
    }

}
