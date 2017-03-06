package io.informhunter.datacollector;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private float[] currentPoint = new float[2];
    private Bitmap original;
    private List<DataPoint> data = new ArrayList<>();

    private boolean isCapturing = false;
    private BluetoothAdapter mAdapter;
    final private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            data.add(new DataPoint(device.getName(), rssi, currentPoint));

            TextView textView = (TextView) findViewById(R.id.textLog);
            textView.append("Discovered " + device.getName() + " ");
            textView.append(String.valueOf(rssi) + "\n");
        }
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        original = BitmapFactory.decodeResource(getResources(), R.drawable.cropped_flat, options);
    }


    public void onClickButtonCapture(View v) {
        TextView textView = (TextView) findViewById(R.id.textLog);

        ToggleButton btn = (ToggleButton) v;
        if (btn.isChecked()) {
            //Enable
            textView.setText("Start capture\n");
            isCapturing = true;
            mAdapter.startLeScan(leScanCallback);
        } else {
            //Disable
            textView.setText("Stop capture\n");
            mAdapter.stopLeScan(leScanCallback);
            isCapturing = false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TextView coordsText = (TextView) findViewById(R.id.coordsText);
        ImageView flatPlan = (ImageView) findViewById(R.id.flatPlan);

        if(isCapturing) {
            return false;
        }

        int loc[] = new int[2];
        flatPlan.getLocationOnScreen(loc);

        float x = event.getX();
        float y = event.getY();
        coordsText.setText(String.valueOf(x - loc[0]) + " " + String.valueOf(y - loc[1]) + "\n");
        drawPoint(x - loc[0], y - loc[1], false);

        x = (x - loc[0]) / flatPlan.getWidth() * 13.90f;
        y = (y - loc[1]) / flatPlan.getHeight() * 7.35f;

        currentPoint[0] = x;
        currentPoint[1] = y;

        coordsText.append(String.valueOf(x) + " " + String.valueOf(y) + "\n");

        return super.onTouchEvent(event);
    }

    private void drawPoint(float x, float y, boolean coordsAreReal) {
        ImageView flatPlan = (ImageView) findViewById(R.id.flatPlan);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

        Bitmap mutableBitmap = original.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);

        float scaleX, scaleY;
        if(coordsAreReal) {
            scaleX = (float)mutableBitmap.getWidth() / 13.90f;
            scaleY = (float)mutableBitmap.getHeight() / 7.35f;
        } else {
            scaleX = (float)mutableBitmap.getWidth() / (float)flatPlan.getWidth();
            scaleY = (float)mutableBitmap.getHeight() / (float)flatPlan.getHeight();
        }

        canvas.drawCircle(x * scaleX , y * scaleY, 25, paint);
        flatPlan.setImageBitmap(mutableBitmap);
    }

    public void onClickButtonClear(View v) {
        TextView textLog = (TextView) findViewById(R.id.textLog);
        textLog.setText("");
    }

    public void onClickButtonSave(View v) {
        TextView textLog = (TextView) findViewById(R.id.textLog);
        textLog.append("Total captures: " + String.valueOf(data.size()) + "\n");
        saveData(data);
    }

    private void saveData(List<DataPoint> data) {

        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "datacollector");
        boolean created = folder.mkdirs();

        final String filename = folder.toString() + "/" + "data.csv";


        try {
            FileWriter fw = new FileWriter(filename);
            fw.write("PosX,PosY,RSSI,Name,Timestamp\n");
            for(DataPoint dp : data) {
                fw.write(String.format(Locale.US, "%f,%f,%d,%s,%d\n",
                        dp.Point[0],
                        dp.Point[1],
                        dp.RSSI,
                        dp.Name,
                        dp.Timestamp));
            }
            fw.close();
        } catch (Exception e) {
            e.getMessage();
        }

    }


}