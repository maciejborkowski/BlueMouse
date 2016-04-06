package lex.bluemouse.transfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothOrderSenderAdapter implements OrderSender {
    public static final UUID BLUETOOTH_UUID = UUID.fromString("0e0165c9-0fa0-4f6f-93a5-f388b99395de");
    public static final String BLUETOOTH_TAG = "Bluetooth";

    private final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket socket = null;

    public BluetoothOrderSenderAdapter(BluetoothSocket socket) {
        this.socket = socket;
    }


    @Override
    public void moveMouse(int x, int y) {
        StringBuilder sb = new StringBuilder();
        char[] chars = Character.toChars(x);
        sb.append(chars);
        chars = Character.toChars(y);
        sb.append(chars);

        send(chars);
    }

    @Override
    public void openWebsite(String url) {
        char[] charUrl = url.toCharArray();
        send(charUrl);
    }

    private void send(char[] chars) {
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeChars(new String(chars) + "\n");
        } catch (IOException e) {
            Log.e(BLUETOOTH_TAG, "Cannot write to bluetooth socket", e);
        }
    }

    private void connect() {
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        if (pairedDevices.iterator().hasNext()) {
            BluetoothDevice device = pairedDevices.iterator().next();
            connect(device);
        } else {
            Log.i(BLUETOOTH_TAG, "No device available");
        }
    }

    private void connect(BluetoothDevice device) {
        Log.i(BLUETOOTH_TAG, "Beginning connection");
        try {
            try {
                socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
        adapter.cancelDiscovery();
            } catch (IOException e) {
                Log.e(BLUETOOTH_TAG, "Socket creation failed", e);
            }
            socket.connect();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e2) {
                Log.e(BLUETOOTH_TAG, "Unable to close() socket during connection failure", e2);
            }
        }
    }

}
