package pt.uc.student.aclima.device_agent.Collectors.OneTimeDataCollector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;

import static pt.uc.student.aclima.device_agent.Database.Entries.Measurement.DELIMITER;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static helper methods.
 * 
 * Collected Data:
 * - RAM
 */
public class OneTimeIntentService extends IntentService {

    private static final String EXTRA_PACKAGE_UID = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.extra.PACKAGE_UID";
    private static final String EXTRA_PACKAGE_NAME = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.extra.PACKAGE_NAME";
    private static final String EXTRA_PACKAGE_DATA_REMOVED = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.extra.PACKAGE_DATA_REMOVED";
    private static final String EXTRA_REPLACING_OTHER_PACKAGE = "pt.uc.student.aclima.terminal.Collectors.OneTimeIntentService.extra.REPLACING_OTHER_PACKAGE";

    public OneTimeIntentService() {
        super("PeriodicIntentService");
    }

    public static void startActionPackageChange(Context context, String action, int packageUID,
                                                String packageName, boolean isExtraDataRemoved,
                                                boolean isReplacingOtherPackage) {

        Intent intent = new Intent(context, OneTimeIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_PACKAGE_UID, packageUID);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        intent.putExtra(EXTRA_PACKAGE_DATA_REMOVED, isExtraDataRemoved);
        intent.putExtra(EXTRA_REPLACING_OTHER_PACKAGE, isReplacingOtherPackage);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (Intent.ACTION_PACKAGE_ADDED.equals(action)
                || Intent.ACTION_PACKAGE_CHANGED.equals(action) ) {

                int packageUID = intent.getIntExtra(EXTRA_PACKAGE_UID, -1);
                String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
                boolean isExtraDataRemoved = intent.getBooleanExtra(EXTRA_PACKAGE_DATA_REMOVED, false);
                boolean isReplacingOtherPackage = intent.getBooleanExtra(EXTRA_REPLACING_OTHER_PACKAGE, false);

                handleActionPackageChange(action, packageUID, packageName, isExtraDataRemoved, isReplacingOtherPackage);
            }
        }
    }

    private void handleActionPackageChange(String action, int packageUID, String packageName, boolean isExtraDataRemoved, boolean isReplacingOtherPackage) {
        Log.d( "PackageChange", "PackageChange" + DELIMITER + action + " service called");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            String unitsOfMeasurement = "packageUID" + DELIMITER + "packageName"
                    + DELIMITER + "isExtraDataRemoved" + DELIMITER + "isReplacingOtherPackage";
            String measurement = packageUID + DELIMITER + packageName
                    + DELIMITER + isExtraDataRemoved + DELIMITER + isReplacingOtherPackage;

            boolean success = new DatabaseManager(context).getOneTimeMeasurementsTable().addRow(
                    "PackageChange" + DELIMITER + action, measurement, unitsOfMeasurement, timestamp);
            if (!success) {
                Log.e("PackageChange", "PackageChange" + DELIMITER + action + " service failed to add row.");
            }
        }
    }


}
