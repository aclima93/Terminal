package pt.uc.student.aclima.device_agent.Database.Entries;

import java.util.Date;

/**
 * Created by aclima on 13/12/2016.
 */

public class PeriodicAggregatedMeasurement extends AggregatedMeasurement{

    private String harmonicValue;
    private String medianValue;
    private String unitsOfMeasurement;

    public PeriodicAggregatedMeasurement(Integer id, String name, Date sampleStartTime, Date sampleEndTime, String harmonicValue, String medianValue, String unitsOfMeasurement) {
        super(id, name, sampleStartTime, sampleEndTime);
        this.harmonicValue = harmonicValue;
        this.medianValue = medianValue;
        this.unitsOfMeasurement = unitsOfMeasurement;
    }

    public String getHarmonicValue() {
        return harmonicValue;
    }

    public void setHarmonicValue(String harmonicValue) {
        this.harmonicValue = harmonicValue;
    }

    public String getMedianValue() {
        return medianValue;
    }

    public void setMedianValue(String medianValue) {
        this.medianValue = medianValue;
    }

    public String getUnitsOfMeasurement() {
        return unitsOfMeasurement;
    }

    public void setUnitsOfMeasurement(String unitsOfMeasurement) {
        this.unitsOfMeasurement = unitsOfMeasurement;
    }
}
