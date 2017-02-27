package pt.uc.student.aclima.device_agent.Aggregators.PeriodicDataAggregator;

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

import pt.uc.student.aclima.device_agent.Aggregators.Utilities.MathUtils;
import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Database.Entries.Configuration;
import pt.uc.student.aclima.device_agent.Database.Entries.PeriodicMeasurement;

import static pt.uc.student.aclima.device_agent.Database.Entries.Measurement.DELIMITER;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class PeriodicAggregatorIntentService extends IntentService {

    public static final String ACTION_AGGREGATE_PERIODIC_DATA = "pt.uc.student.aclima.device_agent.Aggregators.PeriodicAggregatorIntentService.action.AGGREGATE_PERIODIC_DATA";
    public static final String EXTRA_AGGREGATE_PERIODIC_DATA_SAMPLE_START_TIME = "pt.uc.student.aclima.device_agent.Aggregators.PeriodicAggregatorIntentService.extra.AGGREGATE_PERIODIC_DATA_SAMPLE_START_TIME";

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
        Log.d( "PeriodicAggregator", "Periodic Data Aggregator service called");

        Context context = getApplicationContext();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);

        if(context != null) {

            try {
                Configuration configuration = new DatabaseManager(context).getConfigurationsTable().getRowForName(EXTRA_AGGREGATE_PERIODIC_DATA_SAMPLE_START_TIME);
                Date sampleStartDate = simpleDateFormat.parse(configuration.getValue());
                Date sampleEndDate = new Date(); // current time

                HashMap<String, List<PeriodicMeasurement>> aggregationHashMap = new HashMap<>();
                List<PeriodicMeasurement> allRows = new DatabaseManager(context).getPeriodicMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);

                // search each unique type of row name, aggregate them in a hashmap
                for(PeriodicMeasurement periodicMeasurement : allRows){
                    List<PeriodicMeasurement> rowsForMeasurementType = aggregationHashMap.get(periodicMeasurement.getName());

                    if(rowsForMeasurementType == null || rowsForMeasurementType.isEmpty()){
                        rowsForMeasurementType = new ArrayList<>();
                    }

                    rowsForMeasurementType.add(periodicMeasurement);
                    aggregationHashMap.put(periodicMeasurement.getName(), rowsForMeasurementType);
                }

                // perform the central tendency calculations
                for(Map.Entry<String, List<PeriodicMeasurement>> aggregationHashMapEntry : aggregationHashMap.entrySet()){

                    Double harmonicValue;
                    Double medianValue;
                    List<PeriodicMeasurement> measurements = aggregationHashMapEntry.getValue();
                    String unitsOfMeasurement = measurements.get(0).getUnitsOfMeasurement();

                    List<Double> values = new ArrayList<>();
                    for(PeriodicMeasurement periodicMeasurement : measurements){
                        values.add( Double.parseDouble(periodicMeasurement.getValue()) );
                    }

                    if(values.size() > 0) {
                        medianValue = MathUtils.median(values);
                        harmonicValue = MathUtils.harmonicMean(values);

                        boolean addSuccess = new DatabaseManager(context).getPeriodicAggregatedMeasurementsTable().addRow(
                                aggregationHashMapEntry.getKey(), sampleStartDate, sampleEndDate, harmonicValue, medianValue, unitsOfMeasurement);
                        if (!addSuccess) {
                            Log.e("PeriodicAggregator", "PeriodicAggregator" + DELIMITER + aggregationHashMapEntry.getKey() + " service failed to add row.");
                        }
                        else{

                            boolean editSuccess = new DatabaseManager(context).getConfigurationsTable().editRowForName(EXTRA_AGGREGATE_PERIODIC_DATA_SAMPLE_START_TIME, simpleDateFormat.format(sampleEndDate));
                            if (!editSuccess) {
                                Log.e("PeriodicAggregator", "PeriodicAggregator service failed to edit configuration \'"+ EXTRA_AGGREGATE_PERIODIC_DATA_SAMPLE_START_TIME +"\' row.");
                            }
                        }
                    }
                    else{
                        Log.d( "PeriodicAggregator", "No Periodic Data to Aggregate.");
                    }

                }

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("PeriodicAggregator", "PeriodicAggregator service failed to add row.");
            }

        }

    }
}
