package pt.uc.student.aclima.terminal.Database.Entries;

import java.util.Date;

/**
 * Created by aclima on 13/12/2016.
 */

public class EventfulAggregatedMeasurement extends AggregatedMeasurement{

    public EventfulAggregatedMeasurement(Integer id, String name, Integer numberOfEvents, Date sampleStartTime, Date sampleEndTime) {
        super(id, name, numberOfEvents, sampleStartTime, sampleEndTime);
    }

}
