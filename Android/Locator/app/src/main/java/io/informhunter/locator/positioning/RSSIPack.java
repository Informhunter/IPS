package io.informhunter.locator.positioning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by informhunter on 29.05.2017.
 */

public class RSSIPack {
    private Map<Integer, Float> minorMap;

    public RSSIPack(Map<Integer, Float> mMap) {
        minorMap = new HashMap<>(mMap);
    }

    public float EucledianDistance(RSSIPack rssiPack) {
        Set<Integer> minors = new HashSet<>(rssiPack.minorMap.keySet());
        float sum = 0.0f;
        for(Integer minor : minors) {
            float diff = rssiPack.minorMap.get(minor) - minorMap.get(minor);
            sum += diff * diff;
        }
        return (float)Math.sqrt(sum);
    }

    public float GetRSSI(Integer minor) {
        return minorMap.get(minor);
    }

}
