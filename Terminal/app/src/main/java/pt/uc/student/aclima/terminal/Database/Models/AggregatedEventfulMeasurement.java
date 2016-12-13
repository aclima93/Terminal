package pt.uc.student.aclima.terminal.Database.Models;

import java.util.Date;

/**
 * Created by aclima on 13/12/2016.
 */

public class AggregatedEventfulMeasurement extends Measurement{

    private Integer numberOfEvents;
    private Date sampleStartTime;
    private Date sampleEndTime;

    public AggregatedEventfulMeasurement(Integer id, String name, Integer numberOfEvents, Date sampleStartTime, Date sampleEndTime) {
        super(id, name);
        this.numberOfEvents = numberOfEvents;
        this.sampleStartTime = sampleStartTime;
        this.sampleEndTime = sampleEndTime;
    }

    public Integer getNumberOfEvents() {
        return numberOfEvents;
    }

    public void setNumberOfEvents(Integer numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }

    public Date getSampleStartTime() {
        return sampleStartTime;
    }

    public void setSampleStartTime(Date sampleStartTime) {
        this.sampleStartTime = sampleStartTime;
    }

    public Date getSampleEndTime() {
        return sampleEndTime;
    }

    public void setSampleEndTime(Date sampleEndTime) {
        this.sampleEndTime = sampleEndTime;
    }
}
