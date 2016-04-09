package lex.bluemouse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static lex.bluemouse.BluetoothDiscoveryActivity.BLUETOOTH_ORDER_SENDER;

public class ActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        Button redditButton = (Button) findViewById(R.id.reddit);
        redditButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BLUETOOTH_ORDER_SENDER.openWebsite("www.reddit.com");
            }
        });
    }

}
