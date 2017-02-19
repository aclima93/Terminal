package pt.uc.student.aclima.device_agent.Collectors.EventfulDataCollector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;

import static android.content.Intent.ACTION_DATE_CHANGED;
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

    private static final String EXTRA_TIME_CHANGED_PARAM1 = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.extra.TIME_CHANGED_PARAM1";

    public EventfulIntentService() {
        super("EventfulIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionTimeChange(Context context, String param1) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(ACTION_TIME_CHANGED);
        intent.putExtra(EXTRA_TIME_CHANGED_PARAM1, param1);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_TIME_CHANGED.equals(action)) {
                String param1 = intent.getStringExtra(EXTRA_TIME_CHANGED_PARAM1);
                handleActionTimeChange("TIME_CHANGED", param1);
            }
            else if (ACTION_DATE_CHANGED.equals(action)) {
                String param1 = intent.getStringExtra(EXTRA_TIME_CHANGED_PARAM1);
                handleActionTimeChange("DATE_CHANGED", param1);
            }
            else if (ACTION_TIMEZONE_CHANGED.equals(action)) {
                String param1 = intent.getStringExtra(EXTRA_TIME_CHANGED_PARAM1);
                handleActionTimeChange("TIMEZONE_CHANGED", param1);
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
}
