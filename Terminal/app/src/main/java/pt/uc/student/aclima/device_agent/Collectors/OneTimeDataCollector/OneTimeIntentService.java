package pt.uc.student.aclima.device_agent.Collectors.OneTimeDataCollector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
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

    public OneTimeIntentService() {
        super("OneTimeIntentService");
    }

    public static void startActionPackageChange(Context context, String action, Bundle bundle) {

        Intent intent = new Intent(context, OneTimeIntentService.class);
        intent.setAction(action);
        intent.putExtras(bundle);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Bundle bundle = intent.getExtras();

            if (Intent.ACTION_PACKAGE_INSTALL.equals(action)) {

                Uri uri = intent.getData();

                String unitsOfMeasurement = "URI";
                String measurement = uri.toString();

                handleActionPackageChange(action, measurement, unitsOfMeasurement);
            }
            else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {

                int packageUID = bundle.getInt(Intent.EXTRA_UID);
                String packageName = getApplicationContext().getPackageManager().getNameForUid(packageUID);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    packageName = bundle.getString(Intent.EXTRA_PACKAGE_NAME);
                }

                boolean isReplacingOtherPackage = bundle.getBoolean(Intent.EXTRA_REPLACING);

                String unitsOfMeasurement = "packageUID" + DELIMITER + "packageName" + DELIMITER + "isReplacingOtherPackage";
                String measurement = packageUID + DELIMITER + packageName + DELIMITER + isReplacingOtherPackage;

                handleActionPackageChange(action, measurement, unitsOfMeasurement);
            }
            else if (Intent.ACTION_PACKAGE_CHANGED.equals(action) ) {

                String[] components = bundle.getStringArray(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST);
                boolean dontKillApp = bundle.getBoolean(Intent.EXTRA_DONT_KILL_APP);

                String unitsOfMeasurement = "components" + DELIMITER + "dontKillApp";
                String measurement = Arrays.toString(components) + DELIMITER + dontKillApp;

                handleActionPackageChange(action, measurement, unitsOfMeasurement);
            }
            else if (Intent.ACTION_PACKAGE_REMOVED.equals(action) ) {

                int packageUID = bundle.getInt(Intent.EXTRA_UID);
                String packageName = getApplicationContext().getPackageManager().getNameForUid(packageUID);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    packageName = bundle.getString(Intent.EXTRA_PACKAGE_NAME);
                }

                boolean isExtraDataRemoved = bundle.getBoolean(Intent.EXTRA_DATA_REMOVED);
                boolean isReplacingOtherPackage = bundle.getBoolean(Intent.EXTRA_REPLACING);

                String unitsOfMeasurement = "packageUID" + DELIMITER + "packageName" + DELIMITER + "isExtraDataRemoved" + DELIMITER + "isReplacingOtherPackage";
                String measurement = packageUID + DELIMITER + packageName + DELIMITER + isExtraDataRemoved + DELIMITER + isReplacingOtherPackage;

                handleActionPackageChange(action, measurement, unitsOfMeasurement);
            }
            else if (Intent.ACTION_PACKAGE_REPLACED.equals(action) ) {

                int packageUID = bundle.getInt(Intent.EXTRA_UID);
                String packageName = getApplicationContext().getPackageManager().getNameForUid(packageUID);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    packageName = bundle.getString(Intent.EXTRA_PACKAGE_NAME);
                }

                String unitsOfMeasurement = "packageUID" + DELIMITER + "packageName";
                String measurement = packageUID + DELIMITER + packageName;

                handleActionPackageChange(action, measurement, unitsOfMeasurement);
            }
            else if (Intent.ACTION_PACKAGE_FULLY_REMOVED.equals(action) ) {

                int packageUID = bundle.getInt(Intent.EXTRA_UID);
                String packageName = getApplicationContext().getPackageManager().getNameForUid(packageUID);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    packageName = bundle.getString(Intent.EXTRA_PACKAGE_NAME);
                }

                String unitsOfMeasurement = "packageUID" + DELIMITER + "packageName";
                String measurement = packageUID + DELIMITER + packageName;

                handleActionPackageChange(action, measurement, unitsOfMeasurement);
            }
        }
    }

    private void handleActionPackageChange(String action, String measurement, String unitsOfMeasurement) {
        Log.d( "PackageChange", "PackageChange" + DELIMITER + action + " service called");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            boolean success = new DatabaseManager(context).getOneTimeMeasurementsTable().addRow(
                    "PackageChange" + DELIMITER + action, measurement, unitsOfMeasurement, timestamp);
            if (!success) {
                Log.e("PackageChange", "PackageChange" + DELIMITER + action + " service failed to add row.");
            }
        }
    }


}
