package pt.uc.student.aclima.terminal.Database.Tables;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class AggregatedPeriodicMeasurementsTable extends AggregatedMeasurementsTable {

    public static final String TABLE_NAME = "PeriodicAggregatedMeasurementsTable";

    public static final String HARMONIC_VALUE = "harmonic_value";
    public static final String MEDIAN_VALUE = "median_value";
    public static final String UNITS_OF_MEASUREMENT = "units_of_measurement";

    public AggregatedPeriodicMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }

}
