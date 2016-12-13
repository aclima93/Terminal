package pt.uc.student.aclima.terminal.Aggregators.PeriodicDataAggregator;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static helper methods.
 */
public class PeriodicAggregatorIntentService extends IntentService {

    public static final int ACTION_AGGREGATE_PERIODIC_DATA_REQUEST_CODE = 1;
    public static final String ACTION_AGGREGATE_PERIODIC_DATA = "pt.uc.student.aclima.terminal.Aggregators.PeriodicAggregatorIntentService.action.AGGREGATE_PERIODIC_DATA";

    public PeriodicAggregatorIntentService() {
        super("PeriodicAggregatorIntentService");
    }

    /**
     * Starts this service to perform action AggregatePeriodicData with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionAggregatePeriodicData(Context context) {

        Intent intent = new Intent(context, PeriodicAggregatorIntentService.class);
        intent.setAction(ACTION_AGGREGATE_PERIODIC_DATA);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_AGGREGATE_PERIODIC_DATA.equals(action)) {
                handleActionAggregatePeriodicData();
            }
        }
    }

    /**
     * Handle action AggregatePeriodicData in the provided background thread with the provided
     * parameters.
     */
    private void handleActionAggregatePeriodicData() {
        // TODO: get all data from the database
        Log.d( "PeriodicAggregator", "Periodic Data Aggregator service called");

        //int numTopics = new DatabaseManager(this).getTopicsTableManager().countTopicsForSubjectId(subject.getId());
    }
}
