package lex.bluemouse.transfer;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.DataInputStream;
import java.util.function.Consumer;

public class BluetoothOrderReceiverAdapter extends Thread {
    private static final String SERVICE_UUID_STRING = "f1db4b408de711e4bd610002a5d5c51b";
    private UUID  SERVICE_UUID = new UUID(SERVICE_UUID_STRING, false);

    public final String name = "BlueMouseServer";
    public final String url = "btspp://localhost:" + SERVICE_UUID + ";name=" + name + ";authenticate=false;encrypt=false;";
    public final Consumer<String>  statusUpdater;

    public BluetoothOrderReceiverAdapter(Consumer<String> statusUpdater) {
        this.statusUpdater = statusUpdater;
    }

    @Override
    public void run() {
        try {
            LocalDevice device = LocalDevice.getLocalDevice();
            device.setDiscoverable(DiscoveryAgent.GIAC);

            StreamConnectionNotifier server = (StreamConnectionNotifier) Connector.open(url);
            statusUpdater.accept("Waiting for connection");
            StreamConnection connection = server.acceptAndOpen();
            statusUpdater.accept("Connection established.");
            DataInputStream input = new DataInputStream(connection.openInputStream());
            statusUpdater.accept("Listening for input.");
            while (true) {
                String line = "";
                char readChar;
                while (((readChar = input.readChar()) > 0) && (readChar != '\n')) {
                    line = line + readChar;
                }
                System.out.println("Received " + line);
                statusUpdater.accept("Last received: " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
