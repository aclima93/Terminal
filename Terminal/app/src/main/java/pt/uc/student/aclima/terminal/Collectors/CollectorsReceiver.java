package pt.uc.student.aclima.terminal.Collectors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import pt.uc.student.aclima.terminal.Collectors.EventfulDataCollector.EventfulDataCollector;
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

        if (action.equals(ACTION_RAM)) {
            PeriodicDataCollector.startActionRAM(context);
        }
        else if (action.equals(ACTION_CPU)) {
            PeriodicDataCollector.startActionCPU(context);
        }
        else if (action.equals(Intent.ACTION_TIME_CHANGED)
                || action.equals(Intent.ACTION_DATE_CHANGED)
                || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

            EventfulDataCollector.startActionTimeChanged(context, new Date(System.currentTimeMillis()).toString());
        }
    }
}
