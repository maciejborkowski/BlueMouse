package lex.bluemouse.transfer;

import lex.bluemouse.action.ActionController;
import lex.bluemouse.action.MouseController;

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
    private UUID SERVICE_UUID = new UUID(SERVICE_UUID_STRING, false);

    public final String name = "BlueMouseServer";
    public final String url = "btspp://localhost:" + SERVICE_UUID + ";name=" + name + ";authenticate=false;encrypt=false;";
    public final Consumer<String> statusUpdater;

    public BluetoothOrderReceiverAdapter(Consumer<String> statusUpdater) {
        this.statusUpdater = statusUpdater;
    }

    public MouseController mouseController = new MouseController();
    public ActionController actionController = new ActionController();

    @Override
    public void run() {
        try {
            LocalDevice device = LocalDevice.getLocalDevice();
            device.setDiscoverable(DiscoveryAgent.GIAC);
            while (true) {
                StreamConnectionNotifier server = (StreamConnectionNotifier) Connector.open(url);
                statusUpdater.accept("Waiting for connection");
                StreamConnection connection = server.acceptAndOpen();
                statusUpdater.accept("Connection established.");
                DataInputStream input = new DataInputStream(connection.openInputStream());
                statusUpdater.accept("Listening for input.");

                boolean end = false;
                while (!end) {
                    String line = "";
                    char readChar;
                    while (input.available() > 0 && ((readChar = input.readChar()) > 0) && (readChar != '\n')) {
                        line = line + readChar;
                    }
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    end = processCommand(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean processCommand(String command) {
        System.out.println("Received command: " + command);
        statusUpdater.accept("Last command: " + command);
        if (command.startsWith("MOVE")) {
            String[] splitCommand = command.split(":");
            int x = Integer.valueOf(splitCommand[2]);
            int y = Integer.valueOf(splitCommand[4]);
            if (splitCommand[0].contains("IMP")) {
                mouseController.moveAnimate(x, y);
            } else {
                mouseController.move(x, y);
            }
        } else if (command.startsWith("CLICK")) {
            if (command.contains("LEFT")) {
                mouseController.clickLeft();
            } else if (command.contains("RIGHT")) {
                mouseController.clickRight();
            }
        } else if (command.startsWith("WEB")) {
            String[] splitCommand = command.split(":");
            String website = splitCommand[1];
            actionController.openWebsite(website);
        } else if (command.startsWith("END")) {
            return true;
        }
        return false;
    }
}
