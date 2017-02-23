package pt.uc.student.aclima.device_agent.Aggregators.Utilities;

/**
 * Created by aclima on 23/02/2017.
 * Source: <a>https://stackoverflow.com/questions/4191687/how-to-calculate-mean-median-mode-and-range-from-a-set-of-numbers</a>
 */

public class Math {

    // Mean
    public static double mean(double[] items) {
        double sum = 0;
        for (double item : items) {
            sum += item;
        }
        return sum / items.length;
    }

    // Median
    // the array double[] items MUST BE SORTED
    public static double median(double[] items) {
        int middle = items.length/2;
        if (items.length%2 == 1) {
            return items[middle];
        } else {
            return (items[middle-1] + items[middle]) / 2.0;
        }
    }

    // Mode
    public static int mode(int items[]) {
        int maxValue = -1, maxCount = -1;

        for (int item1 : items) {
            int count = 0;
            for (int item2 : items) {
                if (item2 == item1)
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
    public static double harmonicMean(int[] items) {
        double sum = 0.0;

        for (int item : items) {
            sum += 1.0 / item;
        }
        return items.length / sum;
    }

}
