package pt.uc.student.aclima.terminal.Database.Tables;

import pt.uc.student.aclima.terminal.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class MeasurementsTable {

    private DatabaseManager databaseManager;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String UNITS_OF_MEASUREMENT = "units_of_measurement";
    public static final String TIMESTAMP = "timestamp";

    MeasurementsTable(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

}