package io.informhunter.datacollector;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by informhunter on 04.03.2017.
 */

public class RSSIDataPoint extends DataPoint {
    private int RSSI;
    private String Name;
    private byte[] UUID;
    private byte[] Major;
    private byte[] Minor;

    RSSIDataPoint(String name, int RSSI, byte[] data) {
        Type = DataPointType.RSSI;
        this.Name = name;
        this.RSSI = RSSI;
        UUID = Arrays.copyOfRange(data, 9, 25);
        Major = Arrays.copyOfRange(data, 25, 27);
        Minor = Arrays.copyOfRange(data, 27, 29);
    }

    public static void WriteHeaderToFile(FileWriter fileWriter) throws IOException {
        fileWriter.write("UUID,Major,Minor,RSSI,Name,Timestamp\n");
    }

    @Override
    public void WriteToFile(FileWriter fileWriter) throws IOException {
        fileWriter.write(String.format(Locale.US, "%s,%s,%s,%d,%s,%d\n",
                Util.BytesToHex(UUID),
                Util.BytesToHex(Major),
                Util.BytesToHex(Minor),
                RSSI,
                Name,
                Timestamp));
    }
}
