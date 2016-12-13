package pt.uc.student.aclima.terminal.Database.Tables;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class AggregatedPeriodicMeasurementsTable extends MeasurementsTable {

    public static final String TABLE_NAME = "AggregatedPeriodicMeasurementsTable";

    public static final String HARMONIC_VALUE = "harmonic_value";
    public static final String MEDIAN_VALUE = "median_value";
    public static final String UNITS_OF_MEASUREMENT = "units_of_measurement";
    public static final String SAMPLE_START_TIME = "sample_start_time";
    public static final String SAMPLE_END_TIME = "sample_end_time";

    public AggregatedPeriodicMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }

}
