package io.informhunter.datacollector;

import java.util.Calendar;

/**
 * Created by informhunter on 04.03.2017.
 */

public class DataPoint {
    public int RSSI;
    public String Name;
    public float[] Point;
    public long Timestamp;

    DataPoint(String name, int RSSI, float[] point) {
        this.Name = name;
        this.RSSI = RSSI;
        this.Point = point.clone();
        this.Timestamp = Calendar.getInstance().getTimeInMillis();
    }
}
