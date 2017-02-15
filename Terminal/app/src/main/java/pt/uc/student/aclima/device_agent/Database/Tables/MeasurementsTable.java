package pt.uc.student.aclima.device_agent.Database.Tables;

import pt.uc.student.aclima.device_agent.Database.DatabaseManager;

/**
 * Created by aclima on 13/12/2016.
 */

public class MeasurementsTable {

    protected DatabaseManager databaseManager;

    public static final String ID = "id";
    public static final String NAME = "name";

    MeasurementsTable(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

}
