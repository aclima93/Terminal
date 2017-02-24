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
import pt.uc.student.aclima.device_agent.Database.Entries.Configuration;

/**
 * Created by aclima on 13/12/2016.
 */

public class ConfigurationsTable {

    public static final String TABLE_NAME = "ConfigurationsTable";

    private DatabaseManager databaseManager;

    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String TIMESTAMP = "timestamp";

    public ConfigurationsTable(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean shouldAddRowForName(String name){
        if( getRowForName(name) == null ){
            return true;
        }
        return false;
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

    public boolean editRowForName(String name, String value){

        boolean success = false;

        // TODO: edit row value

        return success;
    }

    public Configuration getRowForName(String name) {

        Log.d("getRowForName", "Getting rows from table named " + TABLE_NAME + " where " + NAME + " like " + name);

        Configuration row = null;

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME + " LIKE \'" + name + "\'" ;

        SQLiteDatabase database = databaseManager.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        try {

            database.beginTransaction();

            row = parseRowObjects(cursor).get(0); // entries are unique

            Log.d("getRowForName", "Got rows from table named " + TABLE_NAME + ".\nRows:\n" + row.toString());

        }
        catch (Exception e){
            e.printStackTrace();

            Log.d("getRowForName", "Failed to get rows from table named " + TABLE_NAME + ".");
        }
        finally {
            cursor.close();
            database.endTransaction();
            database.close();
        }

        return row;
    }

    private List<Configuration> parseRowObjects(Cursor cursor) throws ParseException {

        List<Configuration> rows = new ArrayList<>();
        Configuration configuration;
        if (cursor.moveToFirst()) {
            do {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);

                configuration = new Configuration(
                        cursor.getString(0), // NAME
                        cursor.getString(1), // VALUE
                        simpleDateFormat.parse(cursor.getString(2)) // TIMESTAMP
                );
                rows.add(configuration);

            } while (cursor.moveToNext());
        }

        return rows;
    }

}
