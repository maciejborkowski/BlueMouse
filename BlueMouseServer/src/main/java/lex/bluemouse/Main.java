package lex.bluemouse;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lex.bluemouse.transfer.BluetoothOrderReceiverAdapter;

public class Main extends Application {

    private Text statusText = new Text("Nothing happened");

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("BlueMouse Server");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(statusText, 1, 1);

        Scene scene = new Scene(grid, 300, 275);

        primaryStage.setScene(scene);
        primaryStage.show();

        registerOrderReceiver();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void registerOrderReceiver() {
        BluetoothOrderReceiverAdapter receiver = new BluetoothOrderReceiverAdapter(this::updateStatus);
        receiver.start();
    }

    private void updateStatus(String text) {
        statusText.setText(text);
    }
}
