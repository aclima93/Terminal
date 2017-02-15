package pt.uc.student.aclima.device_agent.Collectors.OneTimeDataCollector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static helper methods.
 * 
 * Collected Data:
 * - RAM
 * - CPU
 * - GPS
 * - CPU usage
 * - RAM usage
 * - Battery
 * - Open Ports
 * - Data Traffic
 */
public class OneTimeIntentService extends IntentService {

    public static final String ACTION_PACKAGE_ADDED = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_CHANGED = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.action.PACKAGE_CHANGED";
    public static final String ACTION_PACKAGE_REMOVED = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.action.PACKAGE_REMOVED";
    public static final String ACTION_PACKAGE_REPLACED = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.action.PACKAGE_REPLACED";

    private static final String EXTRA_PACKAGE_UID = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.extra.PACKAGE_UID";
    private static final String EXTRA_PACKAGE_NAME = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.extra.PACKAGE_NAME";

    public OneTimeIntentService() {
        super("PeriodicIntentService");
    }

    public static void startActionPackageAdded(Context context, int packageUID, String packageName) {

        Intent intent = new Intent(context, OneTimeIntentService.class);
        intent.setAction(ACTION_PACKAGE_ADDED);
        intent.putExtra(EXTRA_PACKAGE_UID, packageUID);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (action.equals(ACTION_PACKAGE_ADDED)) {
                handleActionRAM();
            }
        }
    }

    /**
     * Handle action RAM in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRAM() {
        Log.d( "RAM", "RAM service called");
    }


}
