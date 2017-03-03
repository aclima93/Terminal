package pt.uc.student.aclima.device_agent.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Database.Entries.EventfulAggregatedMeasurement;

/**
 * Created by aclima on 13/12/2016.
 */

public class EventfulAggregatedMeasurementsTable extends AggregatedMeasurementsTable {

    public static final String TABLE_NAME = "EventfulAggregatedMeasurementsTable";

    public static final String NUMBER_OF_EVENTS = "number_of_events";

    public EventfulAggregatedMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public boolean addRow(String name, Date sampleStartTime, Date sampleEndTime, int numberOfEvents){

        Log.d("addRow",
                "Adding row to table named " + TABLE_NAME + "\n" +
                        NAME + ": " + name + "\n" +
                        SAMPLE_START_TIME + ": " + sampleStartTime.toString() + "\n" +
                        SAMPLE_END_TIME + ": " + sampleEndTime.toString() + "\n" +
                        NUMBER_OF_EVENTS + ": " + numberOfEvents + "\n"
        );

        boolean success;
        SQLiteDatabase database = databaseManager.getWritableDatabase();

        try {
            database.beginTransaction();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);

            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, name);
            contentValues.put(SAMPLE_START_TIME, simpleDateFormat.format(sampleStartTime));
            contentValues.put(SAMPLE_END_TIME, simpleDateFormat.format(sampleEndTime));
            contentValues.put(NUMBER_OF_EVENTS, numberOfEvents);

            database.insertOrThrow(TABLE_NAME, NAME, contentValues);

            database.setTransactionSuccessful();
            success = true;

            Log.d("addRow", "Added to table.");
        }
        catch (Exception e){
            e.printStackTrace();
            success = false;

            Log.d("addRow", "Failed to add to table.");
        }
        finally {
            database.endTransaction();
            database.close();
        }

        return success;
    }

    public List<EventfulAggregatedMeasurement> getAllRows() {

        Log.d("getAllRows", "Getting rows from table named " + TABLE_NAME);

        List<EventfulAggregatedMeasurement> rows = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase database = databaseManager.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        try {
            database.beginTransaction();
            rows = parseRowObjects(cursor);
            Log.d("getAllRows", "Got rows from table named " + TABLE_NAME + ".\nRows:\n" + rows.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("getAllRows", "Failed to get rows from table named " + TABLE_NAME + ".");
        }
        finally {
            cursor.close();
            database.endTransaction();
            database.close();
        }

        return rows;
    }

    public List<EventfulAggregatedMeasurement> getAllRowsOlderThan(Date endDate) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);
        String endDateString = simpleDateFormat.format(endDate);

        Log.d("getAllRowsBetween", "Getting rows from table named " + TABLE_NAME + " older than " + endDateString);

        List<EventfulAggregatedMeasurement> rows = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + SAMPLE_END_TIME + " < \'" + endDateString + "\'" ;

        SQLiteDatabase database = databaseManager.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        try {
            database.beginTransaction();
            rows = parseRowObjects(cursor);
            Log.d("getAllRowsBetween", "Got rows from table named " + TABLE_NAME + ".\nRows:\n" + rows.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("getAllRowsBetween", "Failed to get rows from table named " + TABLE_NAME + ".");
        }
        finally {
            cursor.close();
            database.endTransaction();
            database.close();
        }

        return rows;
    }

    private List<EventfulAggregatedMeasurement> parseRowObjects(Cursor cursor) throws ParseException {

        List<EventfulAggregatedMeasurement> rows = new ArrayList<>();
        EventfulAggregatedMeasurement eventfulAggregatedMeasurement;
        if (cursor.moveToFirst()) {
            do {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);

                eventfulAggregatedMeasurement = new EventfulAggregatedMeasurement(
                        cursor.getInt(0), // ID
                        cursor.getString(1), // NAME
                        simpleDateFormat.parse(cursor.getString(2)), // SAMPLE_START_TIME
                        simpleDateFormat.parse(cursor.getString(3)), // SAMPLE_END_TIME
                        cursor.getInt(4)); // NUMBER_OF_EVENTS

                rows.add(eventfulAggregatedMeasurement);

            } while (cursor.moveToNext());
        }

        return rows;
    }

}
