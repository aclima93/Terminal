package pt.uc.student.aclima.terminal.DatabaseManagers;

/**
 * Created by aclima on 13/12/2016.
 */

public class EventfulMeasurementsTable extends MeasurementsTable {

    public static final String TABLE_NAME = "EventfulMeasurementsTable";

    EventfulMeasurementsTable(DatabaseManager databaseManager) {
        super(databaseManager);
    }
}
