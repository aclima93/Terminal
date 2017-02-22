package pt.uc.student.aclima.device_agent.Database.Entries;

import java.util.Date;

/**
 * Created by aclima on 13/12/2016.
 */

public class SchedulingEntry extends Measurement {

    private Integer value;
    private Date timestamp;

    public SchedulingEntry(Integer id, String name, Integer value, Date timestamp) {
        super(id, name);
        this.value = value;
        this.timestamp = timestamp;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}