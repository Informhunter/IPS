package io.informhunter.locator.positioning;

import java.util.Map;
import java.util.Scanner;

/**
 * Created by informhunter on 29.05.2017.
 */
class RSSIMap {

    Map<Location, RSSIPack> rssiMap;

    public RSSIMap(String dataFilename) {
        Scanner scanner = new Scanner(dataFilename);
        while(scanner.hasNext()) {
            scanner.nextFloat();
        }
    }

    public Location FindClosest(Map<Integer, Float> minorMap) {
        return null;
    }

}
