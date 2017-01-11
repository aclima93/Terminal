package pt.uc.student.aclima.terminal.Database.Entries;

import java.util.Date;

/**
 * Created by aclima on 13/12/2016.
 */

public class PeriodicAggregatedMeasurement extends AggregatedMeasurement{

    public PeriodicAggregatedMeasurement(Integer id, String name, Integer numberOfEvents, Date sampleStartTime, Date sampleEndTime) {
        super(id, name, numberOfEvents, sampleStartTime, sampleEndTime);
    }

}
