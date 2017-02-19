package pt.uc.student.aclima.device_agent.Aggregators.PeriodicDataAggregator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static pt.uc.student.aclima.device_agent.Aggregators.PeriodicDataAggregator.PeriodicAggregatorIntentService.ACTION_AGGREGATE_PERIODIC_DATA;

public class PeriodicAggregatorBroadcastReceiver extends BroadcastReceiver {

    public PeriodicAggregatorBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if (action.equals(ACTION_AGGREGATE_PERIODIC_DATA)) {
            PeriodicAggregatorIntentService.startActionAggregatePeriodicData(context);
        }

    }
}
