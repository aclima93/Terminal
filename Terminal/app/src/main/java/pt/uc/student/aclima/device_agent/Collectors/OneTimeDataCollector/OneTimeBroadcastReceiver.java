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

        if( action.equals(Intent.ACTION_PACKAGE_ADDED) ){

            Bundle bundle = intent.getExtras();

            int packageUID = bundle.getInt(Intent.EXTRA_UID);
            String packageName = bundle.getString(Intent.EXTRA_PACKAGE_NAME);

            OneTimeIntentService.startActionPackageAdded(context, packageUID, packageName);

        }
        else if( action.equals(Intent.ACTION_PACKAGE_CHANGED) ){

        }
        else if( action.equals(Intent.ACTION_PACKAGE_REMOVED) ){

        }
        else if( action.equals(Intent.ACTION_PACKAGE_REPLACED) ){

        }

    }
}