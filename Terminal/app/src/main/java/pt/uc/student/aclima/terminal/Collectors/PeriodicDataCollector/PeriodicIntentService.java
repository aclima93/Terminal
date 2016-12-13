package pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

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

    public PeriodicIntentService() {
        super("PeriodicIntentService");
    }

    /**
     * Starts this service to perform action RAM with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRAM(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_RAM);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action CPU with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCPU(Context context) {

        Intent intent = new Intent(context, PeriodicIntentService.class);
        intent.setAction(ACTION_CPU);
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
        }
    }

    /**
     * Handle action RAM in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRAM() {
        Log.d( "RAM", "RAM service called");
        getRAMInfo();
    }

    /**
     * Handle action CPU in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCPU() {
        // TODO: Handle action CPU
        Log.d( "CPU", "CPU service called successfully");
    }

    public void getRAMInfo(){

        Context context = getApplicationContext();

        Date timestamp = new Date();

        if(context != null) {

            // get device RAM info
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            boolean success = new DatabaseManager(this).getPeriodicMeasurementsTable().addRow("available memory", memoryInfo.availMem + "", "bytes", timestamp);

            /*
            Log.i(TAG, " memoryInfo.availMem " + memoryInfo.availMem + "\n");
            Log.i(TAG, " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n");
            Log.i(TAG, " memoryInfo.threshold " + memoryInfo.threshold + "\n");

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
                    Log.i(TAG, String.format("** MEMINFO in pid %d [%s] **\n", pids[0], pidMap.get(pids[0])));
                    Log.i(TAG, " pidMemoryInfo.getTotalPrivateDirty(): " + pidMemoryInfo.getTotalPrivateDirty() + "\n");
                    Log.i(TAG, " pidMemoryInfo.getTotalPss(): " + pidMemoryInfo.getTotalPss() + "\n");
                    Log.i(TAG, " pidMemoryInfo.getTotalSharedDirty(): " + pidMemoryInfo.getTotalSharedDirty() + "\n");
                }
            }
            */

        }
    }
}
