package io.informhunter.ipsapp;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by informhunter on 21.07.2017.
 */

public class StaticCollectorFragment extends Fragment implements View.OnTouchListener {

    private TouchImageView mapImageViewer;
    private TextView logTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.static_collector_fragment_layout, container, false);

        logTextView = (TextView) view.findViewById(R.id.logTextView);

        mapImageViewer = (TouchImageView) view.findViewById(R.id.touchImageView);
        mapImageViewer.setOnTouchListener(this);

        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int viewerPosition[] = new int[2];

        mapImageViewer.getLocationOnScreen(viewerPosition);

        int[] imageOffset = getBitmapOffset(mapImageViewer, true);

        logTextView.setText("");
        logTextView.append("X: " + String.valueOf(x) + "\n");
        logTextView.append("Y: " + String.valueOf(y) + "\n");
        logTextView.append("Image offset X: " + String.valueOf(imageOffset[0]) + "\n");
        logTextView.append("Image offset Y: " + String.valueOf(imageOffset[1]) + "\n");

        return false;
    }

    public static int[] getBitmapOffset(TouchImageView img,  Boolean includeLayout) {
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
