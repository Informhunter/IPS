package io.informhunter.locator.positioning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by informhunter on 29.05.2017.
 */

public class RSSIPack {
    private Map<Integer, Float> minorRSSIMap;
    private Map<Integer, Float> minorStdMap;


    public RSSIPack(Map<Integer, Float> mrMap, Map<Integer, Float> msMap) {
        minorRSSIMap = new HashMap<>(mrMap);
        minorStdMap = new HashMap<>(msMap);
    }

    public RSSIPack(Map<Integer, Float> mrMap) {
        minorRSSIMap = new HashMap<>(mrMap);
    }

    public float EucledianDistance(RSSIPack rssiPack) {
        Set<Integer> minors = new HashSet<>(rssiPack.minorRSSIMap.keySet());
        float sum = 0.0f;
        for(Integer minor : minors) {
            float diff = rssiPack.minorRSSIMap.get(minor) - minorRSSIMap.get(minor);
            sum += diff * diff;
        }
        return (float)Math.sqrt(sum);
    }

    public float CosineDistance(RSSIPack rssiPack) {
        Set<Integer> minors = new HashSet<>(rssiPack.minorRSSIMap.keySet());
        float product = 0.0f;
        float squared_length_a = 0.0f;
        float squared_length_b = 0.0f;
        for(Integer minor : minors) {
            float a = minorRSSIMap.get(minor);
            float b = rssiPack.minorRSSIMap.get(minor);
            product += a * b;
            squared_length_a += a * a;
            squared_length_b += b * b;
        }
        return product / (float)Math.sqrt(squared_length_a * squared_length_b);
    }

    public float GaussianDistance(RSSIPack rssiPack) {
        if(minorStdMap == null) {
            return -1;
        }
        float sumProba = 0.0f;
        Set<Integer> minors = new HashSet<>(rssiPack.minorRSSIMap.keySet());
        for(Integer minor : minors) {
            float sigma = minorStdMap.get(minor);
            float mu = minorRSSIMap.get(minor);
            float x = rssiPack.minorRSSIMap.get(minor);
            sumProba += Math.exp(-((mu - x) * (mu - x) / (2 * sigma * sigma))) / sigma;
        }
        return -sumProba;
    }

    public float GetRSSI(Integer minor) {
        return minorRSSIMap.get(minor);
    }

}
