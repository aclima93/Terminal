package pt.uc.student.aclima.device_agent.Publisher;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Database.Entries.Configuration;
import pt.uc.student.aclima.device_agent.Database.Entries.EventfulAggregatedMeasurement;
import pt.uc.student.aclima.device_agent.Database.Entries.EventfulMeasurement;
import pt.uc.student.aclima.device_agent.Database.Entries.PeriodicAggregatedMeasurement;
import pt.uc.student.aclima.device_agent.Database.Entries.PeriodicMeasurement;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static helper methods.
 */
public class PublisherIntentService extends IntentService {

    public static final String ACTION_PUBLISH_DATA = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.action.PUBLISH_DATA";
    public static final String EXTRA_PUBLISH_DATA_SAMPLE_START_TIME = "pt.uc.student.aclima.device_agent.Publisher.PublisherIntentService.extra.PUBLISH_DATA_SAMPLE_START_TIME";

    public PublisherIntentService() {
        super("PublisherIntentService");
    }

    /**
     * Starts this service to perform action PublishData with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionPublishData(Context context) {

        Intent intent = new Intent(context, PublisherIntentService.class);
        intent.setAction(ACTION_PUBLISH_DATA);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_PUBLISH_DATA.equals(action)) {
                handleActionPublishData();
            }
        }
    }

    /**
     * Handle action PublishData in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPublishData() {
        Log.d( "Publisher", "Publisher service called");

        Context context = getApplicationContext();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);

        if(context != null) {

            try {
                Configuration configuration = new DatabaseManager(context).getConfigurationsTable().getRowForName(EXTRA_PUBLISH_DATA_SAMPLE_START_TIME);
                Date sampleStartDate = simpleDateFormat.parse(configuration.getValue());
                Date sampleEndDate = new Date(); // current time

                List<PeriodicMeasurement> periodicMeasurementRows = new DatabaseManager(context).getPeriodicMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);
                List<EventfulMeasurement> eventfulMeasurementRows = new DatabaseManager(context).getEventfulMeasurementsTable().getAllRowsBetween(sampleStartDate, sampleEndDate);

                List<PeriodicAggregatedMeasurement> periodicAggregatedMeasurementRows = new DatabaseManager(context).getPeriodicAggregatedMeasurementsTable().getAllRowsOlderThan(sampleEndDate);
                List<EventfulAggregatedMeasurement> eventfulAggregatedMeasurementRows = new DatabaseManager(context).getEventfulAggregatedMeasurementsTable().getAllRowsOlderThan(sampleEndDate);


                boolean editSuccess = new DatabaseManager(context).getConfigurationsTable().editRowForName(EXTRA_PUBLISH_DATA_SAMPLE_START_TIME, simpleDateFormat.format(sampleEndDate));
                if (!editSuccess) {
                    Log.e("Publisher", "Publisher service failed to edit configuration \'"+ EXTRA_PUBLISH_DATA_SAMPLE_START_TIME +"\' row.");
                }

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("Publisher", "Publisher service failed to publish data.");
            }

        }
    }
}
