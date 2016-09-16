package com.example.android.rescuerapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private String mSSIDName;

    private TextView mSsid;

    private Spinner mCommand_spinner;
    private Spinner mAge_spinner;
    private Spinner mBlood_spinner;

    private Button mGenBtn;
    private Button mCopyBtn;
    private Button mHotspotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSsid = (TextView) findViewById(R.id.ssid);

        mCommand_spinner = (Spinner) findViewById(R.id.command_spinner);
        ArrayAdapter<CharSequence> command_adapter = ArrayAdapter.createFromResource(this,
                R.array.command_spinner, android.R.layout.simple_spinner_item);
        command_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCommand_spinner.setAdapter(command_adapter);

        mAge_spinner = (Spinner) findViewById(R.id.age_spinner);
        ArrayAdapter<CharSequence> age_adapter = ArrayAdapter.createFromResource(this,
                R.array.age_spinner, android.R.layout.simple_spinner_item);
        age_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAge_spinner.setAdapter(age_adapter);

        mBlood_spinner = (Spinner) findViewById(R.id.blood_spinner);
        ArrayAdapter<CharSequence> blood_adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_spinner, android.R.layout.simple_spinner_item);
        blood_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBlood_spinner.setAdapter(blood_adapter);

        mGenBtn = (Button) findViewById(R.id.generate_btn);
        mGenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String command, age, blood;

                // Define command to get preHash command easier
                HashMap commandMap = new HashMap<>();
                commandMap.put("ON", "on");
                commandMap.put("OFF", "ff");

                // Get command from spinner then convert to preHash
                command = mCommand_spinner.getSelectedItem().toString().toUpperCase();
                command = (String) commandMap.get(command);

                // Define age to get preHash command easier
                HashMap ageMap = new HashMap<>();
                ageMap.put("ALL", "aa");
                ageMap.put("CHILDREN", "ch");
                ageMap.put("ADULT", "ad");
                ageMap.put("ELDER", "ed");

                // Get age from spinner then convert to preHash
                age = mAge_spinner.getSelectedItem().toString().toUpperCase();
                age = (String) commandMap.get(age);

                // Define blood to get preHash command easier
                HashMap bloodMap = new HashMap<>();
                bloodMap.put("ALL", "AA");
                bloodMap.put("A", "A");
                bloodMap.put("B", "B");
                bloodMap.put("O", "O");
                bloodMap.put("AB", "AB");

                // Get blood from spinner then convert to preHash
                blood = mBlood_spinner.getSelectedItem().toString().toUpperCase();
                blood = (String) commandMap.get(blood);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());
                mSSIDName = Integer.toString((formattedDate + command + age + blood).hashCode());
                mSsid.setText(mSSIDName);
            }
        });

        mCopyBtn = (Button) findViewById(R.id.copy_btn);
        mCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", mSSIDName);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), "SSID is copied to Clipboard",Toast.LENGTH_SHORT).show();
            }
        });

        mHotspotBtn = (Button) findViewById(R.id.hotspot_btn);
        mHotspotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tetherSettings = new Intent();
                tetherSettings.setClassName("com.android.settings", "com.android.settings.TetherSettings");
                startActivity(tetherSettings);
            }
        });

    }

}
