package io.informhunter.locator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    final private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            data.add(new RSSIDataPoint(device.getName(), rssi, scanRecord));
            TextView textView = (TextView) findViewById(R.id.textLog);
            textView.append("Discovered " + device.getName() + " ");
            textView.append(String.valueOf(rssi) + "\n");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
