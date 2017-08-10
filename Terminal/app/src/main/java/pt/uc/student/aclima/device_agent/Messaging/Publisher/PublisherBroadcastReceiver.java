package pt.uc.student.aclima.device_agent.Messaging.Publisher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static pt.uc.student.aclima.device_agent.Messaging.Publisher.PublisherIntentService.ACTION_PUBLISH_DATA;

public class PublisherBroadcastReceiver extends BroadcastReceiver {

    public PublisherBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if (action.equals(ACTION_PUBLISH_DATA)) {
            PublisherIntentService.startActionPublishData(context);
        }

    }
}
