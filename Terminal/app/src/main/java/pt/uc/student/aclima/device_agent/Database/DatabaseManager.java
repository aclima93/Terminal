package pt.uc.student.aclima.device_agent.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.uc.student.aclima.device_agent.Aggregators.EventfulDataAggregator.EventfulAggregatorIntentService;
import pt.uc.student.aclima.device_agent.Aggregators.PeriodicDataAggregator.PeriodicAggregatorIntentService;
import pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector.PeriodicIntentService;
import pt.uc.student.aclima.device_agent.Database.Tables.ConfigurationsTable;
import pt.uc.student.aclima.device_agent.Database.Tables.EventfulAggregatedMeasurementsTable;
import pt.uc.student.aclima.device_agent.Database.Tables.EventfulMeasurementsTable;
import pt.uc.student.aclima.device_agent.Database.Tables.OneTimeMeasurementsTable;
import pt.uc.student.aclima.device_agent.Database.Tables.PeriodicAggregatedMeasurementsTable;
import pt.uc.student.aclima.device_agent.Database.Tables.PeriodicMeasurementsTable;

import static pt.uc.student.aclima.device_agent.Database.Entries.Measurement.DELIMITER;

/**
 * Created by aclima on 06/10/16.
 */
public final class DatabaseManager extends SQLiteOpenHelper {

    public static final String TimestampFormat = "yyyy-MM-dd HH:mm:ss.SSS zzz";

    private static final String DATABASE_NAME = "pt.uc.student.aclima.terminal.db";

    private static ConfigurationsTable configurationsTable;

    private static PeriodicMeasurementsTable periodicMeasurementsTable;
    private static EventfulMeasurementsTable eventfulMeasurementsTable;
    private static OneTimeMeasurementsTable oneTimeMeasurementsTable;

    private static EventfulAggregatedMeasurementsTable eventfulAggregatedMeasurementsTable;
    private static PeriodicAggregatedMeasurementsTable periodicAggregatedMeasurementsTable;

