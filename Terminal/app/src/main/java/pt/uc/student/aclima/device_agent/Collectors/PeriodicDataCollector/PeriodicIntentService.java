package pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector.Utilities.SingleShotLocationProvider;
import pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector.Utilities.traffic.TrafficMonitor;
import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector.Utilities.ProcessManager;

import static pt.uc.student.aclima.device_agent.Database.Entries.Measurement.DELIMITER;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * Collected Data:
 * - GPS
 * - Battery
 * - CPU usage
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
        Log.d("RAM", "RAM service called.");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            // get device RAM info
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow("Available System RAM", memoryInfo.availMem + "", "bytes", timestamp);
            if( !success){
                Log.e("RAM", "RAM service failed to add row.");
            }
        }
    }
    private void handleActionCPU() {
        Log.d("CPU", "CPU service called.");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            // Source <a>https://stackoverflow.com/questions/3118234/get-memory-usage-in-android</a>
            // Helpful <a>http://man7.org/linux/man-pages/man5/proc.5.html</a>
            try {
                RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
                String load = reader.readLine();

                String[] toks = load.split(" +");  // Split on one or more spaces

                long idle1 = Long.parseLong(toks[4]);
                long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                        + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

                // wait one second and re-read data
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }

                reader.seek(0);
                load = reader.readLine();
                reader.close();

                toks = load.split(" +");

                long idle2 = Long.parseLong(toks[4]);
                long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                        + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

                float entryValue = (float) 100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

                String entryName = "Used System CPU";
                boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                        entryName,
                        entryValue + "", "%", timestamp);
                if (!success) {
                    Log.e("CPU", "CPU service failed to add row for [" + entryName + "].");
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
    private void handleActionGPS() {
        Log.d("GPS", "GPS service called.");

        final Context context = getApplicationContext();
        final Date timestamp = new Date();

        if(context != null) {
            SingleShotLocationProvider.requestSingleUpdate(context,
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            String entryValue = location.toString();

                            String entryName = "GPS";
                            boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                                    entryName,
                                    entryValue, SingleShotLocationProvider.GPSCoordinates.unitsOfMeasurement, timestamp);
                            if (!success) {
                                Log.e("GPS", "GPS service failed to add row for [" + entryName + "].");
                            }

                        }
                    });
        }

    }
    private void handleActionCPUUsage() {
        Log.d("CPUUsage", "CPUUsage service called.");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            List<ProcessManager.Process> processes = ProcessManager.getRunningApps();

            for (ProcessManager.Process process : processes) {

                // Source <a>https://stackoverflow.com/questions/3118234/get-memory-usage-in-android</a>
                // Helpful <a>http://man7.org/linux/man-pages/man5/proc.5.html</a>
                try {
                    RandomAccessFile readerProcPidStat = new RandomAccessFile("/proc/" + process.pid + "/stat", "r");
                    String loadProcPidStat = readerProcPidStat.readLine();
                    String[] toksProcPidStat = loadProcPidStat.split(" +");  // Split on one or more spaces

                    RandomAccessFile readerProcUptime = new RandomAccessFile("/proc/uptime", "r");
                    String loadProcUptime = readerProcUptime.readLine();
                    String[] toksProcUptime = loadProcUptime.split(" +");  // Split on one or more spaces

                    long utime1 = Long.parseLong(toksProcPidStat[13]);
                    long stime1 = Long.parseLong(toksProcPidStat[14]);
                    long process_cpu_time1 = utime1 + stime1;

                    float system_uptime1 = Float.parseFloat(toksProcUptime[0]);

                    // wait one second and re-read data
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    readerProcPidStat.seek(0);
                    loadProcPidStat = readerProcPidStat.readLine();
                    readerProcPidStat.close();
                    toksProcPidStat = loadProcPidStat.split(" +");

                    readerProcUptime.seek(0);
                    loadProcUptime = readerProcUptime.readLine();
                    readerProcUptime.close();
                    toksProcUptime = loadProcUptime.split(" +");

                    long utime2 = Long.parseLong(toksProcPidStat[13]);
                    long stime2 = Long.parseLong(toksProcPidStat[14]);
                    long process_cpu_time2 = utime2 + stime2;

                    float system_uptime2 = Float.parseFloat(toksProcUptime[0]);

                    float entryValue = (float) 100 * (process_cpu_time2 - process_cpu_time1) / (system_uptime2 - system_uptime1);

                    // FIXME: ? almost always at 0

                    String entryName = "CPU Usage" + DELIMITER + "pid " + process.pid + DELIMITER + process.name;
                    boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                            entryName,
                            entryValue + "", "%", timestamp);
                    if (!success) {
                        Log.e("CPUUsage", "CPUUsage service failed to add row for [" + entryName + "].");
                    }


                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private void handleActionRAMUsage() {
        Log.d("RAMUsage", "RAMUsage service called.");

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

                    String entryName = "RAM Usage" + DELIMITER + "pid " + pids[0] + DELIMITER + pidMap.get(pids[0]);
                    boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                            entryName,
                            pidMemoryInfo.getTotalPss() + "", "Kilobytes", timestamp);
                    if( !success){
                        Log.e("RAMUsage", "RAMUsage service failed to add row for [" + entryName + "].");
                    }
                }
            }

        }
    }
    private void handleActionBattery() {
        Log.d("Battery", "Battery service called.");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            // this is a tricky intent and does not require a broadcastreceiver per se
            // Source: <a>https://developer.android.com/training/monitoring-device-state/battery-monitoring.html#MonitorLevel</a>
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = 100 * (level / (float) scale);

            boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                    "Battery Level", batteryPct + "", "%", timestamp);
            if (!success) {
                Log.e("Battery", "Battery service failed to add row.");
            }
        }


    }
    private void handleActionOpenPorts() {
        Log.d("OpenPorts", "OpenPorts service called.");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {

            Process process = null;
            try {
                process = Runtime.getRuntime().exec("netstat -a");
                process.getOutputStream().close();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String allLines = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    allLines += line + "\n";
                }

                boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                        "Open Ports", allLines, "", timestamp);
                if (!success) {
                    Log.e("OpenPorts", "OpenPorts service failed to add row.");
                }

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void handleActionDataTraffic() {
        Log.d("DataTraffic", "DataTraffic service called.");

        Context context = getApplicationContext();
        Date timestamp = new Date();

        if(context != null) {
            ArrayList<String> lines = new TrafficMonitor().takeSnapshot(context);
            String allLines = "";
            for (String line : lines) {
                allLines += line + "\n";
            }

            boolean success = new DatabaseManager(context).getPeriodicMeasurementsTable().addRow(
                    "Data Traffic", allLines, "", timestamp);
            if (!success) {
                Log.e("DataTraffic", "DataTraffic service failed to add row.");
            }
        }
    }

}
