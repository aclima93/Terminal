package pt.uc.student.aclima.device_agent.Collectors.OneTimeDataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class OneTimeBroadcastReceiver extends BroadcastReceiver {

    public OneTimeBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if( action.equals(Intent.ACTION_PACKAGE_INSTALL)
                || action.equals(Intent.ACTION_PACKAGE_ADDED)
                || action.equals(Intent.ACTION_PACKAGE_CHANGED)
                || action.equals(Intent.ACTION_PACKAGE_REMOVED)
                || action.equals(Intent.ACTION_PACKAGE_REPLACED)
                || action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED) ){

            Bundle bundle = intent.getExtras();

            int packageUID = bundle.getInt(Intent.EXTRA_UID);
            String packageName = context.getPackageManager().getNameForUid(packageUID);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                packageName = bundle.getString(Intent.EXTRA_PACKAGE_NAME);
            }

            boolean isExtraDataRemoved = bundle.getBoolean(Intent.EXTRA_DATA_REMOVED);
            boolean isReplacingOtherPackage = bundle.getBoolean(Intent.EXTRA_REPLACING);

            OneTimeIntentService.startActionPackageChange(context, action, packageUID, packageName, isExtraDataRemoved, isReplacingOtherPackage);

        }

    }
}
