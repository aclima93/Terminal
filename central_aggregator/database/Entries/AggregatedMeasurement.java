package pt.uc.student.aclima.device_agent.Database.Entries;

import java.util.Date;

/**
 * Created by aclima on 11/01/2017.
 */

public class AggregatedMeasurement extends Measurement implements Serializable {

    private Date sampleStartTime;
    private Date sampleEndTime;

    public AggregatedMeasurement(Integer id, String name, Date sampleStartTime, Date sampleEndTime) {
        super(id, name);
        this.sampleStartTime = sampleStartTime;
        this.sampleEndTime = sampleEndTime;
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