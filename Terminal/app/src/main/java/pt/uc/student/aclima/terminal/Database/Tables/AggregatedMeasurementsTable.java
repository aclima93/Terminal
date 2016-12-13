package pt.uc.student.aclima.terminal.Database.Tables;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class AggregatedMeasurementsTable extends MeasurementsTable {

    public static final String SAMPLE_START_TIME = "sample_start_time";
    public static final String SAMPLE_END_TIME = "sample_end_time";

    public AggregatedMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }

}
