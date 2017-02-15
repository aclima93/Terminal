package pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator;

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
public class EventfulAggregatorIntentService extends IntentService {

    public static final int ACTION_AGGREGATE_EVENTFUL_DATA_REQUEST_CODE = 1;
    public static final String ACTION_AGGREGATE_EVENTFUL_DATA = "pt.uc.student.aclima.terminal.Aggregators.EventfulAggregatorIntentService.action.AGGREGATE_EVENTFUL_DATA";

    public EventfulAggregatorIntentService() {
        super("EventfulAggregatorIntentService");
    }

    /**
     * Starts this service to perform action AggregateEventfulData with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionAggregateEventfulData(Context context) {

        Intent intent = new Intent(context, pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator.EventfulAggregatorIntentService.class);
        intent.setAction(ACTION_AGGREGATE_EVENTFUL_DATA);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_AGGREGATE_EVENTFUL_DATA.equals(action)) {
                handleActionAggregateEventfulData();
            }
        }
    }

    /**
     * Handle action AggregateEventfulData in the provided background thread with the provided
     * parameters.
     */
    private void handleActionAggregateEventfulData() {
        // TODO: get all data from the database
        Log.d( "EventfulAggregator", "Eventful Data Aggregator service called");

        //int numTopics = new DatabaseManager(this).getTopicsTableManager().countTopicsForSubjectId(subject.getId());
    }
}
