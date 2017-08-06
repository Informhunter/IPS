package io.informhunter.ipsapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.informhunter.ipsapp.util.Util;

/**
 * Created by informhunter on 21.07.2017.
 */

public class StaticCollectorFragment extends Fragment implements View.OnTouchListener {

    TouchImageView mapImageViewer;
    TextView logTextView;
    Bitmap mapBitmap;
    float[] cursorPosition;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.static_collector_fragment_layout, container, false);

        logTextView = (TextView) view.findViewById(R.id.logTextView);

        mapImageViewer = (TouchImageView) view.findViewById(R.id.touchImageView);
        mapImageViewer.setOnTouchListener(this);

        BitmapDrawable drawable = (BitmapDrawable) mapImageViewer.getDrawable();
        mapBitmap = drawable.getBitmap().copy(Bitmap.Config.ARGB_8888, true);

        cursorPosition = new float[2];

        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        int viewerPosition[] = new int[2];
        mapImageViewer.getLocationOnScreen(viewerPosition);

        PointF scrollPosition = mapImageViewer.getScrollPosition();
        int[] imageOffset = Util.getBitmapOffset(mapImageViewer, true);
        float zoom = mapImageViewer.getCurrentZoom();

        x = x - imageOffset[0];
        y = y - imageOffset[1];

        cursorPosition[0] = x;
        cursorPosition[1] = y;



        logTextView.setText("");
        logTextView.append("X: " + String.valueOf(x) + "\n");
        logTextView.append("Y: " + String.valueOf(y) + "\n");
        RectF rectF = mapImageViewer.getZoomedRect();
        logTextView.append("Image left: " + String.valueOf(rectF.left) + "\n");
        logTextView.append("Image top: " + String.valueOf(rectF.top) + "\n");
        logTextView.append("Image right: " + String.valueOf(rectF.right) + "\n");
        logTextView.append("Image bottom: " + String.valueOf(rectF.bottom) + "\n");

        logTextView.append("Zoom: " + String.valueOf(zoom) + "\n");

        redrawMap();
        return false;
    }

    private void redrawMap() {
        Bitmap mutableBitmap = mapBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(Color.GREEN);
        canvas.drawCircle(cursorPosition[0], cursorPosition[1], 25, paint);

        mapImageViewer.setImageBitmap(mutableBitmap);
    }
}
