package pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static helper methods.
 * 
 * Collected Data:
 * - RAM
 * - CPU
 * - GPS
 * - CPU usage
 * - RAM usage
 * - Battery
 * - Open Ports
 * - Data Traffic
 */
public class PeriodicIntentService extends IntentService {

    public static final int ACTION_RAM_REQUEST_CODE = 1;
    public static final String ACTION_RAM = "pt.uc.student.aclima.terminal.Collectors.PeriodicIntentService.action.RAM";

    public static final int ACTION_CPU_REQUEST_CODE = 2;
    public static final String ACTION_CPU = "pt.uc.student.aclima.terminal.Collectors.PeriodicIntentService.action.CPU";

    public static final int ACTION_GPS_REQUEST_CODE = 3;
    public static final String ACTION_GPS = "pt.uc.student.aclima.terminal.Collectors.PeriodicIntentService.action.GPS";

    public static final int ACTION_CPU_USAGE_REQUEST_CODE = 4;
    public static final String ACTION_CPU_USAGE = "pt.uc.student.aclima.terminal.Collectors.PeriodicIntentService.action.CPU_USAGE";

    public static final int ACTION_RAM_USAGE_REQUEST_CODE = 5;
    public static final String ACTION_RAM_USAGE = "pt.uc.student.aclima.terminal.Collectors.PeriodicIntentService.action.RAM_USAGE";

    public static final int ACTION_BATTERY_REQUEST_CODE = 6;
    public static final String ACTION_BATTERY = "pt.uc.student.aclima.terminal.Collectors.PeriodicIntentService.action.BATTERY";

    public static final int ACTION_OPEN_PORTS_REQUEST_CODE = 7;
    public static final String ACTION_OPEN_PORTS = "pt.uc.student.aclima.terminal.Collectors.PeriodicIntentService.action.OPEN_PORTS";

    public static final int ACTION_DATA_TRAFFIC_REQUEST_CODE = 8;
    public static final String ACTION_DATA_TRAFFIC = "pt.uc.student.aclima.terminal.Collectors.PeriodicIntentService.action.DATA_TRAFFIC";

    public PeriodicIntentService() {
        super("PeriodicIntentService");
    }

    /**
     * The following methods start this service to perform action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionRAM(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_RAM);
        context.startService(intent);
    }
    public static void startActionCPU(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_CPU);
        context.startService(intent);
    }
    public static void startActionGPS(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_GPS);
        context.startService(intent);
    }
    public static void startActionCPUUsage(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_CPU_USAGE);
        context.startService(intent);
    }
    public static void startActionRAMUsage(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_RAM_USAGE);
        context.startService(intent);
    }
    public static void startActionBattery(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_BATTERY);
        context.startService(intent);
    }
    public static void startActionOpenPorts(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_OPEN_PORTS);
        context.startService(intent);
    }
    public static void startActionDataTraffic(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_DATA_TRAFFIC);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_RAM.equals(action)) {
                handleActionRAM();
            }
            else if (ACTION_CPU.equals(action)) {
                handleActionCPU();
            }
            else if (ACTION_GPS.equals(action)) {
                handleActionGPS();
            }
            else if (ACTION_CPU_USAGE.equals(action)) {
                handleActionCPUUsage();
            }
            else if (ACTION_RAM_USAGE.equals(action)) {
                handleActionRAMUsage();
            }
            else if (ACTION_BATTERY.equals(action)) {
                handleActionBattery();
            }
            else if (ACTION_OPEN_PORTS.equals(action)) {
                handleActionOpenPorts();
            }
            else if (ACTION_DATA_TRAFFIC.equals(action)) {
                handleActionDataTraffic();
            }
        }
    }

    /**
     * The following methods handle actions in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRAM() {
        Log.d( "RAM", "RAM service called.");

        Context context = getApplicationContext();

        Date timestamp = new Date();

        if(context != null) {

            // get device RAM info
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow("available memory", memoryInfo.availMem + "", "bytes", timestamp);
            if( !success){
                Log.e( "RAM", "RAM service failed to add row.");
            }
        }
    }
    private void handleActionCPU() {
        // TODO: Handle action CPU
        Log.d( "CPU", "CPU service called.");
    }
    private void handleActionGPS() {
        // TODO: Handle action GPS
        Log.d( "GPS", "GPS service called.");
    }
    private void handleActionCPUUsage() {
        // TODO: Handle action CPU Usage
        Log.d( "CPUUsage", "CPUUsage service called.");
    }
    private void handleActionRAMUsage() {
        Log.d( "RAMUsage", "RAMUsage service called.");

        Context context = getApplicationContext();

        Date timestamp = new Date();

        if(context != null) {

            // get device RAM info
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            // get RAM info of running app processes
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            Map<Integer, String> pidMap = new TreeMap<Integer, String>();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {

                pidMap.put(runningAppProcessInfo.pid, runningAppProcessInfo.processName);
            }

            Collection<Integer> keys = pidMap.keySet();
            for (int key : keys) {

                int pids[] = new int[1];
                pids[0] = key;
                android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pids);

                for (android.os.Debug.MemoryInfo pidMemoryInfo : memoryInfoArray) {

                    String entryName = "available memory in pid " + pids[0] + " - " + pidMap.get(pids[0]);
                    boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                            entryName,
                            pidMemoryInfo.getTotalPss() + "", "Kilobytes", timestamp);
                    if( !success){
                        Log.e( "RAMUsage", "RAMUsage service failed to add row for [" + entryName + "].");
                    }
                }
            }

        }
    }
    private void handleActionBattery() {
        // TODO: Handle action Battery
        Log.d( "Battery", "Battery service called.");
    }
    private void handleActionOpenPorts() {
        // TODO: Handle action OpenPorts
        Log.d( "OpenPorts", "OpenPorts service called.");
    }
    private void handleActionDataTraffic() {
        // TODO: Handle action DataTraffic
        Log.d( "DataTraffic", "DataTraffic service called.");
    }

}
