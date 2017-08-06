package io.informhunter.ipsapp;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import io.informhunter.ipsapp.util.Util;

/**
 * Created by informhunter on 25.07.2017.
 */

public class NavigationMapView extends TouchImageView implements OnTouchListener {

    Bitmap mapBitmap;
    float[] cursorPosition;
    float xLength = 13.90f, yLength = 7.35f;

    public NavigationMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Setup();
    }

    public NavigationMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Setup();
    }

    public NavigationMapView(Context context) {
        super(context);
        Setup();
    }

    private void Setup() {
        BitmapDrawable drawable = (BitmapDrawable) getDrawable();
        mapBitmap = drawable.getBitmap();
        cursorPosition = new float[2];
        setOnTouchListener(this);
    }

    public boolean onTouch(View view, MotionEvent event) {
        int[] imageOffset = Util.getBitmapOffset(this, true);
        float zoom = getCurrentZoom();
        cursorPosition[0] = (event.getX() - imageOffset[0]);// / zoom + imageOffset[0];
        cursorPosition[1] = (event.getY() - imageOffset[1]);// / zoom + imageOffset[1];
        redrawMap();

        return super.onTouchEvent(event);
    }

    private void redrawMap() {
        Bitmap mutableBitmap = mapBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        float scaleX, scaleY;
        scaleX = 1;//(float)mutableBitmap.getWidth() / xLength;
        scaleY = 1;//(float)mutableBitmap.getHeight() / yLength;

        paint.setColor(Color.GREEN);
        canvas.drawCircle(cursorPosition[0] * scaleX , cursorPosition[1] * scaleY, 25, paint);

        setImageBitmap(mutableBitmap);

        /*
        paint.setColor(Color.RED);
        for(float[] point : currentRoute) {
            canvas.drawCircle(point[0] * scaleX , point[1] * scaleY, 25, paint);
        }


        paint.setColor(Color.BLUE);
        canvas.drawCircle(currentPoint[0] * scaleX , currentPoint[1] * scaleY, 25, paint);
        */
    }



    public float[] getCursorPos() {
        return cursorPosition;
    }


}
