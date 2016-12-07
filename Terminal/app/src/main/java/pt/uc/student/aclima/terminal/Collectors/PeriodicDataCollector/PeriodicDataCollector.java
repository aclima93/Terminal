package pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
public class PeriodicDataCollector extends IntentService {
    public static final int ACTION_RAM_REQUEST_CODE = 1;
    public static final String ACTION_RAM = "pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.action.RAM";

    public static final int ACTION_CPU_REQUEST_CODE = 2;
    public static final String ACTION_CPU = "pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.action.CPU";

    public PeriodicDataCollector() {
        super("PeriodicDataCollector");
    }

    /**
     * Starts this service to perform action RAM with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionRAM(Context context) {
        Intent intent = new Intent(context, PeriodicDataCollector.class);
        intent.setAction(ACTION_RAM);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action CPU with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionCPU(Context context) {
        Intent intent = new Intent(context, PeriodicDataCollector.class);
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
        // TODO: Handle action RAM
        Log.i( "RAM", "RAM service called successfully");
    }

    /**
     * Handle action CPU in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCPU() {
        // TODO: Handle action CPU
        Log.i( "CPU", "CPU service called successfully");
    }
}
