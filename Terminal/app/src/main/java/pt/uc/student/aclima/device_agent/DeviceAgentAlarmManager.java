package pt.uc.student.aclima.device_agent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator.EventfulAggregatorBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator.EventfulAggregatorIntentService;
import pt.uc.student.aclima.device_agent.Aggregators.PeriodicDataAggregator.PeriodicAggregatorBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Aggregators.PeriodicDataAggregator.PeriodicAggregatorIntentService;
import pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector.PeriodicBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector.PeriodicIntentService;
import pt.uc.student.aclima.device_agent.Database.Entries.Configuration;
import pt.uc.student.aclima.device_agent.Database.Tables.ConfigurationsTable;
import pt.uc.student.aclima.device_agent.Messaging.Publisher.PublisherBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Messaging.Publisher.PublisherIntentService;
import pt.uc.student.aclima.device_agent.Messaging.Updater.UpdaterBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Messaging.Updater.UpdaterIntentService;

/**
 * Created by aclima on 14/08/2017.
 */

public class DeviceAgentAlarmManager {

    private Context context;

    public DeviceAgentAlarmManager(Context context) {
        this.context = context;
    }

    public Boolean isActionManagedByAlarm(String action){

        // if the action has a class that manges its alarm events
        if(alarmManagerClassForAction(action) != null){
            return true;
        }
        return false;
    }

    private Class alarmManagerClassForAction(String action){

        switch (action) {

            case PublisherIntentService.ACTION_PUBLISH_DATA:
                return PublisherBroadcastReceiver.class;

            case UpdaterIntentService.ACTION_UPDATE_CONFIGURATIONS:
             return UpdaterBroadcastReceiver.class;

            case EventfulAggregatorIntentService.ACTION_AGGREGATE_EVENTFUL_DATA:
                return EventfulAggregatorBroadcastReceiver.class;

            case PeriodicAggregatorIntentService.ACTION_AGGREGATE_PERIODIC_DATA:
                return PeriodicAggregatorBroadcastReceiver.class;

            case PeriodicIntentService.ACTION_RAM:
            case PeriodicIntentService.ACTION_CPU:
            case PeriodicIntentService.ACTION_GPS:
            case PeriodicIntentService.ACTION_CPU_USAGE:
            case PeriodicIntentService.ACTION_RAM_USAGE:
            case PeriodicIntentService.ACTION_BATTERY:
            case PeriodicIntentService.ACTION_OPEN_PORTS:
            case PeriodicIntentService.ACTION_DATA_TRAFFIC:
                return PeriodicBroadcastReceiver.class;

            default:
                Log.e("AlarmScheduler", "No class for action "+ action);
                return null;
        }

    }

    /**
     * Schedule Alarms
     */

    public void schedulePublishAlarm(ConfigurationsTable configurationsTable) {

        Configuration configuration = configurationsTable.getRowForName(PublisherIntentService.ACTION_PUBLISH_DATA);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));
    }

    public void scheduleUpdateAlarm(ConfigurationsTable configurationsTable) {

        Configuration configuration = configurationsTable.getRowForName(UpdaterIntentService.ACTION_UPDATE_CONFIGURATIONS);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));
    }

    public void scheduleEventfulAggregatorAlarm(ConfigurationsTable configurationsTable) {

        Configuration configuration = configurationsTable.getRowForName(EventfulAggregatorIntentService.ACTION_AGGREGATE_EVENTFUL_DATA);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));
    }

    public void schedulePeriodicAggregatorAlarm(ConfigurationsTable configurationsTable) {

        Configuration configuration = configurationsTable.getRowForName(PeriodicAggregatorIntentService.ACTION_AGGREGATE_PERIODIC_DATA);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));
    }

    public void schedulePeriodicAlarms(ConfigurationsTable configurationsTable) {

        Configuration configuration;

        // RAM
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_RAM);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));

        // CPU
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_CPU);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));

        // GPS
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_GPS);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));

        // CPU Usage
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_CPU_USAGE);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));

        // RAM Usage
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_RAM_USAGE);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));

        // Battery
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_BATTERY);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));

        // Open Ports
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_OPEN_PORTS);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));

        // Data Traffic
        configuration = configurationsTable.getRowForName(PeriodicIntentService.ACTION_DATA_TRAFFIC);
        scheduleAlarm(configuration.getName(), Long.parseLong(configuration.getValue()));
    }

    /**
     * Handle Alarms
     */

    // Setup a recurring alarm
    private void scheduleAlarm(String action, long intervalMillis) {

        // Construct an intent that will execute the AlarmReceiver
        Class receiverClass = alarmManagerClassForAction(action);
        Intent intent = new Intent(context, receiverClass);
        intent.setAction(action);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, action.hashCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup periodic alarm every intervalMillis milliseconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // TODO: change to RTC, no wakeup?
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pendingIntent);
    }

    // if this is not called, intents will keep getting called, unless they are explicity killed
    // TODO: call on onDestroy? leave it running?
    private void cancelAlarm(String action) {

        Class receiverClass = alarmManagerClassForAction(action);
        Intent intent = new Intent(context, receiverClass);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, action.hashCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }

    public void rescheduleAlarm(String action, long intervalMillis){
        cancelAlarm(action);
        scheduleAlarm(action, intervalMillis);
    }
    
}
