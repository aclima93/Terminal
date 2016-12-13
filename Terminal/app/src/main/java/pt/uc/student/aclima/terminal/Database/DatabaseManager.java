package pt.uc.student.aclima.terminal.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import pt.uc.student.aclima.terminal.Database.Tables.AggregatedEventfulMeasurementsTable;
import pt.uc.student.aclima.terminal.Database.Tables.AggregatedPeriodicMeasurementsTable;
import pt.uc.student.aclima.terminal.Database.Tables.EventfulMeasurementsTable;
import pt.uc.student.aclima.terminal.Database.Tables.OneTimeMeasurementsTable;
import pt.uc.student.aclima.terminal.Database.Tables.PeriodicMeasurementsTable;

/**
 * Created by aclima on 06/10/16.
 */
public final class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pt.uc.student.aclima.terminal.db";

    private static PeriodicMeasurementsTable periodicMeasurementsTable;
    private static EventfulMeasurementsTable eventfulMeasurementsTable;
    private static OneTimeMeasurementsTable oneTimeMeasurementsTable;
    private static AggregatedEventfulMeasurementsTable aggregatedEventfulMeasurementsTable;
    private static AggregatedPeriodicMeasurementsTable aggregatedPeriodicMeasurementsTable;

    public DatabaseManager(Context context){
        super(context, DATABASE_NAME, null, 1);

        periodicMeasurementsTable = new PeriodicMeasurementsTable(this);
        eventfulMeasurementsTable = new EventfulMeasurementsTable(this);
        oneTimeMeasurementsTable = new OneTimeMeasurementsTable(this);
        aggregatedEventfulMeasurementsTable = new AggregatedEventfulMeasurementsTable(this);
        aggregatedPeriodicMeasurementsTable = new AggregatedPeriodicMeasurementsTable(this);

        //context.deleteDatabase(DATABASE_NAME); // TODO: comment me
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        createDatabase(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("onUpgrade", "Dropping tables...");

        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + SubjectsTableManager.SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TopicsTableManager.TOPICS);
        db.execSQL("DROP TABLE IF EXISTS " + EntriesTableManager.ENTRIES);
        db.execSQL("DROP TABLE IF EXISTS " + AnswersTableManager.ANSWERS);

        Log.d("onUpgrade", "Dropped tables.");

        // create fresh books table
        createDatabase(db);
    }

    // Create the database's tables.
    private void createDatabase(SQLiteDatabase database) {
        try{

            Log.d("createDatabase", "Opening database...");
            database = SQLiteDatabase.openDatabase(DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            Log.d("createDatabase", "Opened database");

        } catch (Exception e){

            Log.d("createDatabase", "Creating tables...");

            // Subjects table
            database.execSQL("CREATE TABLE " + SubjectsTableManager.SUBJECTS + " ( "
                    + SubjectsTableManager.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SubjectsTableManager.NAME + " TEXT, "
                    + SubjectsTableManager.DESCRIPTION + " TEXT, "
                    + SubjectsTableManager.ICON + " BLOB)");

            // Topics table
            database.execSQL("CREATE TABLE " + TopicsTableManager.TOPICS + " ( "
                    + TopicsTableManager.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TopicsTableManager.SUBJECT_ID + " INTEGER, "
                    + TopicsTableManager.NAME + " TEXT, "
                    + TopicsTableManager.DESCRIPTION + " TEXT, "
                    + TopicsTableManager.ICON + " BLOB)");

            // Entries table
            database.execSQL("CREATE TABLE " + EntriesTableManager.ENTRIES + " ( "
                    + EntriesTableManager.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EntriesTableManager.TOPIC_ID + " INTEGER, "
                    + EntriesTableManager.QUESTION + " TEXT, "
                    + EntriesTableManager.IMAGE + " BLOB)");

            // Answers table
            database.execSQL("CREATE TABLE " + AnswersTableManager.ANSWERS + " ( "
                    + AnswersTableManager.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + AnswersTableManager.ENTRY_ID + " INTEGER, "
                    + AnswersTableManager.VALUE + " TEXT, "
                    + AnswersTableManager.IS_CORRECT_ANSWER + " INTEGER)");

            Log.d("createDatabase", "Created tables.");

        }
    }

    public SubjectsTableManager getSubjectsTableManager() {
        return subjectsTableManager;
    }

    public TopicsTableManager getTopicsTableManager() {
        return topicsTableManager;
    }

    public EntriesTableManager getEntriesTableManager() {
        return entriesTableManager;
    }

    public AnswersTableManager getAnswersTableManager() {
        return answersTableManager;
    }

    // convert from bitmap to byte array
    static byte[] getBytes(Bitmap bitmap) {
        if(bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }
        return new byte[0];
    }

    // convert from byte array to bitmap
    static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
