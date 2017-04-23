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

    public static int BytesToInt(byte[] in) {
        int result = 0;
        for(byte b:in){
            result <<= 8;
            result |= (b & 0xff);
        }
        return result;
    }
}
