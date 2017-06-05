package io.informhunter.locator.positioning;

import android.util.Log;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by informhunter on 29.05.2017.
 */
class RSSIMap {

    private int[] beaconMinors = new int[]{9609, 9616, 9617, 9618, 9619};

    Map<Location, RSSIPack> rssiMap;
    float distance = 0;

    public RSSIMap(String dataFilename) {
        rssiMap = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new FileReader(dataFilename)).useLocale(Locale.ENGLISH);
            while(scanner.hasNext()) {
                float x = scanner.nextFloat();
                float y = scanner.nextFloat();
                HashMap<Integer, Float> pack = new HashMap<>();
                for(int i = 0; i < beaconMinors.length; i++) {
                    float rssi = scanner.nextFloat();
                    pack.put(beaconMinors[i], rssi);
                }
                rssiMap.put(new Location(x, y), new RSSIPack(pack));
            }
            scanner.close();
        } catch (Exception ex) {
            Log.e("Error: ", ex.getMessage());
        }
    }

    public Location FindClosest(RSSIPack fingerprint) {
        float bestDist = 100000000;
        Location bestLocation = new Location(0, 0);
        for(Location location : rssiMap.keySet()) {
            float dist = rssiMap.get(location).EucledianDistance(fingerprint);
            if(bestDist > dist) {
                bestDist = dist;
                bestLocation = location;
            }
        }

        RSSIPack pack = rssiMap.get(bestLocation);
        StringBuilder b = new StringBuilder();
        for(Integer i : beaconMinors) {
            b.append(pack.GetRSSI(i));
            b.append(" ");
        }
        b.append(bestDist);
        Log.e("Loc: ", b.toString());
        distance = bestDist;
        return bestLocation;
    }


    public float GetBestDistance() {
        return distance;
    }

}
