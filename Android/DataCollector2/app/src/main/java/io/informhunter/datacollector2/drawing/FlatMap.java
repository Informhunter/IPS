package io.informhunter.datacollector2.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by informhunter on 19.06.2017.
 */

public class FlatMap {
    private List<float[]> currentRoute;
    private float[] cursorPoint;
    private Bitmap original;
    private float xLength, yLength;

    public FlatMap(Bitmap mapBitmap, float xlength, float ylength) {
        currentRoute = new ArrayList<>();
        cursorPoint = new float[]{-5, -5};
        original = mapBitmap;//mapBitmap.copy(Bitmap.Config.ARGB_8888, true);
        xLength = xlength;
        yLength = ylength;
    }

    public Bitmap Render() {
        Bitmap mutableBitmap = original.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

        float scaleX, scaleY;
        scaleX = (float)mutableBitmap.getWidth() / xLength;
        scaleY = (float)mutableBitmap.getHeight() / yLength;

        for(float[] point : currentRoute) {
            canvas.drawCircle(point[0] * scaleX , point[1] * scaleY, 25, paint);
        }

        paint.setColor(Color.GREEN);
        canvas.drawCircle(cursorPoint[0] * scaleX , cursorPoint[1] * scaleY, 25, paint);

        return mutableBitmap;
    }

    public void SetCursor(float loc[]) {
        cursorPoint[0] = loc[0] * xLength;
        cursorPoint[1] = loc[1] * yLength;
    }

    public float[] GetCursor() {
        return cursorPoint.clone();
    }

    public void AddRoutePoint(float[] point) {
        currentRoute.add(point.clone());
    }

    public void ClearRoute() {
        currentRoute.clear();
    }

}
