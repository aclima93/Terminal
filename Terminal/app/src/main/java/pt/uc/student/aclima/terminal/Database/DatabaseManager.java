package pt.uc.student.aclima.terminal.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import pt.uc.student.aclima.terminal.Database.Tables.AggregatedEventfulMeasurementsTable;
import pt.uc.student.aclima.terminal.Database.Tables.AggregatedPeriodicMeasurementsTable;
import pt.uc.student.aclima.terminal.Database.Tables.EventfulMeasurementsTable;
import pt.uc.student.aclima.terminal.Database.Tables.OneTimeMeasurementsTable;
import pt.uc.student.aclima.terminal.Database.Tables.PeriodicMeasurementsTable;

/**
 * Created by aclima on 06/10/16.
 */
public final class DatabaseManager extends SQLiteOpenHelper {

    public static final String TimestampFormat = "yyyy-MM-dd HH:mm:ss.SSS zzz";

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

        //context.deleteDatabase(DATABASE_NAME); // TODO: should i consider this as an option ?
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        createDatabase(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("onUpgrade", "Dropping tables...");

        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + PeriodicMeasurementsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EventfulMeasurementsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OneTimeMeasurementsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AggregatedPeriodicMeasurementsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AggregatedEventfulMeasurementsTable.TABLE_NAME);

        Log.d("onUpgrade", "Dropped tables.");

        // create new tables
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

            // PeriodicMeasurementsTable
            database.execSQL("CREATE TABLE " + PeriodicMeasurementsTable.TABLE_NAME + " ( "
                    + PeriodicMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PeriodicMeasurementsTable.NAME + " TEXT, "
                    + PeriodicMeasurementsTable.VALUE + " TEXT, "
                    + PeriodicMeasurementsTable.UNITS_OF_MEASUREMENT + " TEXT, "
                    + PeriodicMeasurementsTable.TIMESTAMP + " TEXT)");

            // EventfulMeasurementsTable
            database.execSQL("CREATE TABLE " + EventfulMeasurementsTable.TABLE_NAME + " ( "
                    + EventfulMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EventfulMeasurementsTable.NAME + " TEXT, "
                    + EventfulMeasurementsTable.VALUE + " TEXT, "
                    + EventfulMeasurementsTable.UNITS_OF_MEASUREMENT + " TEXT, "
                    + EventfulMeasurementsTable.TIMESTAMP + " TEXT)");

            // OneTimeMeasurementsTable
            database.execSQL("CREATE TABLE " + OneTimeMeasurementsTable.TABLE_NAME + " ( "
                    + OneTimeMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + OneTimeMeasurementsTable.NAME + " TEXT, "
                    + OneTimeMeasurementsTable.VALUE + " TEXT, "
                    + OneTimeMeasurementsTable.UNITS_OF_MEASUREMENT + " TEXT, "
                    + OneTimeMeasurementsTable.TIMESTAMP + " TEXT)");

            // AggregatedPeriodicMeasurementsTable
            database.execSQL("CREATE TABLE " + AggregatedPeriodicMeasurementsTable.TABLE_NAME + " ( "
                    + AggregatedPeriodicMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + AggregatedPeriodicMeasurementsTable.NAME + " TEXT, "
                    + AggregatedPeriodicMeasurementsTable.SAMPLE_START_TIME + " TEXT, "
                    + AggregatedPeriodicMeasurementsTable.SAMPLE_END_TIME + " TEXT, "
                    + AggregatedPeriodicMeasurementsTable.HARMONIC_VALUE + " TEXT, "
                    + AggregatedPeriodicMeasurementsTable.MEDIAN_VALUE + " TEXT, "
                    + AggregatedPeriodicMeasurementsTable.UNITS_OF_MEASUREMENT + " TEXT)");

            // AggregatedEventfulMeasurementsTable
            database.execSQL("CREATE TABLE " + AggregatedEventfulMeasurementsTable.TABLE_NAME + " ( "
                    + AggregatedEventfulMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + AggregatedEventfulMeasurementsTable.NAME + " TEXT, "
                    + AggregatedEventfulMeasurementsTable.SAMPLE_START_TIME + " TEXT, "
                    + AggregatedEventfulMeasurementsTable.SAMPLE_END_TIME + " TEXT, "
                    + AggregatedEventfulMeasurementsTable.NUMBER_OF_EVENTS + " TEXT)");

            Log.d("createDatabase", "Created tables.");

        }
    }

    public static PeriodicMeasurementsTable getPeriodicMeasurementsTable() {
        return periodicMeasurementsTable;
    }

    public static EventfulMeasurementsTable getEventfulMeasurementsTable() {
        return eventfulMeasurementsTable;
    }

    public static OneTimeMeasurementsTable getOneTimeMeasurementsTable() {
        return oneTimeMeasurementsTable;
    }

    public static AggregatedEventfulMeasurementsTable getAggregatedEventfulMeasurementsTable() {
        return aggregatedEventfulMeasurementsTable;
    }

    public static AggregatedPeriodicMeasurementsTable getAggregatedPeriodicMeasurementsTable() {
        return aggregatedPeriodicMeasurementsTable;
    }
}
