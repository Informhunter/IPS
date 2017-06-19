package io.informhunter.datacollector2.data;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.informhunter.datacollector2.util.MultipartUtility;
import io.informhunter.datacollector2.util.Util;

/**
 * Created by informhunter on 19.06.2017.
 */

public class Collector {
    private List<DataPoint> data;
    private Set<Integer> minorSet;

    public Collector() {
        data = new ArrayList<>();
        minorSet = new HashSet<>();
    }

    public void AddDataPoint(DataPoint dp) {
        if(dp.GetPointType() == DataPointType.RSSI) {
            RSSIDataPoint rdp = (RSSIDataPoint) dp;
            minorSet.add(Util.BytesToInt(rdp.GetMinor()));
        }
        data.add(dp);
    }

    public void ResetData() {
        data.clear();
    }

    public void ResetMinorCounter() {
        minorSet.clear();
    }

    public int GetMinorCount() {
        return minorSet.size();
    }

    public int GetDataSize() {
        return data.size();
    }

    public boolean SaveData(String sessionName) {
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                + "/" + "datacollector" + "/" + sessionName;


        File folder = new File(folderPath);
        boolean created = folder.mkdirs();

        if(!created) {
            return false;
        }

        String positionDataFileName = folder.getPath() + "/" + "position_data.csv";
        String rssiDataFileName = folder.getPath() + "/" + "rssi_data.csv";
        String sessionInfoFileName = folder.getPath() + "/" + "session_info.csv";

        try {
            FileWriter rssiFW = new FileWriter(rssiDataFileName);
            RSSIDataPoint.WriteHeaderToFile(rssiFW);

            FileWriter posFW = new FileWriter(positionDataFileName);
            PositionDataPoint.WriteHeaderToFile(posFW);

            FileWriter sessFW = new FileWriter(sessionInfoFileName);

            sessFW.write(sessionName + "\n");
            sessFW.write(new Date().toString());
            sessFW.close();

            for(DataPoint dp : data) {
                switch (dp.GetPointType()) {
                    case Position:
                        dp.WriteToFile(posFW);
                        break;
                    case RSSI:
                        dp.WriteToFile(rssiFW);
                        break;
                }
            }
            posFW.close();
            rssiFW.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String SendData(String sessionName, String url) {
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                + "/" + "datacollector" + "/" + sessionName;

        File folder = new File(folderPath);

        String positionDataFileName = folder.toString() + "/" + "position_data.csv";
        String rssiDataFileName = folder.toString() + "/" + "rssi_data.csv";
        String sessionInfoFileName = folder.getPath() + "/" + "session_info.csv";

        try {
            MultipartUtility multipart = new MultipartUtility(url, "UTF-8");
            File rssiDataFile = new File(rssiDataFileName);
            File positionDataFile = new File(positionDataFileName);
            File sessionDataFile = new File(sessionInfoFileName);
            multipart.addFilePart("rssi_data", rssiDataFile);
            multipart.addFilePart("position_data", positionDataFile);
            multipart.addFilePart("session_data", sessionDataFile);
            multipart.finish();
        } catch (Exception e) {
            return e.toString();
        }
        return null;
    }
}
