package pt.uc.student.aclima.device_agent.Database.Entries;

import java.util.Date;

/**
 * Created by aclima on 13/12/2016.
 */

public class PeriodicMeasurement extends SingleMeasurement implements Serializable {

    public PeriodicMeasurement(Integer id, String name, String value, String unitsOfMeasurement, Date timestamp) {
        super(id, name, value, unitsOfMeasurement, timestamp);
    }
}