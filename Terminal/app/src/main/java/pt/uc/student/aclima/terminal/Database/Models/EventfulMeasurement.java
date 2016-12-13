package pt.uc.student.aclima.terminal.Database.Models;

import java.util.Date;

/**
 * Created by aclima on 13/12/2016.
 */

public class EventfulMeasurement extends Measurement{

    private String value;
    private String unitsOfMeasurement;
    private Date timestamp;

    public EventfulMeasurement(Integer id, String name, String value, String unitsOfMeasurement, Date timestamp) {
        super(id, name);
        this.value = value;
        this.unitsOfMeasurement = unitsOfMeasurement;
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnitsOfMeasurement() {
        return unitsOfMeasurement;
    }

    public void setUnitsOfMeasurement(String unitsOfMeasurement) {
        this.unitsOfMeasurement = unitsOfMeasurement;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
