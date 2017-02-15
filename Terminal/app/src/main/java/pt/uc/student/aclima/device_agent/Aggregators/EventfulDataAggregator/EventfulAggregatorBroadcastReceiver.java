package pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator.EventfulAggregatorIntentService.ACTION_AGGREGATE_EVENTFUL_DATA;

public class EventfulAggregatorBroadcastReceiver extends BroadcastReceiver {

    public EventfulAggregatorBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if (action.equals(ACTION_AGGREGATE_EVENTFUL_DATA)) {
            EventfulAggregatorIntentService.startActionAggregateEventfulData(context);
        }

    }
}