    public DatabaseManager(Context context){
        super(context, DATABASE_NAME, null, 1);

        configurationsTable = new ConfigurationsTable(this);

        periodicMeasurementsTable = new PeriodicMeasurementsTable(this);
        eventfulMeasurementsTable = new EventfulMeasurementsTable(this);
        oneTimeMeasurementsTable = new OneTimeMeasurementsTable(this);

        eventfulAggregatedMeasurementsTable = new EventfulAggregatedMeasurementsTable(this);
        periodicAggregatedMeasurementsTable = new PeriodicAggregatedMeasurementsTable(this);

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
        db.execSQL("DROP TABLE IF EXISTS " + ConfigurationsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PeriodicMeasurementsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EventfulMeasurementsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OneTimeMeasurementsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PeriodicAggregatedMeasurementsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EventfulAggregatedMeasurementsTable.TABLE_NAME);

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

            // if an exception occurs it is best to just re-create the tables and re-populate them from scratch
            // TODO: is there a better way?
            createTables(database);
            populateConfigurationsTable(database);
        }
    }

    private void createTables(SQLiteDatabase database) {

        Log.d("createTables", "Creating tables...");

        /*
         * Configurations Table
         */

        // ConfigurationsTable
        Log.d("createTables", "Creating Configurations table...");
        database.execSQL("CREATE TABLE " + ConfigurationsTable.TABLE_NAME + " ( "
                + ConfigurationsTable.NAME + " TEXT PRIMARY KEY, " // the NAME will act as a unique identifier
                + ConfigurationsTable.VALUE + " TEXT, "
                + ConfigurationsTable.TIMESTAMP + " TEXT)");

        /*
         * Measurement Tables
         */

        // PeriodicMeasurementsTable
        Log.d("createTables", "Creating PeriodicMeasurements table...");
        database.execSQL("CREATE TABLE " + PeriodicMeasurementsTable.TABLE_NAME + " ( "
                + PeriodicMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PeriodicMeasurementsTable.NAME + " TEXT, "
                + PeriodicMeasurementsTable.VALUE + " TEXT, "
                + PeriodicMeasurementsTable.UNITS_OF_MEASUREMENT + " TEXT, "
                + PeriodicMeasurementsTable.TIMESTAMP + " TEXT)");

        // EventfulMeasurementsTable
        Log.d("createTables", "Creating EventfulMeasurements table...");
        database.execSQL("CREATE TABLE " + EventfulMeasurementsTable.TABLE_NAME + " ( "
                + EventfulMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EventfulMeasurementsTable.NAME + " TEXT, "
                + EventfulMeasurementsTable.VALUE + " TEXT, "
                + EventfulMeasurementsTable.UNITS_OF_MEASUREMENT + " TEXT, "
                + EventfulMeasurementsTable.TIMESTAMP + " TEXT)");

        // OneTimeMeasurementsTable
        Log.d("createTables", "Creating OneTimeMeasurements table...");
        database.execSQL("CREATE TABLE " + OneTimeMeasurementsTable.TABLE_NAME + " ( "
                + OneTimeMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + OneTimeMeasurementsTable.NAME + " TEXT, "
                + OneTimeMeasurementsTable.VALUE + " TEXT, "
                + OneTimeMeasurementsTable.UNITS_OF_MEASUREMENT + " TEXT, "
                + OneTimeMeasurementsTable.TIMESTAMP + " TEXT)");

        /*
         * Aggregated Measurement Tables
         */

        // PeriodicAggregatedMeasurementsTable
        Log.d("createTables", "Creating PeriodicAggregatedMeasurements table...");
        database.execSQL("CREATE TABLE " + PeriodicAggregatedMeasurementsTable.TABLE_NAME + " ( "
                + PeriodicAggregatedMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PeriodicAggregatedMeasurementsTable.NAME + " TEXT, "
                + PeriodicAggregatedMeasurementsTable.SAMPLE_START_TIME + " TEXT, "
                + PeriodicAggregatedMeasurementsTable.SAMPLE_END_TIME + " TEXT, "
                + PeriodicAggregatedMeasurementsTable.HARMONIC_VALUE + " TEXT, "
                + PeriodicAggregatedMeasurementsTable.MEDIAN_VALUE + " TEXT, "
                + PeriodicAggregatedMeasurementsTable.UNITS_OF_MEASUREMENT + " TEXT)");

        // EventfulAggregatedMeasurementsTable
        Log.d("createTables", "Creating EventfulAggregatedMeasurements table...");
        database.execSQL("CREATE TABLE " + EventfulAggregatedMeasurementsTable.TABLE_NAME + " ( "
                + EventfulAggregatedMeasurementsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EventfulAggregatedMeasurementsTable.NAME + " TEXT, "
                + EventfulAggregatedMeasurementsTable.SAMPLE_START_TIME + " TEXT, "
                + EventfulAggregatedMeasurementsTable.SAMPLE_END_TIME + " TEXT, "
                + EventfulAggregatedMeasurementsTable.NUMBER_OF_EVENTS + " INTEGER)");

        Log.d("createTables", "Created tables.");

    }

    private void populateConfigurationsTable(SQLiteDatabase database) {
        Log.d("ConfigurationsTable", "Populating table...");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);
        String sampleStartTime = simpleDateFormat.format(new Date());

        List<Pair<String, String>> namesValuePairs = new ArrayList<>();

        // Periodic Collection Actions
        namesValuePairs.add(new Pair<>(PeriodicIntentService.ACTION_RAM, (10 * 1000) + "")); // every 10 seconds
        namesValuePairs.add(new Pair<>(PeriodicIntentService.ACTION_CPU, (10 * 1000) + "")); // every 10 seconds
        namesValuePairs.add(new Pair<>(PeriodicIntentService.ACTION_GPS, (10 * 1000) + "")); // every 10 seconds
        namesValuePairs.add(new Pair<>(PeriodicIntentService.ACTION_CPU_USAGE, (1000) + "")); // every second
        namesValuePairs.add(new Pair<>(PeriodicIntentService.ACTION_RAM_USAGE, (1000) + "")); // every second
        namesValuePairs.add(new Pair<>(PeriodicIntentService.ACTION_BATTERY, (5 * 60 * 1000) + "")); // every 5 minutes
        namesValuePairs.add(new Pair<>(PeriodicIntentService.ACTION_OPEN_PORTS, (60 * 1000) + "")); // every minute
        namesValuePairs.add(new Pair<>(PeriodicIntentService.ACTION_DATA_TRAFFIC, (5 * 60 * 1000) + "")); // every 5 minutes

        // Periodic Aggregation Action
        namesValuePairs.add(new Pair<>(PeriodicAggregatorIntentService.ACTION_AGGREGATE_PERIODIC_DATA, (1000) + "")); // every 30 minutes
        namesValuePairs.add(new Pair<>(PeriodicAggregatorIntentService.EXTRA_AGGREGATE_PERIODIC_DATA_SAMPLE_START_TIME, sampleStartTime)); // when the last aggregation was made

        // Eventful Aggregation Action
        namesValuePairs.add(new Pair<>(EventfulAggregatorIntentService.ACTION_AGGREGATE_EVENTFUL_DATA, (1000) + "")); // every 30 minutes
        namesValuePairs.add(new Pair<>(EventfulAggregatorIntentService.EXTRA_AGGREGATE_EVENTFUL_DATA_SAMPLE_START_TIME, sampleStartTime)); // when the last aggregation was made

        for( Pair<String, String> namesValuePair : namesValuePairs ) {

            String name = namesValuePair.first;
            String value = namesValuePair.second;

            boolean success = addConfigurationRow(database, name, value, new Date());
            if (!success) {
                Log.e("Configuration", "Configuration" + DELIMITER + name + " service failed to add row.");
            }
        }

        Log.d("ConfigurationsTable", "Populated table.");
    }

    public boolean addConfigurationRow(SQLiteDatabase database, String name, String value, Date timestamp){

        Log.d("addConfigurationRow",
                "Adding row to table named " + ConfigurationsTable.TABLE_NAME + "\n" +
                        ConfigurationsTable.NAME + ": " + name + "\n" +
                        ConfigurationsTable.VALUE + ": " + value + "\n" +
                        ConfigurationsTable.TIMESTAMP + ": " + timestamp.toString() + "\n"
        );

        boolean success;

        try {
            database.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put(ConfigurationsTable.NAME, name);
            contentValues.put(ConfigurationsTable.VALUE, value);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatabaseManager.TimestampFormat);
            String timestampString = simpleDateFormat.format(timestamp);
            contentValues.put(ConfigurationsTable.TIMESTAMP, timestampString);

            database.insertOrThrow(ConfigurationsTable.TABLE_NAME, ConfigurationsTable.NAME, contentValues);

            database.setTransactionSuccessful();
            success = true;

            Log.d("addConfigurationRow", "Added to table.");
        }
        catch (Exception e){
            e.printStackTrace();
            success = false;

            Log.d("addConfigurationRow", "Failed to add to table.");
        }
        finally {
            database.endTransaction();
        }

        return success;
    }


    public static ConfigurationsTable getConfigurationsTable() {
        return configurationsTable;
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

    public static EventfulAggregatedMeasurementsTable getEventfulAggregatedMeasurementsTable() {
        return eventfulAggregatedMeasurementsTable;
    }

    public static PeriodicAggregatedMeasurementsTable getPeriodicAggregatedMeasurementsTable() {
        return periodicAggregatedMeasurementsTable;
    }
}
