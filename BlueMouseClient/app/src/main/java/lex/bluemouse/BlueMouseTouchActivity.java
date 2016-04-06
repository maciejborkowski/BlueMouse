package lex.bluemouse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import lex.bluemouse.transfer.BluetoothOrderSenderAdapter;

public class BlueMouseTouchActivity extends AppCompatActivity {
    private static final BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BluetoothOrderSenderAdapter bluetoothOrderSender = new BluetoothOrderSenderAdapter(myBluetoothAdapter);
    private final FoundDeviceReceiver foundDeviceReceiver = new FoundDeviceReceiver();

    private Button onBtn, offBtn, findBtn;
    private TextView text;
    private ListView myListView;
    private ArrayAdapter<String> deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_mouse_touch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findComponents();

        if (myBluetoothAdapter == null) {
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            findBtn.setEnabled(false);
            text.setText("Your device does not support Bluetooth");
        } else {
            onBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    on(v);
                }
            });
            offBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    off(v);
                }
            });
            findBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    find(v);
                }
            });

            myListView.setAdapter(deviceList);
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        bluetoothOrderSender.connect((String) myListView.getItemAtPosition(position));
                    } catch (Exception e) {
                        text.setText("Connection to device failed");
                        e.printStackTrace();
                    }
                    text.setText("Connected to device");
                }
            });
        }
    }

    private void findComponents() {
        onBtn = (Button) findViewById(R.id.turnOn);
        offBtn = (Button) findViewById(R.id.turnOff);
        findBtn = (Button) findViewById(R.id.search);
        text = (TextView) findViewById(R.id.text);
        myListView = (ListView) findViewById(R.id.listView1);
        deviceList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    }

    public void on(View view) {
        if (!myBluetoothAdapter.isEnabled()) {
            myBluetoothAdapter.enable();
            text.setText("Bluetooth turned on");
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_LONG).show();
        }
    }

    private class FoundDeviceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device.getName());
                deviceList.notifyDataSetChanged();
            }
        }
    }

    public void find(View view) {
        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        } else {
            deviceList.clear();
            myBluetoothAdapter.startDiscovery();

            registerReceiver(foundDeviceReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    public void off(View view) {
        myBluetoothAdapter.disable();
        text.setText("Bluetooth turned off");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(foundDeviceReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blue_mouse_touch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
