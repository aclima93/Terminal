package pt.uc.student.aclima.terminal.Database.Tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * Created by aclima on 06/10/16.
 */
public class EntriesTableManager {

    private DatabaseManager databaseManager;

    public static final String ENTRIES = "entries";

    public static final String ID = "id";
    public static final String TOPIC_ID = "topic_id";
    public static final String IMAGE = "image";
    public static final String QUESTION = "question";

    EntriesTableManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Transactional function to insert a entry in the local DB.
     *
     * @param entry object
     * @return success boolean
     */
    public boolean addEntries(Entry entry){

        Log.d("addEntries", "Adding entry to table...\nEntry:\n" + entry.toString());

        boolean success;
        SQLiteDatabase database = databaseManager.getWritableDatabase();

        try {
            database.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, entry.getId());
            contentValues.put(TOPIC_ID, entry.getId());
            contentValues.put(IMAGE, entry.getImage());
            contentValues.put(QUESTION, entry.getQuestion());

            database.insert(ENTRIES, IMAGE, contentValues);

            database.setTransactionSuccessful();
            success = true;

            Log.d("addEntries", "Added entry to table.");
        }
        catch (Exception e){
            e.printStackTrace();
            success = false;

            Log.d("addEntries", "Failed to add entry to table.");
        }
        finally {
            database.endTransaction();
        }

        return success;
    }

}
