package lex.bluemouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static lex.bluemouse.BluetoothDiscoveryActivity.BLUETOOTH_ORDER_SENDER;

public class ActionActivity extends AppCompatActivity {
    private AccelerometerListener accelerometerListener;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accelerometerListener = new AccelerometerListener(getPreferredSensitivityX(), getPreferredSensitivityY(), getPreferredImproved());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Button webButton = (Button) findViewById(R.id.web);
        webButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText urlText = (EditText) findViewById(R.id.url);
                BLUETOOTH_ORDER_SENDER.openWebsite(urlText.getText().toString());
            }
        });

        Button leftButton = (Button) findViewById(R.id.left);
        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BLUETOOTH_ORDER_SENDER.clickLeftMouse();
            }
        });

        Button rightButton = (Button) findViewById(R.id.right);
        rightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BLUETOOTH_ORDER_SENDER.clickRightMouse();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerometerListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BLUETOOTH_ORDER_SENDER.endConnection();
    }

    private int getPreferredSensitivityX() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(prefs.getString("sens_x", "100"));
    }

    private int getPreferredSensitivityY() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(prefs.getString("sens_y", "80"));
    }

    private boolean getPreferredImproved() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("imp_draw", true);
    }

    private class AccelerometerListener implements SensorEventListener {
        private float xLast, yLast;
        private int xSensitivity, ySensitivity;
        private final boolean improved;

        public AccelerometerListener(int xSensitivity, int ySensitivity, boolean improved) {
            this.xSensitivity = xSensitivity;
            this.ySensitivity = ySensitivity;
            this.improved = improved;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                return;
            }

            float x = event.values[0];
            float y = event.values[1];

            float xMove = xLast - x;
            float yMove = y - yLast;
            if (Math.abs(xMove) > 0.08 || Math.abs(yMove) > 0.08) {
                new MouseMovementSender((int) (xMove * xSensitivity), (int) (yMove * ySensitivity), improved).run();
                xLast = x;
                yLast = y;
            }
        }

        private final class MouseMovementSender implements Runnable {
            private final int xMove, yMove;
            private final boolean improved;

            public MouseMovementSender(int xMove, int yMove, boolean improved) {
                this.xMove = xMove;
                this.yMove = yMove;
                this.improved = improved;
            }

            @Override
            public void run() {
                BLUETOOTH_ORDER_SENDER.moveMouse(xMove, yMove, improved);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
