package com.example.android.rescuerapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    private Button mButton;
    private EditText mEditText;
    private TextView mTextView;
    private String ssid = "NULL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());
                ssid = Integer.toString((formattedDate + mEditText.getText()).hashCode());
                mTextView.setText(ssid);

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", ssid);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), "SSID is copied to Clipboard",Toast.LENGTH_SHORT).show();

                Intent tetherSettings = new Intent();
                tetherSettings.setClassName("com.android.settings", "com.android.settings.TetherSettings");
                startActivity(tetherSettings);
            }
        });
    }

}
