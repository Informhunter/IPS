package io.informhunter.datacollector;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by informhunter on 16.03.2017.
 */

public class AccelerationDataPoint extends DataPoint {

    float Ax, Ay, Az;

    @Override
    public void WriteToFile(FileWriter fileWriter) throws IOException {
        fileWriter.write(String.format(Locale.US, "%f,%f,%f,%d\n",
                Ax,
                Ay,
                Az,
                Timestamp));
    }

    public static void WriteHeaderToFile(FileWriter fileWriter) throws IOException {
        fileWriter.write("X,Y,Z,Timestamp\n");
    }
}
