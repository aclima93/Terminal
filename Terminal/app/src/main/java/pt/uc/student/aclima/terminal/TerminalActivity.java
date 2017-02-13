package pt.uc.student.aclima.terminal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pt.uc.student.aclima.terminal.Aggregators.EventfulDataAggregator.EventfulAggregatorBroadcastReceiver;
import pt.uc.student.aclima.terminal.Aggregators.EventfulDataAggregator.EventfulAggregatorIntentService;
import pt.uc.student.aclima.terminal.Aggregators.PeriodicDataAggregator.PeriodicAggregatorBroadcastReceiver;
import pt.uc.student.aclima.terminal.Aggregators.PeriodicDataAggregator.PeriodicAggregatorIntentService;
import pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicBroadcastReceiver;
import pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicIntentService;

public class TerminalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collectors);

        // TODO: add Configuration Management Table, logic, period fetch, etc.

        schedulePeriodicAlarms();

        schedulePeriodicAggregatorAlarms();

        scheduleEventfulAggregatorAlarms();
    }

    private void scheduleEventfulAggregatorAlarms() {

        scheduleAlarm(EventfulAggregatorBroadcastReceiver.class,
                EventfulAggregatorIntentService.ACTION_AGGREGATE_EVENTFUL_DATA_REQUEST_CODE,
                EventfulAggregatorIntentService.ACTION_AGGREGATE_EVENTFUL_DATA,
                60 * 1000); // every minute
    }

    private void schedulePeriodicAggregatorAlarms() {
        scheduleAlarm(PeriodicAggregatorBroadcastReceiver.class,
                PeriodicAggregatorIntentService.ACTION_AGGREGATE_PERIODIC_DATA_REQUEST_CODE,
                PeriodicAggregatorIntentService.ACTION_AGGREGATE_PERIODIC_DATA,
                60 * 1000); // every minute
    }

    /**
     * Schedule the Periodic Data Collector Alarms
     * */
    private void schedulePeriodicAlarms() {

        // RAM
        scheduleAlarm(PeriodicBroadcastReceiver.class,
                PeriodicIntentService.ACTION_RAM_REQUEST_CODE,
                PeriodicIntentService.ACTION_RAM,
                5 * 1000);

        // CPU
        scheduleAlarm(PeriodicBroadcastReceiver.class,
                PeriodicIntentService.ACTION_CPU_REQUEST_CODE,
                PeriodicIntentService.ACTION_CPU,
                15 * 1000);

        // GPS
        scheduleAlarm(PeriodicBroadcastReceiver.class,
                PeriodicIntentService.ACTION_GPS_REQUEST_CODE,
                PeriodicIntentService.ACTION_GPS,
                15 * 1000);

        // CPU Usage
        scheduleAlarm(PeriodicBroadcastReceiver.class,
                PeriodicIntentService.ACTION_CPU_USAGE_REQUEST_CODE,
                PeriodicIntentService.ACTION_CPU_USAGE,
                15 * 1000);

        // RAM Usage
        scheduleAlarm(PeriodicBroadcastReceiver.class,
                PeriodicIntentService.ACTION_RAM_USAGE_REQUEST_CODE,
                PeriodicIntentService.ACTION_RAM_USAGE,
                15 * 1000);

        // Battery
        scheduleAlarm(PeriodicBroadcastReceiver.class,
                PeriodicIntentService.ACTION_BATTERY_REQUEST_CODE,
                PeriodicIntentService.ACTION_BATTERY,
                15 * 1000);

        // Open Ports
        scheduleAlarm(PeriodicBroadcastReceiver.class,
                PeriodicIntentService.ACTION_OPEN_PORTS_REQUEST_CODE,
                PeriodicIntentService.ACTION_OPEN_PORTS,
                15 * 1000);

        // Data Traffic
        scheduleAlarm(PeriodicBroadcastReceiver.class,
                PeriodicIntentService.ACTION_DATA_TRAFFIC_REQUEST_CODE,
                PeriodicIntentService.ACTION_DATA_TRAFFIC,
                15 * 1000);

    }

    // Setup a recurring alarm
    private void scheduleAlarm(Class receiverClass, int requestCode, String action, long intervalMillis) {

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), receiverClass);
        intent.setAction(action);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup periodic alarm every intervalMillis milliseconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // TODO: change to RTC, no wakeup?
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pendingIntent);
    }

    // if this is not called, intents will keep getting called, unless they are explicity killed
    // TODO: call on onDestroy? leave it running?
    private void cancelAlarm(Class receiverClass, int requestCode) {

        Intent intent = new Intent(getApplicationContext(), receiverClass);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }

}
