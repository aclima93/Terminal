package pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_BATTERY;
import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_CPU;
import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_CPU_USAGE;
import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_DATA_TRAFFIC;
import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_GPS;
import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_OPEN_PORTS;
import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_RAM;
import static pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService.ACTION_RAM_USAGE;

public class PeriodicBroadcastReceiver extends BroadcastReceiver {

    public PeriodicBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        final String action = intent.getAction();

        if (ACTION_RAM.equals(action)) {
            PeriodicIntentService.startActionRAM(context);
        }
        else if (ACTION_CPU.equals(action)) {
            PeriodicIntentService.startActionCPU(context);
        }
        else if (ACTION_GPS.equals(action)) {
            PeriodicIntentService.startActionGPS(context);
        }
        else if (ACTION_CPU_USAGE.equals(action)) {
            PeriodicIntentService.startActionCPUUsage(context);
        }
        else if (ACTION_RAM_USAGE.equals(action)) {
            PeriodicIntentService.startActionRAMUsage(context);
        }
        else if (ACTION_BATTERY.equals(action)) {
            PeriodicIntentService.startActionBattery(context);
        }
        else if (ACTION_OPEN_PORTS.equals(action)) {
            PeriodicIntentService.startActionOpenPorts(context);
        }
        else if (ACTION_DATA_TRAFFIC.equals(action)) {
            PeriodicIntentService.startActionDataTraffic(context);
        }

    }
}
