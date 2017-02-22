package pt.uc.student.aclima.device_agent.Collectors.OneTimeDataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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

            OneTimeIntentService.startActionPackageChange(context, action, intent.getExtras());

        }

    }
}
