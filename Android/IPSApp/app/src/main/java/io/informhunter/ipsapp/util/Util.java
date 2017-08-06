package io.informhunter.ipsapp.util;

import android.graphics.Matrix;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public static int[] getBitmapOffset(ImageView img, Boolean includeLayout) {
        int[] offset = new int[2];
        float[] values = new float[9];

        Matrix m = img.getImageMatrix();
        m.getValues(values);

        offset[0] = (int) values[2];
        offset[1] = (int) values[5];

        if (includeLayout) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) img.getLayoutParams();
            int paddingTop = (int) (img.getPaddingTop() );
            int paddingLeft = (int) (img.getPaddingLeft() );

            offset[0] += paddingTop + lp.topMargin;
            offset[1] += paddingLeft + lp.leftMargin;
        }
        return offset;
    }
}
