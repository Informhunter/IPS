package io.informhunter.locator.positioning;


import android.os.Environment;

import java.util.HashMap;
import java.util.Map;

import io.informhunter.locator.data.RSSIDataPoint;
import io.informhunter.locator.Util;

/**
 * Created by informhunter on 08.05.2017.
 */


public class Locator {

    private int windowSize = 21;
    private RSSIMap rssiMap;
    private Map<Integer, Window> minorMap;

    public Locator() {
        minorMap = new HashMap<>();
        String mapFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                + "/" + "datacollector" + "/" + "map.csv";
        rssiMap = new RSSIMap(mapFileName);
    }

    public void AddDataPoint(RSSIDataPoint dp) {
        if(!Util.BytesToHex(dp.GetUUID()).equals("b9407f30f5f8466eaff925556b57fe6d")) {
            return;
        }
        int minor = Util.BytesToInt(dp.GetMinor());
        if(!minorMap.containsKey(minor)) {
            minorMap.put(minor, new Window(windowSize));
        }
        minorMap.get(minor).AddPoint((float)dp.GetRSSI());
    }

    public Map<Integer, Float> GetAverages() {
        Map<Integer, Float> result = new HashMap<>();
        for(int key : minorMap.keySet()) {
            result.put(key, minorMap.get(key).Average());
        }
        return result;
    }

public Location GetLocation() {
    return rssiMap.FindClosest(new RSSIPack(GetAverages()));
}

    public float GetBestDistance() {
        return rssiMap.GetBestDistance();
    }
}
