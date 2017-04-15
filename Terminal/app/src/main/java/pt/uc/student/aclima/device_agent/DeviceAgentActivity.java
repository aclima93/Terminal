package pt.uc.student.aclima.device_agent;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;

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
import pt.uc.student.aclima.device_agent.Publisher.PublisherBroadcastReceiver;
import pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService;

public class DeviceAgentActivity extends AppCompatActivity {

    final String[] permissionStrings = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE};

    final int WRITE_EXTERNAL_STORAGE_CODE = 0;
    final int READ_EXTERNAL_STORAGE_CODE = 1;
    final int ACCESS_FINE_LOCATION_CODE = 2;
    final int ACCESS_COARSE_LOCATION_CODE = 3;
    final int READ_PHONE_STATE_CODE = 4;

    final int[] permissionCodes = new int[]{
            WRITE_EXTERNAL_STORAGE_CODE,
            READ_EXTERNAL_STORAGE_CODE,
            ACCESS_FINE_LOCATION_CODE,
            ACCESS_COARSE_LOCATION_CODE,
            READ_PHONE_STATE_CODE};

    // so that we only perform the setup once, after all the permissions have been granted
    private boolean performedAdditionalSetup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_agent);

        // TODO: add Configuration Management Table, logic, period fetch, etc.

        checkPermissions();
    }

    private void checkPermissions() {

        /*
        WRITE_EXTERNAL_STORAGE
        READ_EXTERNAL_STORAGE
        ACCESS_FINE_LOCATION
        ACCESS_COARSE_LOCATION
        READ_PHONE_STATE
        */

        int grantedPermissionsCounter = 0;

        for(int i=0; i<permissionStrings.length; i++) {

            final String permissionString = permissionStrings[i];
            final int permissionCode = permissionCodes[i];

            if (ContextCompat.checkSelfPermission(getApplicationContext(), permissionString) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionString)) {

                    // Display a SnackBar with an explanation and a button to trigger the request.
                    Snackbar.make(findViewById(R.id.activity_device_agent),
                            "Please enable " + permissionString,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(DeviceAgentActivity.this,
                                            new String[]{permissionString}, permissionCode);
                                }
                            })
                            .show();

                } else {

                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{permissionString}, permissionCode);
                }
            }
            else{
                grantedPermissionsCounter++;
            }
        }

        if(grantedPermissionsCounter == permissionStrings.length){
            continueAppSetup();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == ACCESS_COARSE_LOCATION_CODE
                || requestCode == ACCESS_FINE_LOCATION_CODE
                || requestCode == READ_PHONE_STATE_CODE
                || requestCode == WRITE_EXTERNAL_STORAGE_CODE
                || requestCode == READ_EXTERNAL_STORAGE_CODE){

            /*
             * It doesn't matter if the user confirmed or not, we have to keep checking until all of the permissions are granted.
             * This app's nature requires this behaviour.
             */
            checkPermissions();

        }

    }

    /**
     * After all the permissions have been granted, we can continue the setup.
     */
    private void continueAppSetup(){

        if( performedAdditionalSetup ) {

            // performed only once per session
            performedAdditionalSetup = true;

            Context context = getApplicationContext();

            setupScreenBroadcastReceivers(context);

            setupTelephonyBroadcastReceivers(context);

            ConfigurationsTable configurationsTable = new DatabaseManager(context).getConfigurationsTable();

            schedulePeriodicAlarms(configurationsTable);

            schedulePeriodicAggregatorAlarm(configurationsTable);

            scheduleEventfulAggregatorAlarm(configurationsTable);

            schedulePublishAlarm(configurationsTable);
        }
    }

    private void setupScreenBroadcastReceivers(Context context) {

        /*
         * for some reason, ACTION_SCREEN_ON and OFF will not work if configured through the Manifest
         * so we have to add these actions to the BroadcastReceiver here
         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new EventfulBroadcastReceiver(), intentFilter);

    }

    private void setupTelephonyBroadcastReceivers(Context context) {

        // Base station
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new EventfulBroadcastReceiver().setupPhoneStateListener(context);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(new EventfulBroadcastReceiver());
    }

    private void schedulePublishAlarm(ConfigurationsTable configurationsTable) {

        Configuration configuration = configurationsTable.getRowForName(PublisherIntentService.ACTION_PUBLISH_DATA);
        scheduleAlarm(PublisherBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));
    }

    private void scheduleEventfulAggregatorAlarm(ConfigurationsTable configurationsTable) {

        Configuration configuration = configurationsTable.getRowForName(EventfulAggregatorIntentService.ACTION_AGGREGATE_EVENTFUL_DATA);
        scheduleAlarm(EventfulAggregatorBroadcastReceiver.class, configuration.getName(), Long.parseLong(configuration.getValue()));
    }

    private void schedulePeriodicAggregatorAlarm(ConfigurationsTable configurationsTable) {

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
