package io.informhunter.datacollector;

/**
 * Created by informhunter on 14.04.2017.
 */

public class Util {
    public static String BytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
