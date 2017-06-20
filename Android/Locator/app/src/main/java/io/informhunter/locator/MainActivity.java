package io.informhunter.locator;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.informhunter.locator.data.RSSIDataPoint;
import io.informhunter.locator.drawing.FlatMap;
import io.informhunter.locator.positioning.Location;
import io.informhunter.locator.positioning.Locator;

public class MainActivity extends AppCompatActivity {

    private List<Float> diffs;
    private float diffMean = 0.0f;
    private float diffStd = 0.0f;
    private float resultStd = 0.0f;
    private float resultStdTotal = 0.0f;
    private Locator locator;
    private BluetoothAdapter mAdapter;
    private FlatMap flatMap;

    private int counter = 0;

    final private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            locator.AddDataPoint(new RSSIDataPoint(device.getName(), rssi, scanRecord));

            TextView textView = (TextView) findViewById(R.id.textLog);

            Location l = locator.GetLocation();
            float[] currentPoint = new float[2];
            currentPoint[0] = l.GetX();
            currentPoint[1] = l.GetY();
            flatMap.SetCurrentPoint(currentPoint);
            float[] cursorPoint = flatMap.GetCursor();

            float diff = (float)Math.sqrt((currentPoint[0] - cursorPoint[0]) * (currentPoint[0] - cursorPoint[0]) +
                    (currentPoint[1] - cursorPoint[1]) * (currentPoint[1] - cursorPoint[1]));
            diffs.add(diff);

            if(counter > 10) {
                counter = 0;
                //diffMean = Util.Mean(diffs);
                //diffStd = Util.Std(diffs);
                resultStd = Util.ResultStd(diffs);
            }
            counter++;

            textView.setText("");
            //textView.append("Diff mean: " + String.valueOf(diffMean) + "\n");
            //textView.append("Diff std: " + String.valueOf(diffStd) + "\n");
            textView.append("Result std: " + String.valueOf(resultStd) + "\n");


            Map<Integer, Float> averages = locator.GetAverages();
            for(int minor : averages.keySet()) {
                textView.append(String.valueOf(minor) + ": ");
                textView.append(String.valueOf(averages.get(minor)) + "\n");
            }

            textView.append("Best dist: ");
            textView.append(String.valueOf(locator.GetBestDistance()) + "\n");

            redrawPlan();
        }
    };

    private static final int REQUEST_PERMISSIONS = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSIONS
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        verifyStoragePermissions(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.cropped_flat, options);
        flatMap = new FlatMap(original, 13.90f, 7.35f);

        diffs = new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TextView coordsText = (TextView) findViewById(R.id.coordsText);
        ImageView flatPlan = (ImageView) findViewById(R.id.flatPlan);

        int loc[] = new int[2];
        flatPlan.getLocationOnScreen(loc);

        float x = event.getX();
        float y = event.getY();
        coordsText.setText(String.valueOf(x - loc[0]) + " " + String.valueOf(y - loc[1]) + "\n");

        x = (x - loc[0]) / flatPlan.getWidth();
        y = (y - loc[1]) / flatPlan.getHeight();

        float[] cursorPoint = new float[2];

        cursorPoint[0] = x;
        cursorPoint[1] = y;

        flatMap.SetCursor(cursorPoint);

        coordsText.append(String.valueOf(x) + " " + String.valueOf(y) + "\n");

        redrawPlan();

        return super.onTouchEvent(event);
    }

    private void redrawPlan() {
        ImageView flatPlan = (ImageView) findViewById(R.id.flatPlan);
        flatPlan.setImageBitmap(flatMap.Render());
    }


    public void onClickButtonLocate(View v) {
        TextView textView = (TextView) findViewById(R.id.textLog);

        locator = new Locator();

        ToggleButton btn = (ToggleButton) v;
        if (btn.isChecked()) {
            //diffs.clear();
            //Enable
            textView.setText("Capture\n");
            mAdapter.startLeScan(leScanCallback);
        } else {
            //Disable
            textView.setText("Stop capture\n");
            mAdapter.stopLeScan(leScanCallback);
        }
    }

    public void onUpdateButtonClick(View v) {
        EditText url = (EditText)findViewById(R.id.collectorURLEdit);
        TextView textView = (TextView) findViewById(R.id.textLog);
        textView.append("Try download " + url.getText().toString());
        textView.append("\n");
        new DownloadFileFromURL().execute(url.getText().toString());
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            TextView textView = (TextView) findViewById(R.id.textLog);
            textView.append("Download started\n");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            String mapFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    + "/" + "datacollector" + "/" + "map.csv";
            try {
                HttpDownloadUtility.downloadFile(f_url[0], mapFileName);
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            TextView textView = (TextView) findViewById(R.id.textLog);
            textView.append("Download finished\n");
        }

    }
}

