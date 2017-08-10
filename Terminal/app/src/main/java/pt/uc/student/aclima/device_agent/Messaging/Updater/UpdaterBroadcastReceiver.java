package pt.uc.student.aclima.device_agent.Messaging.Updater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pt.uc.student.aclima.device_agent.Messaging.Publisher.PublisherIntentService;

import static pt.uc.student.aclima.device_agent.Messaging.Updater.UpdaterIntentService.ACTION_UPDATE_CONFIGURATIONS;

public class UpdaterBroadcastReceiver extends BroadcastReceiver {

    public UpdaterBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if (action.equals(ACTION_UPDATE_CONFIGURATIONS)) {
            UpdaterIntentService.startActionUpdateConfigurations(context);
        }

    }
}
