package io.informhunter.datacollector;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<float[]> currentRoute = new ArrayList<>();
    private Iterator<float[]> routeIterator;
    private float[] currentPoint = new float[2];

    private float[] cursorPoint = new float[2];
    private Bitmap original;
    private List<DataPoint> data = new ArrayList<>();

    private boolean isCapturing = false;
    private BluetoothAdapter mAdapter;

    final private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            data.add(new RSSIDataPoint(device.getName(), rssi, scanRecord));
            TextView textView = (TextView) findViewById(R.id.textLog);
            textView.append("Discovered " + device.getName() + " ");
            textView.append(String.valueOf(rssi) + "\n");
        }
    };


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        verifyStoragePermissions(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        original = BitmapFactory.decodeResource(getResources(), R.drawable.cropped_flat, options);
    }


    public void onClickButtonCapture(View v) {
        TextView textView = (TextView) findViewById(R.id.textLog);
        Button btnNext = (Button) findViewById(R.id.nextPointButton);

        ToggleButton btn = (ToggleButton) v;
        if (btn.isChecked()) {
            //Enable
            textView.setText("Start capture\n");
            routeIterator = currentRoute.iterator();
            if(!routeIterator.hasNext()) {
                btn.toggle();
                return;
            }
            float[] point = routeIterator.next();
            data.add(new PositionDataPoint(point[0], point[1]));
            btnNext.setEnabled(true);
            btnNext.callOnClick();
            isCapturing = true;
            mAdapter.startLeScan(leScanCallback);
        } else {
            //Disable
            textView.setText("Stop capture\n");
            btnNext.setEnabled(false);
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
        //drawPoint(x - loc[0], y - loc[1], false);

        x = (x - loc[0]) / flatPlan.getWidth() * 13.90f;
        y = (y - loc[1]) / flatPlan.getHeight() * 7.35f;

        cursorPoint[0] = x;
        cursorPoint[1] = y;

        coordsText.append(String.valueOf(x) + " " + String.valueOf(y) + "\n");

        redrawPlan();

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

    private void redrawPlan() {
        ImageView flatPlan = (ImageView) findViewById(R.id.flatPlan);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

        Bitmap mutableBitmap = original.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        float scaleX, scaleY;
        scaleX = (float)mutableBitmap.getWidth() / 13.90f;
        scaleY = (float)mutableBitmap.getHeight() / 7.35f;

        for(float[] point : currentRoute) {
            canvas.drawCircle(point[0] * scaleX , point[1] * scaleY, 25, paint);
        }

        paint.setColor(Color.BLUE);
        canvas.drawCircle(currentPoint[0] * scaleX , currentPoint[1] * scaleY, 25, paint);

        paint.setColor(Color.GREEN);
        canvas.drawCircle(cursorPoint[0] * scaleX , cursorPoint[1] * scaleY, 25, paint);

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


    public void onAddPointButtonClick(View v) {
        currentRoute.add(cursorPoint.clone());
        redrawPlan();
    }

    public void onResetRouteButtonClick(View v) {
        currentRoute.clear();
        redrawPlan();
    }

    public void onNextPointButtonClick(View v) {
        data.add(new PositionDataPoint(currentPoint[0], currentPoint[1]));
        if(routeIterator.hasNext()) {
            currentPoint = routeIterator.next();
            redrawPlan();
        } else {
            ToggleButton tb = (ToggleButton)findViewById(R.id.toggleCaptureButton);
            tb.toggle();
            tb.callOnClick();
        }
    }


    private void saveData(List<DataPoint> data) {

        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "datacollector");
        boolean created = folder.mkdirs();

        String positionDataFile = folder.toString() + "/" + "position_data.csv";
        String rssiDataFile = folder.toString() + "/" + "rssi_data.csv";
        String laDataFile = folder.toString() + "/" + "la_data.csv";



        try {
            FileWriter posFW = new FileWriter(positionDataFile);
            PositionDataPoint.WriteHeaderToFile(posFW);

            FileWriter rssiFW = new FileWriter(rssiDataFile);
            RSSIDataPoint.WriteHeaderToFile(rssiFW);

            FileWriter laFW = new FileWriter(laDataFile);
            VectorDataPoint.WriteHeaderToFile(laFW);

            for(DataPoint dp : data) {
                switch (dp.GetPointType()) {
                    case Position:
                        dp.WriteToFile(posFW);
                        break;
                    case RSSI:
                        dp.WriteToFile(rssiFW);
                        break;
                    case LinearAcceleration:
                        dp.WriteToFile(laFW);
                        break;
                }
            }
            posFW.close();
            rssiFW.close();
            laFW.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void onSendButtonClick(View v) {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "datacollector");

        String positionDataFileName = folder.toString() + "/" + "position_data.csv";
        String rssiDataFileName = folder.toString() + "/" + "rssi_data.csv";
        TextView textLog = (TextView) findViewById(R.id.textLog);

        EditText urlEdit = (EditText) findViewById(R.id.collectorURLEdit);


        try {
            MultipartUtility multipart = new MultipartUtility(urlEdit.getText().toString(), "UTF-8");
            File rssiDataFile = new File(rssiDataFileName);
            File positionDataFile = new File(positionDataFileName);
            multipart.addFilePart("rssi_data", rssiDataFile);
            multipart.addFilePart("position_data", positionDataFile);
            multipart.finish();
        } catch (Exception e) {
            textLog.append("Exception: " + e.toString() + "\n");
        }
        textLog.append("Send data\n");
    }
}