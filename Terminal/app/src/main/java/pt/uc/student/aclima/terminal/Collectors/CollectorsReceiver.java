package pt.uc.student.aclima.terminal.Collectors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicDataCollector;

import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicDataCollector.ACTION_CPU;
import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicDataCollector.ACTION_RAM;

public class CollectorsReceiver extends BroadcastReceiver {

    public CollectorsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if (ACTION_RAM.equals(action)) {
            PeriodicDataCollector.startActionRAM(context);
        }
        else if (ACTION_CPU.equals(action)) {
            PeriodicDataCollector.startActionCPU(context);
        }
    }
}
