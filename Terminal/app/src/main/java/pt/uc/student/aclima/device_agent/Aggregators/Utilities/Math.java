package pt.uc.student.aclima.device_agent.Aggregators.Utilities;

import java.util.Collections;
import java.util.List;

/**
 * Created by aclima on 23/02/2017.
 * Source: <a>https://stackoverflow.com/questions/4191687/how-to-calculate-mean-median-mode-and-range-from-a-set-of-numbers</a>
 */

public class Math {

    // Mean
    public Double mean(List<Double> items) {
        Double sum = 0.0;
        for (Double item : items) {
            sum += item;
        }
        return sum / items.size();
    }

    // Median
    public Double median(List<Double> items) {
        Collections.sort(items);
        int middle = (items.size() / 2);
        if (items.size() % 2 == 1) {
            return items.get(middle);
        } else {
            return (items.get(middle-1) + items.get(middle)) / 2.0;
        }
    }

    // Mode
    public Double mode(List<Double> items) {
        Double maxValue = -1.0;
        int maxCount = -1;

        for (Double item1 : items) {
            int count = 0;
            for (Double item2 : items) {
                if ( item2.equals(item1) )
                    ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = item1;
            }
        }

        return maxValue;
    }

    // Harmonic Mean
    public Double harmonicMean(List<Double> items) {
        Double sum = 0.0;

        for (Double item : items) {
            sum += 1.0 / item;
        }
        return items.size() / sum;
    }

}
