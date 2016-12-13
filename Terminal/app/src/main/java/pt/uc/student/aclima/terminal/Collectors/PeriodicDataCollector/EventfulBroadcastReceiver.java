package pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_CPU;
import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_RAM;

public class EventfulBroadcastReceiver extends BroadcastReceiver {

    public EventfulBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if (action.equals(ACTION_RAM)) {
            PeriodicIntentService.startActionRAM(context);
        }
        else if (action.equals(ACTION_CPU)) {
            PeriodicIntentService.startActionCPU(context);
        }

    }
}
