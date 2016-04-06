package lex.bluemouse.transfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

import static lex.bluemouse.transfer.BluetoothOrderSenderAdapter.BLUETOOTH_TAG;
import static lex.bluemouse.transfer.BluetoothOrderSenderAdapter.BLUETOOTH_UUID;

    public class ConnectThread extends Thread {
        private BluetoothSocket socket = null;
        private final BluetoothDevice device;
        private final BluetoothAdapter adapter;

        public ConnectThread(BluetoothDevice device, BluetoothAdapter adapter) {
            this.device = device;
            this.adapter = adapter;

            try {
                this.socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
            } catch (IOException e) {
                Log.e(BLUETOOTH_TAG, "create() failed", e);
            }
        }

        @Override
        public void run() {
            Log.i(BLUETOOTH_TAG, "Beginning connectThread");
            setName("BlueMouseBluetoothConnectThread");
            adapter.cancelDiscovery();
            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e2) {
                    Log.e(BLUETOOTH_TAG, "unable to close() socket during connection failure", e2);
                }
                return;
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(BLUETOOTH_TAG, "close() of connect socket failed", e);
            }
        }
    }

