package pt.uc.student.aclima.device_agent.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Database.Entries.EventfulMeasurement;

/**
 * Created by aclima on 13/12/2016.
 */

public class EventfulMeasurementsTable extends MeasurementsTable {

    public static final String TABLE_NAME = "EventfulMeasurementsTable";

    public static final String VALUE = "value";
    public static final String UNITS_OF_MEASUREMENT = "units_of_measurement";
    public static final String TIMESTAMP = "timestamp";

    public EventfulMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public boolean addRow(String name, String value, String unitsOfMeasurement, Date timestamp){

        Log.d("addRow",
                "Adding row to table named " + TABLE_NAME + "\n" +
                        "name: " + name + "\n" +
                        "value: " + value + "\n" +
                        "unitsOfMeasurement: " + unitsOfMeasurement + "\n" +
                        "timestamp: " + timestamp.toString() + "\n"
        );

        boolean success;
        SQLiteDatabase database = databaseManager.getWritableDatabase();

        try {
            database.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, name);
            contentValues.put(VALUE, value);
            contentValues.put(UNITS_OF_MEASUREMENT, unitsOfMeasurement);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);
            String timestampString = simpleDateFormat.format(timestamp);
            contentValues.put(TIMESTAMP, timestampString);

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

    public ArrayList<EventfulMeasurement> getAllRows() {

        Log.d("getAllRows", "Getting rows from table named " + TABLE_NAME);

        ArrayList<EventfulMeasurement> rows = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase database = databaseManager.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        try {

            database.beginTransaction();

            EventfulMeasurement eventfulMeasurement;
            if (cursor.moveToFirst()) {
                do {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);
                    Date timestamp = simpleDateFormat.parse(cursor.getString(4));

                    eventfulMeasurement = new EventfulMeasurement(
                            cursor.getInt(0), // ID
                            cursor.getString(1), // NAME
                            cursor.getString(2), // VALUE
                            cursor.getString(3), // UNITS_OF_MEASUREMENT
                            timestamp); // TIMESTAMP

                    rows.add(eventfulMeasurement);

                } while (cursor.moveToNext());
            }

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

}
