package pt.uc.student.aclima.device_agent.Database.Tables;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;

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
}
