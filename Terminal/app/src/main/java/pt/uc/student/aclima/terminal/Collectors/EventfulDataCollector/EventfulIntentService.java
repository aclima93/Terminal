package pt.uc.student.aclima.terminal.Collectors.EventfulDataCollector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class EventfulIntentService extends IntentService {

    private static final String ACTION_TIME_CHANGED = "pt.uc.student.aclima.terminal.Collectors.EventfulIntentService.action.TIME_CHANGED";
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
    // TODO: Customize helper method
    public static void startActionTimeChanged(Context context, String param1) {
        Intent intent = new Intent(context, EventfulIntentService.class);
        intent.setAction(ACTION_TIME_CHANGED);
        intent.putExtra(EXTRA_TIME_CHANGED_PARAM1, param1);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_TIME_CHANGED)) {
                final String param1 = intent.getStringExtra(EXTRA_TIME_CHANGED_PARAM1);
                handleActionTimeChanged(param1);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionTimeChanged(String newTime) {
        Log.d("TIME_CHANGED", "TIME_CHANGED service called");
        Log.d(TAG, "changed time: " + newTime);
    }
}
