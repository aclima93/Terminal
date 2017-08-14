package pt.uc.student.aclima.device_agent.Messaging;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

import pt.uc.student.aclima.device_agent.Database.Tables.ConfigurationsTable;

import static pt.uc.student.aclima.device_agent.Database.Entries.Measurement.DELIMITER;

public class MessagingIntentServiceCommons {

    public static final String EXTRA_MQTT_TIMEOUT = "pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.extra.MQTT_TIMEOUT";
    public static final String EXTRA_MQTT_KEEP_ALIVE = "pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.extra.MQTT_KEEP_ALIVE";

    public static final String MESSAGING_DEVICE_ID = "pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_DEVICE_ID";
    public static final String MESSAGING_SERVER_PROTOCOL = "pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_PROTOCOL";
    public static final String MESSAGING_SERVER_URI = "pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_URI";
    public static final String MESSAGING_SERVER_PORT = "pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_PORT";
    public static final String MESSAGING_SERVER_BASE_PUBLISH_TOPIC = "pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_BASE_PUBLISH_TOPIC";
    public static final String MESSAGING_SERVER_BASE_UPDATE_TOPIC = "pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_BASE_UPDATE_TOPIC";
    public static final String MESSAGING_SERVER_PASSWORD = "pt.uc.student.aclima.device_agent.Messaging.MessagingIntentServiceCommons.MESSAGING_SERVER_PASSWORD";

    public static final String DEFAULT_SERVER_BASE_PUBLISH_TOPIC = "OryxInput";
    public static final String DEFAULT_SERVER_BASE_UPDATE_TOPIC = "Configurations";
    public static final String DEFAULT_SERVER_PASSWORD = "mosquitto";


    // MQTT + no encryption
    public static final String DEFAULT_SERVER_PROTOCOL = "tcp";
    public static final String DEFAULT_SERVER_URI = "188.37.251.133" /*"193.136.212.199"*/ /*"oryx.aclima.dei.uc.pt"*/; // Oryx machine - 10.3.2.9
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


    public static String updateDeviceId(Context context, ConfigurationsTable configurationsTable){

        String deviceId;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        deviceId = tm.getDeviceId();
        deviceId += DELIMITER + tm.getSimSerialNumber();
        deviceId += DELIMITER + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        boolean success = configurationsTable.addRow(MessagingIntentServiceCommons.MESSAGING_DEVICE_ID, deviceId, new Date());
        if (!success) {
            Log.e("Configuration", "Configuration" + DELIMITER + MessagingIntentServiceCommons.MESSAGING_DEVICE_ID + " service failed to add row.");
        }

        return deviceId;
    }

}
