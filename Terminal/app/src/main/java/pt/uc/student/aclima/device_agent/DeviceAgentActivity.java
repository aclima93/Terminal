package pt.uc.student.aclima.device_agent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator.EventfulAggregatorBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator.EventfulAggregatorIntentService;
import pt.uc.student.aclima.device_agent.Aggregators.PeriodicDataAggregator.PeriodicAggregatorBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Aggregators.PeriodicDataAggregator.PeriodicAggregatorIntentService;
import pt.uc.student.aclima.device_agent.Collectors.EventfulDataCollector.EventfulBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector.PeriodicBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector.PeriodicIntentService;
import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Database.Entries.Configuration;
import pt.uc.student.aclima.device_agent.Database.Tables.ConfigurationsTable;

public class DeviceAgentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_agent);

        // TODO: add Configuration Management Table, logic, period fetch, etc.

        Context context = getApplicationContext();

        setupSpecialEventfulBroadcastReceivers(context);

        ConfigurationsTable configurationsTable = new DatabaseManager(context).getConfigurationsTable();

        schedulePeriodicAlarms(configurationsTable);

        schedulePeriodicAggregatorAlarms(configurationsTable);

        scheduleEventfulAggregatorAlarms(configurationsTable);
    }

    private void setupSpecialEventfulBroadcastReceivers(Context context) {

        /*
         * for some reason, ACTION_SCREEN_ON and OFF will not work if configured through the Manifest
         * so we have to add these actions to the BroadcastReceiver here
         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new EventfulBroadcastReceiver(), intentFilter);

        // Base station
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new EventfulBroadcastReceiver().setupPhoneStateListener(context);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

    }

    private void scheduleEventfulAggregatorAlarms(ConfigurationsTable configurationsTable) {

        Configuration configuration = configurationsTable.getRowForName(EventfulAggregatorIntentService.ACTION_AGGREGATE_EVENTFUL_DATA);
        scheduleAlarm(EventfulAggregatorBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));
    }

    private void schedulePeriodicAggregatorAlarms(ConfigurationsTable configurationsTable) {

        Configuration configuration = configurationsTable.getRowForName(PeriodicAggregatorIntentService.ACTION_AGGREGATE_PERIODIC_DATA);
        scheduleAlarm(PeriodicAggregatorBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));
    }

    private void schedulePeriodicAlarms(ConfigurationsTable configurationsTable) {

        Configuration configuration;

        // RAM
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_RAM);
        scheduleAlarm(PeriodicBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));

        // CPU
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_CPU);
        scheduleAlarm(PeriodicBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));

        // GPS
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_GPS);
        scheduleAlarm(PeriodicBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));

        // CPU Usage
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_CPU_USAGE);
        scheduleAlarm(PeriodicBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));

        // RAM Usage
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_RAM_USAGE);
        scheduleAlarm(PeriodicBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));

        // Battery
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_BATTERY);
        scheduleAlarm(PeriodicBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));

        // Open Ports
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_OPEN_PORTS);
        scheduleAlarm(PeriodicBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));

        // Data Traffic
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_DATA_TRAFFIC);
        scheduleAlarm(PeriodicBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));

    }

    /**
     * Handle Alarms
     */

    // Setup a recurring alarm
    private void scheduleAlarm(Class receiverClass, String action, long intervalMillis) {

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), receiverClass);
        intent.setAction(action);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, action.hashCode(),
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
