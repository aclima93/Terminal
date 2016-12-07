package pt.uc.student.aclima.terminal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pt.uc.student.aclima.terminal.Collectors.CollectorsReceiver;
import pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicDataCollector;

public class CollectorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collectors);

        scheduleAlarm(PeriodicDataCollector.ACTION_RAM_REQUEST_CODE, PeriodicDataCollector.ACTION_RAM, 5 * 1000);

        scheduleAlarm(PeriodicDataCollector.ACTION_CPU_REQUEST_CODE, PeriodicDataCollector.ACTION_CPU, 15 * 1000);
    }

    // Setup a recurring alarm every 5 seconds
    public void scheduleAlarm(int requestCode, String action, long intervalMillis) {

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), CollectorsReceiver.class);
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
    public void cancelAlarm(int requestCode) {
        Intent intent = new Intent(getApplicationContext(), CollectorsReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }

}
