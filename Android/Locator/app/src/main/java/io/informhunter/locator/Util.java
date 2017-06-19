package io.informhunter.locator;

import java.util.List;

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

    public static float Mean(List<Float> array) {
        float result = 0.0f;
        for(Float i : array) {
            result += i;
        }
        result = result / array.size();
        return result;
    }

    public static float Std(List<Float> array) {
        float mean = Mean(array);
        return Std(array, mean);
    }

    public static float Std(List<Float> array, float mean) {
        float result = 0.0f;
        for(Float i : array) {
            result += (i - mean) * (i - mean);
        }
        result = (float)Math.sqrt(result / array.size());
        return result;
    }

    public static float ResultStd(List<Float> diffArray) {
        float result = 0.0f;
        for(Float i : diffArray) {
            result += i * i;
        }
        result = (float)Math.sqrt(result / diffArray.size());
        return result;
    }


}

