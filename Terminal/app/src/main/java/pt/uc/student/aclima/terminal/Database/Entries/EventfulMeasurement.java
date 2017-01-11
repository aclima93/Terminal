package pt.uc.student.aclima.terminal.Database.Entries;

import java.util.Date;

/**
 * Created by aclima on 13/12/2016.
 */

public class EventfulMeasurement extends SingleMeasurement {

    public EventfulMeasurement(Integer id, String name, String value, String unitsOfMeasurement, Date timestamp) {
        super(id, name, value, unitsOfMeasurement, timestamp);
    }
}
