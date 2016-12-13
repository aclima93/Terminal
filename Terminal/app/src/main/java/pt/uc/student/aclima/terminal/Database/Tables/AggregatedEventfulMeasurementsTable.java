package pt.uc.student.aclima.terminal.Database.Tables;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class AggregatedEventfulMeasurementsTable {

    public static final String TABLE_NAME = "AggregatedEventfulMeasurementsTable";

    private DatabaseManager databaseManager;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String NUMBER_OF_EVENTS = "number_of_events";
    public static final String TIME_RANGE = "time_range";

    AggregatedEventfulMeasurementsTable(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

}
