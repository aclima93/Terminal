package pt.uc.student.aclima.terminal.Database.Tables;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class PeriodicMeasurementsTable extends MeasurementsTable {

    public static final String TABLE_NAME = "PeriodicMeasurementsTable";

    PeriodicMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }
}
