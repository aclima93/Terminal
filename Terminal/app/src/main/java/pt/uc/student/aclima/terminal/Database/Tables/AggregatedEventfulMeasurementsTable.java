package pt.uc.student.aclima.terminal.Database.Tables;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class AggregatedEventfulMeasurementsTable extends AggregatedMeasurementsTable {

    public static final String TABLE_NAME = "AggregatedEventfulMeasurementsTable";

    public static final String NUMBER_OF_EVENTS = "number_of_events";

    public AggregatedEventfulMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }

}
