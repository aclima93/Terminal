package pt.uc.student.aclima.terminal.Database.Tables;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class EventfulAggregatedMeasurementsTable extends AggregatedMeasurementsTable {

    public static final String TABLE_NAME = "EventfulAggregatedMeasurementsTable";

    public static final String NUMBER_OF_EVENTS = "number_of_events";

    public EventfulAggregatedMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }

}
