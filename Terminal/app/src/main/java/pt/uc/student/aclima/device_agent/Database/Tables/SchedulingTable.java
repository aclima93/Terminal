package pt.uc.student.aclima.device_agent.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;
import pt.uc.student.aclima.device_agent.Database.Entries.SchedulingEntry;

/**
 * Created by aclima on 13/12/2016.
 */

public class SchedulingTable {

    public static final String TABLE_NAME = "SchedulingTable";

    protected DatabaseManager databaseManager;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String TIMESTAMP = "timestamp";

    public SchedulingTable(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }


    public boolean addRow(String name, String value, Date timestamp){

        Log.d("addRow",
                "Adding row to table named " + TABLE_NAME + "\n" +
                NAME + ": " + name + "\n" +
                VALUE + ": " + value + "\n" +
                TIMESTAMP + ": " + timestamp.toString() + "\n"
        );

        boolean success;
        SQLiteDatabase database = databaseManager.getWritableDatabase();

        try {
            database.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, name);
            contentValues.put(VALUE, value);

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

    public boolean editRowsForEntryName(String name, Integer value){

        boolean success = false;

        // TODO: edit row value

        return success;
    }

    public ArrayList<SchedulingEntry> getRowsForEntryName(String name) {

        Log.d("getRowsForEntryName", "Getting rows from table named " + TABLE_NAME + " where " + NAME + " like " + name);

        ArrayList<SchedulingEntry> rows = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME + " LIKE " + name ;

        SQLiteDatabase database = databaseManager.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        try {

            database.beginTransaction();

            rows = parseRowObjects(cursor);

            Log.d("getRowsForEntryName", "Got rows from table named " + TABLE_NAME + ".\nRows:\n" + rows.toString());

        }
        catch (Exception e){
            e.printStackTrace();

            Log.d("getRowsForEntryName", "Failed to get rows from table named " + TABLE_NAME + ".");
        }
        finally {
            cursor.close();
            database.endTransaction();
            database.close();
        }

        return rows;
    }

    private ArrayList<SchedulingEntry> parseRowObjects(Cursor cursor) throws ParseException {

        ArrayList<SchedulingEntry> rows = new ArrayList<>();
        SchedulingEntry schedulingEntry;
        if (cursor.moveToFirst()) {
            do {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);

                schedulingEntry = new SchedulingEntry(
                        cursor.getInt(0), // ID
                        cursor.getString(1), // NAME
                        cursor.getInt(2), // VALUE
                        simpleDateFormat.parse(cursor.getString(3)) // TIMESTAMP
                );
                rows.add(schedulingEntry);

            } while (cursor.moveToNext());
        }

        return rows;
    }

}
