package pt.uc.student.aclima.terminal.Database.Entries;

import java.util.Date;

/**
 * Created by aclima on 11/01/2017.
 */

public class AggregatedMeasurement extends Measurement {

    private Integer numberOfEvents;
    private Date sampleStartTime;
    private Date sampleEndTime;

    public AggregatedMeasurement(Integer id, String name, Integer numberOfEvents, Date sampleStartTime, Date sampleEndTime) {
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
