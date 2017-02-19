package pt.uc.student.aclima.device_agent.Collectors.EventfulDataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class EventfulBroadcastReceiver extends BroadcastReceiver {

    public EventfulBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if (action.equals(Intent.ACTION_TIME_CHANGED)
                || action.equals(Intent.ACTION_DATE_CHANGED)
                || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

            EventfulIntentService.startActionTimeChange(context, new Date(System.currentTimeMillis()).toString());
        }
    }
}
