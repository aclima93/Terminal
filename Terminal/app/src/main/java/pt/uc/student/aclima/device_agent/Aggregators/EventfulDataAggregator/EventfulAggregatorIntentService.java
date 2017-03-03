package pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Database.Entries.Configuration;
import pt.uc.student.aclima.device_agent.Database.Entries.EventfulMeasurement;

import static pt.uc.student.aclima.device_agent.Database.Entries.Measurement.DELIMITER;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static helper methods.
 */
public class EventfulAggregatorIntentService extends IntentService {

    public static final String ACTION_AGGREGATE_EVENTFUL_DATA = "pt.uc.student.aclima.device_agent.Aggregators.EventfulAggregatorIntentService.action.AGGREGATE_EVENTFUL_DATA";
    public static final String EXTRA_AGGREGATE_EVENTFUL_DATA_SAMPLE_START_TIME = "pt.uc.student.aclima.device_agent.Aggregators.EventfulAggregatorIntentService.extra.AGGREGATE_EVENTFUL_DATA_SAMPLE_START_TIME";

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
        Log.d( "EventfulAggregator", "Eventful Data Aggregator service called");

        Context context = getApplicationContext();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);

        if(context != null) {

            try {

                DatabaseManager databaseManager = new DatabaseManager(context);

                Configuration configuration = databaseManager.getConfigurationsTable().getRowForName(EXTRA_AGGREGATE_EVENTFUL_DATA_SAMPLE_START_TIME);
                Date sampleStartDate = simpleDateFormat.parse(configuration.getValue());
                Date sampleEndDate = new Date(); // current time

                HashMap<String, List<EventfulMeasurement>> aggregationHashMap = new HashMap<>();
                List<EventfulMeasurement> allRows = databaseManager.getEventfulMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);

                // search each unique type of row name, aggregate them in a hashmap
                for(EventfulMeasurement eventfulMeasurement : allRows){
                    List<EventfulMeasurement> rowsForMeasurementType = aggregationHashMap.get(eventfulMeasurement.getName());

                    if(rowsForMeasurementType == null || rowsForMeasurementType.isEmpty()){
                        rowsForMeasurementType = new ArrayList<>();
                    }

                    rowsForMeasurementType.add(eventfulMeasurement);
                    aggregationHashMap.put(eventfulMeasurement.getName(), rowsForMeasurementType);
                }

                // count the number of measurements
                for(Map.Entry<String, List<EventfulMeasurement>> aggregationHashMapEntry : aggregationHashMap.entrySet()){

                    List<EventfulMeasurement> measurements = aggregationHashMapEntry.getValue();
                    int numberOfEvents = measurements.size();

                    boolean addSuccess = databaseManager.getEventfulAggregatedMeasurementsTable().addRow(
                            aggregationHashMapEntry.getKey(), sampleStartDate, sampleEndDate, numberOfEvents);
                    if (!addSuccess) {
                        Log.e("EventfulAggregator", "EventfulAggregator" + DELIMITER + aggregationHashMapEntry.getKey() + " service failed to add row.");
                    }
                    else{

                        boolean editSuccess = databaseManager.getConfigurationsTable().editRowForName(EXTRA_AGGREGATE_EVENTFUL_DATA_SAMPLE_START_TIME, simpleDateFormat.format(sampleEndDate));
                        if (!editSuccess) {
                            Log.e("EventfulAggregator", "EventfulAggregator service failed to edit configuration \'"+ EXTRA_AGGREGATE_EVENTFUL_DATA_SAMPLE_START_TIME +"\' row.");
                        }
                    }

                }

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("EventfulAggregator", "EventfulAggregator service failed to add row.");
            }

        }
    }
}
