package pt.uc.student.aclima.device_agent.Collectors.EventfulDataCollector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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

    private static final String EXTRA_POWER_CHANGE_PARAM1 = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.POWER_CHANGE_PARAM1";
    private static final String EXTRA_POWER_CHANGE_PARAM2 = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.POWER_CHANGE_PARAM2";
    private static final String EXTRA_POWER_CHANGE_PARAM3 = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.POWER_CHANGE_PARAM3";
    private static final String EXTRA_POWER_CHANGE_PARAM4 = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.POWER_CHANGE_PARAM4";

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

    public static void startActionPowerChange(Context context, String action, boolean isCharging, boolean chargingUSB, boolean chargingAC, boolean chargingWireless) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_POWER_CHANGE_PARAM1, isCharging);
        intent.putExtra(EXTRA_POWER_CHANGE_PARAM2, chargingUSB);
        intent.putExtra(EXTRA_POWER_CHANGE_PARAM3, chargingAC);
        intent.putExtra(EXTRA_POWER_CHANGE_PARAM4, chargingWireless);
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
                boolean isCharging = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM1, false);
                boolean chargingUSB = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM2, false);
                boolean chargingAC = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM3, false);
                boolean chargingWireless = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM4, false);
                handleActionPowerChange("POWER_CONNECTED", isCharging, chargingUSB, chargingAC, chargingWireless);
            }
            else if(ACTION_POWER_DISCONNECTED.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM1, false);
                boolean chargingUSB = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM2, false);
                boolean chargingAC = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM3, false);
                boolean chargingWireless = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM4, false);
                handleActionPowerChange("POWER_DISCONNECTED", isCharging, chargingUSB, chargingAC, chargingWireless);
            }
            else if(ACTION_BATTERY_LOW.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM1, false);
                boolean chargingUSB = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM2, false);
                boolean chargingAC = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM3, false);
                boolean chargingWireless = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM4, false);
                handleActionPowerChange("BATTERY_LOW", isCharging, chargingUSB, chargingAC, chargingWireless);
            }
            else if(ACTION_BATTERY_OKAY.equals(action)){
                boolean isCharging = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM1, false);
                boolean chargingUSB = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM2, false);
                boolean chargingAC = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM3, false);
                boolean chargingWireless = intent.getBooleanExtra(EXTRA_POWER_CHANGE_PARAM4, false);
                handleActionPowerChange("BATTERY_OKAY", isCharging, chargingUSB, chargingAC, chargingWireless);
            }

        }
    }
    private void handleActionTimeChange(String event, String newTime) {
        Log.d("TimeChange", "TimeChange service called for event " + event);

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {
            boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                    "TimeChange" + DELIMITER + event, newTime, "time", timestamp);
            if (!success) {
                Log.e("TimeChange", "TimeChange" + DELIMITER + event + "service failed to add row.");
            }
        }
    }

    private void handleActionPowerChange(String event, boolean isCharging, boolean chargingUSB, boolean chargingAC, boolean chargingWireless) {
        Log.d("PowerChange", "PowerChange service called for event " + event);

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            String entryValue = "isCharging" + DELIMITER + isCharging + "\n"+
                    "chargingUSB" + DELIMITER + chargingUSB + "\n"+
                    "chargingAC" + DELIMITER + chargingAC + "\n"+
                    "chargingWireless" + DELIMITER + chargingWireless;

            boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                    "PowerChange" + DELIMITER + event, entryValue, "booleans", timestamp);
            if (!success) {
                Log.e("PowerChange", "PowerChange" + DELIMITER + event + "service failed to add row.");
            }
        }
    }
}
