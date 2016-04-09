package lex.bluemouse.transfer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import static lex.bluemouse.BluetoothDiscoveryActivity.BLUETOOTH_ADAPTER;

public class BluetoothOrderSenderAdapter implements OrderSender {
    private static final UUID BLUETOOTH_UUID = UUID.fromString("f1db4b40-8de7-11e4-bd61-0002a5d5c51b");
    private static final String RFCOMM_SOCKET_STRING = "createRfcommSocket";
    private static final String BLUETOOTH_TAG = "Bluetooth";

    private BluetoothSocket socket = null;

    @Override
    public void moveMouse(int x, int y) {
        StringBuilder builder = new StringBuilder();
        char[] chars = Character.toChars(x);
        builder.append("X:");
        builder.append(chars);
        chars = Character.toChars(y);
        builder.append(":Y:");
        builder.append(chars);

        send(builder.toString().toCharArray());
    }

    @Override
    public void openWebsite(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("WEB:");
        char[] charUrl = url.toCharArray();
        builder.append(charUrl);
        send(builder.toString().toCharArray());
    }

    private void send(char[] chars) {
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeChars(new String(chars) + "\n");
        } catch (IOException e) {
            Log.e(BLUETOOTH_TAG, "Cannot write to bluetooth socket", e);
        }
    }

    public void connect(final String deviceName) throws Exception {
        BluetoothDevice device = findDeviceByName(deviceName);

        try {
            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
        } catch (Exception e) {
            Log.e(BLUETOOTH_TAG, "Error creating socket");
        }
        try {
            socket.connect();
        } catch (IOException e) {
            Log.w(BLUETOOTH_TAG, "Trying fallback...");
            socket = (BluetoothSocket) device.getClass().getMethod(RFCOMM_SOCKET_STRING, new Class[]{int.class}).invoke(device, 1);
            socket.connect();
            Log.w(BLUETOOTH_TAG, "Fallback connected");
        }
    }


    private BluetoothDevice findDeviceByName(final String deviceName) throws Resources.NotFoundException {
        for (BluetoothDevice pairedDevice : BLUETOOTH_ADAPTER.getBondedDevices()) {
            if (pairedDevice.getName().equals(deviceName)) {
                return pairedDevice;
            }
        }
        throw new Resources.NotFoundException("Not found device with name " + deviceName);
    }

}
