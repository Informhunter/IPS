package io.informhunter.locator.positioning;

import java.util.HashMap;
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
            float x = scanner.nextFloat();
            float y = scanner.nextFloat();
            int n = scanner.nextInt();
            HashMap<Integer, Float> pack = new HashMap<>();
            for(int i = 0; i < n; i++) {
                int minor = scanner.nextInt();
                float rssi = scanner.nextFloat();
                pack.put(minor, rssi);
            }
            rssiMap.put(new Location(x, y), new RSSIPack(pack));
        }
        scanner.close();
    }

    public Location FindClosest(RSSIPack fingerprint) {
        float bestDist = 10000000;
        Location bestLocation = new Location(0, 0);
        for(Location location : rssiMap.keySet()) {
            float dist = rssiMap.get(location).EucledianDistance(fingerprint);
            if(bestDist > dist) {
                bestDist = dist;
                bestLocation = location;
            }
        }
        return bestLocation;
    }

}
