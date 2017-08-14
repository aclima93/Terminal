package pt.uc.student.aclima.device_agent.Messaging.Updater;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Database.Entries.Configuration;
import pt.uc.student.aclima.device_agent.Database.Tables.ConfigurationsTable;
import pt.uc.student.aclima.device_agent.DeviceAgentAlarmManager;
import pt.uc.student.aclima.device_agent.Messaging.SslUtil;
import pt.uc.student.aclima.device_agent.R;

import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.EXTRA_MQTT_KEEP_ALIVE;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.EXTRA_MQTT_TIMEOUT;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_DEVICE_ID;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_BASE_UPDATE_TOPIC;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_PASSWORD;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_PORT;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_PROTOCOL;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_URI;
import static pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.updateDeviceId;

public class UpdaterIntentService extends IntentService {

    public static final String ACTION_UPDATE_CONFIGURATIONS = "pt.uc.student.aclima.device_agent.Messaging.Updater.UpdaterIntentService.action.UPDATE_CONFIGURATIONS";
    public static final String EXTRA_UPDATE_CONFIGURATIONS_TIME = "pt.uc.student.aclima.device_agent.Messaging.Updater.UpdaterIntentService.extra.UPDATE_CONFIGURATIONS_TIME";

    public UpdaterIntentService() {
        super("UpdaterIntentService");
    }

    /**
     * Starts this service to perform action UpdateConfigurations with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateConfigurations(Context context) {

        Intent intent = new Intent(context, UpdaterIntentService.class);
        intent.setAction(ACTION_UPDATE_CONFIGURATIONS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_UPDATE_CONFIGURATIONS.equals(action)) {
                handleActionUpdateConfigurations();
            }
        }
    }

    /**
     * Handle action UpdateConfigurations in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateConfigurations() {
        Log.d( "Updater", "Updater service called");

        Context context = getApplicationContext();

        if(context != null) {
            updateConfigurations(context);
        }
    }

    private void updateConfigurations(final Context context) {

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


        // prepare the topic's URL
        final String topic = configurationsTable.getRowForName(MESSAGING_SERVER_BASE_UPDATE_TOPIC).getValue() + "/" + deviceId;
        String protocol = configurationsTable.getRowForName(MESSAGING_SERVER_PROTOCOL).getValue();
        String uri = configurationsTable.getRowForName(MESSAGING_SERVER_URI).getValue();
        String port = configurationsTable.getRowForName(MESSAGING_SERVER_PORT).getValue();

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
                Log.d("MQTT", "Message Arrived: " + topic + ": " + message);

                // parse message config and update it
                String jsonConfigurationMessage = new String(message.getPayload());
                final Gson gson = new Gson();
                ConfigurationMessage configurationMessage = gson.fromJson(jsonConfigurationMessage, ConfigurationMessage.class);

                String configurationName = configurationMessage.getConfigurationName();
                String configurationValue = configurationMessage.getConfigurationValue();

                boolean editConfigurationSuccess = configurationsTable.editRowForName(configurationName, configurationValue);
                if (editConfigurationSuccess) {

                    /*
                     * Some configurations have alarms associated with them, and value changes
                     * call for the rescheduling of their respective alarms with the updated value.
                     */
                    DeviceAgentAlarmManager alarmScheduler = new DeviceAgentAlarmManager(context);
                    if(alarmScheduler.isActionManagedByAlarm(configurationName)){
                        // reschedule the alarm
                        alarmScheduler.rescheduleAlarm(configurationName, Long.parseLong(configurationValue));
                    }

                    // update the record of the last successful configuration update
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);
                    String stringDate = simpleDateFormat.format(new Date()); // current time
                    boolean editTimeSuccess = configurationsTable.editRowForName(EXTRA_UPDATE_CONFIGURATIONS_TIME, stringDate);
                    if (!editTimeSuccess) {
                        Log.e("Updater", "Updater service failed to edit configuration \'" + EXTRA_UPDATE_CONFIGURATIONS_TIME + "\' row.");
                    }
                }
                else {
                    Log.e("Updater", "Updater service failed to edit configuration \'"+ configurationMessage.getConfigurationName() +"\' row to value \'"+ configurationMessage.getConfigurationValue() +"\'.");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MQTT", "Delivery Complete");
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
                        Log.d("MQTT", "Connection Success");
                        try {
                            Log.d("MQTT", "Subscribing to " + topic);
                            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    Log.d("MQTT", "Successfully subscribed to topic " + topic);
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                    Log.d("MQTT", "Failed to subscribedto topic " + topic);
                                }
                            });

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

}
