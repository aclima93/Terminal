package pt.uc.student.aclima.device_agent.Collectors.EventfulDataCollector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.Date;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;

import static android.content.Intent.ACTION_BATTERY_LOW;
import static android.content.Intent.ACTION_BATTERY_OKAY;
import static android.content.Intent.ACTION_DATE_CHANGED;
import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;
import static android.content.Intent.ACTION_TIMEZONE_CHANGED;
import static android.content.Intent.ACTION_TIME_CHANGED;
import static pt.uc.student.aclima.device_agent.Database.Entries.Measurement.DELIMITER;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * Collected Data:
 * - Base station connection/disconnection
 * - Charging State
 * - Network (WiFi/3G) connection/disconnection
 * - Time and Time Zone changes
 * - Power On/Off
 */
public class EventfulIntentService extends IntentService {

    private static final String EXTRA_TIME_CHANGE_PARAM = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.TIME_CHANGE_PARAM";

    private static final String EXTRA_CHARGING_CHANGE_PARAM1 = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.CHARGING_CHANGE_PARAM1";
    private static final String EXTRA_CHARGING_CHANGE_PARAM2 = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.CHARGING_CHANGE_PARAM2";

    private static final String EXTRA_CONNECTION_CHANGE_PARAM1 = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.CONNECTION_CHANGE_PARAM1";
    private static final String EXTRA_CONNECTION_CHANGE_PARAM2 = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.CONNECTION_CHANGE_PARAM2";

    public EventfulIntentService() {
        super("EventfulIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionTimeChange(Context context, String action, String time) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_TIME_CHANGE_PARAM, time);
        context.startService(intent);
    }

    public static void startActionChargingChange(Context context, String action, boolean isCharging, String chargingMethod) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_CHARGING_CHANGE_PARAM1, isCharging);
        intent.putExtra(EXTRA_CHARGING_CHANGE_PARAM2, chargingMethod);
        context.startService(intent);
    }

    public static void startActionConnectionChange(Context context, String action, boolean isConnected, String connectedMethod) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_CONNECTION_CHANGE_PARAM1, isConnected);
        intent.putExtra(EXTRA_CONNECTION_CHANGE_PARAM2, connectedMethod);
        context.startService(intent);
    }

    public static void startActionScreenChange(Context context, String action) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void startActionPowerChange(Context context, String action) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_TIME_CHANGED.equals(action)) {
                String time = intent.getStringExtra(EXTRA_TIME_CHANGE_PARAM);
                handleActionTimeChange("TIME_CHANGED", time);
            }
            else if (ACTION_DATE_CHANGED.equals(action)) {
                String time = intent.getStringExtra(EXTRA_TIME_CHANGE_PARAM);
                handleActionTimeChange("DATE_CHANGED", time);
            }
            else if (ACTION_TIMEZONE_CHANGED.equals(action)) {
                String time = intent.getStringExtra(EXTRA_TIME_CHANGE_PARAM);
                handleActionTimeChange("TIMEZONE_CHANGED", time);
            }
            else if(ACTION_POWER_CONNECTED.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_CHARGING_CHANGE_PARAM1, false);
                String chargingMethod = intent.getStringExtra(EXTRA_CHARGING_CHANGE_PARAM2);
                handleActionChargingChange("POWER_CONNECTED", isCharging, chargingMethod);
            }
            else if(ACTION_POWER_DISCONNECTED.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_CHARGING_CHANGE_PARAM1, false);
                String chargingMethod = intent.getStringExtra(EXTRA_CHARGING_CHANGE_PARAM2);
                handleActionChargingChange("POWER_DISCONNECTED", isCharging, chargingMethod);
            }
            else if(ACTION_BATTERY_LOW.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_CHARGING_CHANGE_PARAM1, false);
                String chargingMethod = intent.getStringExtra(EXTRA_CHARGING_CHANGE_PARAM2);
                handleActionChargingChange("BATTERY_LOW", isCharging, chargingMethod);
            }
            else if(ACTION_BATTERY_OKAY.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_CHARGING_CHANGE_PARAM1, false);
                String chargingMethod = intent.getStringExtra(EXTRA_CHARGING_CHANGE_PARAM2);
                handleActionChargingChange("BATTERY_OKAY", isCharging, chargingMethod);
            }
            else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
                boolean isConnected = intent.getBooleanExtra(EXTRA_CONNECTION_CHANGE_PARAM1, false);
                String connectedMethod = intent.getStringExtra(EXTRA_CONNECTION_CHANGE_PARAM2);
                handleActionConnectionChange(isConnected, connectedMethod);
            }
            else if(Intent.ACTION_SCREEN_ON.equals(action)
                    || Intent.ACTION_SCREEN_OFF.equals(action)){
                handleActionScreenChange(action);
            }
            else if(Intent.ACTION_SHUTDOWN.equals(action)){
                handleActionPowerChange(action);
            }

        }
    }

    private void handleActionTimeChange(String event, String newTime) {
        Log.d("TimeChange", "TimeChange service called for event " + event);

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {
            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "TimeChange" + DELIMITER + event, newTime, "time", timestamp);
            if (!success) {
                Log.e("TimeChange", "TimeChange" + DELIMITER + event + "service failed to add row.");
            }
        }
    }

    private void handleActionChargingChange(String event, boolean isCharging, String chargingMethod) {
        Log.d("ChargingChange", "ChargingChange service called for event " + event);

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            String entryValue = "isCharging" + DELIMITER + isCharging + "\n"+
                    "chargingMethod" + DELIMITER + chargingMethod;

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "ChargingChange" + DELIMITER + event, entryValue, "", timestamp);
            if (!success) {
                Log.e("ChargingChange", "ChargingChange" + DELIMITER + event + "service failed to add row.");
            }
        }
    }


    private void handleActionConnectionChange(boolean isConnected, String connectedMethod) {
        Log.d("ConnectionChange", "ConnectionChange service called");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            String entryValue = "isConnected" + DELIMITER + isConnected + "\n"+
                    "connectedMethod" + DELIMITER + connectedMethod;

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "ConnectionChange", entryValue, "", timestamp);
            if (!success) {
                Log.e("ConnectionChange", "ConnectionChange service failed to add row.");
            }
        }
    }

    private void handleActionScreenChange(String action) {
        Log.d("ScreenChange", "ScreenChange service called");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "ScreenChange", action, "", timestamp);
            if (!success) {
                Log.e("ScreenChange", "ScreenChange service failed to add row.");
            }
        }
    }

    private void handleActionPowerChange(String action) {
        Log.d("PowerChange", "PowerChange service called");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            boolean success = new DatabaseManager(context).getEventfulMeasurementsTable().addRow(
                    "PowerChange", action, "", timestamp);
            if (!success) {
                Log.e("PowerChange", "PowerChange service failed to add row.");
            }
        }
    }

}
