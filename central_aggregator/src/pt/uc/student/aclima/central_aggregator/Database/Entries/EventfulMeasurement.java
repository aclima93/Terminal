package pt.uc.student.aclima.central_aggregator.Database.Entries;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aclima on 13/12/2016.
 */

public class EventfulMeasurement extends SingleMeasurement implements Serializable {

    public EventfulMeasurement(Integer id, String name, String value, String unitsOfMeasurement, Date timestamp) {
        super(id, name, value, unitsOfMeasurement, timestamp);
    }
}
