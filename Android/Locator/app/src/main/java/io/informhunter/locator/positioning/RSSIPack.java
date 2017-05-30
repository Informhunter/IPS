package io.informhunter.locator.positioning;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by informhunter on 29.05.2017.
 */

public class RSSIPack {
    Map<Integer, Float> minorMap;

    public RSSIPack(HashMap<Integer, Float> mMap) {
        minorMap = new HashMap<>(mMap);
    }

}
