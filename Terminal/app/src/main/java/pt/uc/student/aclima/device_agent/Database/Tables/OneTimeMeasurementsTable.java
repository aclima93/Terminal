package pt.uc.student.aclima.device_agent.Database.Tables;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class OneTimeMeasurementsTable extends MeasurementsTable {

    public static final String TABLE_NAME = "OneTimeMeasurementsTable";

    public static final String VALUE = "value";
    public static final String UNITS_OF_MEASUREMENT = "units_of_measurement";
    public static final String TIMESTAMP = "timestamp";

    public OneTimeMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }
}
