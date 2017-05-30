package io.informhunter.locator.positioning;


import java.util.HashMap;
import java.util.Map;

import io.informhunter.locator.RSSIDataPoint;
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
    }

    public void AddDataPoint(RSSIDataPoint dp) {
        int minor = Util.BytesToInt(dp.GetMinor());
        if(!minorMap.containsKey(minor)) {
            minorMap.put(minor, new Window(windowSize));
        }
        minorMap.get(minor).AddPoint((float)dp.GetRSSI());
    }

    public RSSIPack GetAverages() {
        Map<Integer, Float> result = new HashMap<>();
        for(int key : minorMap.keySet()) {
            result.put(key, minorMap.get(key).Average());
        }
        return new RSSIPack(result);
    }

    public Location GetLocation() {
        return rssiMap.FindClosest(GetAverages());
    }
}
