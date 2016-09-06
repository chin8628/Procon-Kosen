package com.example.android.procon_kosen;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button editBtn;
    private boolean detection = false;
    private String commands;
    private int appStatus;
    private String detectedSSID = "None";
    private String ssidKey = "kitsuchart";
    private String onCommands = "on";
    private String offCommands = "ff";
    private String target;
    //WifiManager mainWifi;
    //WifiReceiver receiverWifi;
    AudioManager am;
    Uri notification;
    Ringtone r;
    SharedPreferences sharedpreferences;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Object
        //Request permisiion at runtime
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 1002);

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        mp = MediaPlayer.create(MainActivity.this, R.raw.loudalarm);
        mp.setLooping(true);

        //mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //receiverWifi = new WifiReceiver();
        //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //start WiFiscanner service
        Intent service = new Intent(this, WiFiScanner.class);
        startService(service);

        Intent mainBroadcaster = new Intent("mainBroadcaster");
        mainBroadcaster.putExtra("mainstatus", true);
        sendBroadcast(mainBroadcaster);

        TextView name = (TextView) findViewById(R.id.name);
        TextView birthday = (TextView) findViewById(R.id.birthday);
        TextView blood = (TextView) findViewById(R.id.blood);
        TextView sibling1 = (TextView) findViewById(R.id.sibling_phone1);
        TextView sibling2 = (TextView) findViewById(R.id.sibling_phone2);

        sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);
        name.setText(sharedpreferences.getString("name", ""));
        birthday.setText(sharedpreferences.getString("birthday", ""));
        blood.setText(sharedpreferences.getString("blood", ""));
        sibling1.setText(sharedpreferences.getString("sibling1", ""));
        sibling2.setText(sharedpreferences.getString("sibling2", ""));

        editBtn = (Button) findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditProfile.class);
                startActivity(i);

            }
        });

        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("IamHere")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(false)
                .setAutoCancel(false)
                .setContentText("You have been detected.");

        NotificationCompat.InboxStyle inBoxStyle = new NotificationCompat.InboxStyle();
        inBoxStyle.setBigContentTitle("Personal Details:");
        inBoxStyle.addLine("Siblings Contact");
        inBoxStyle.addLine(sharedpreferences.getString("sibling1", ""));
        inBoxStyle.addLine(sharedpreferences.getString("sibling2", ""));
        inBoxStyle.addLine("Blood Type " + sharedpreferences.getString("blood", ""));
        mBuilder.setStyle(inBoxStyle);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        registerReceiver(mMessageReceiver, new IntentFilter("command recived"));


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Intent mainBroadcaster = new Intent("mainBroadcaster");
        mainBroadcaster.putExtra("mainstatus", false);
        sendBroadcast(mainBroadcaster);
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String mCommand = null;
            String mtarget = null;
            mCommand= intent.getStringExtra("comamnds");
            mtarget= intent.getStringExtra("target");
            Log.v("Recieve", mCommand);
            if(mCommand != null && mtarget != null )
            {
                if(mtarget.equals("AA") || mtarget.equals(sharedpreferences.getString("blood", "")))
                {
                    switch (mCommand) {
                        case "on":
                            if (!mp.isPlaying()) {
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                                mp.start();
                                mNotificationManager.notify(512, mBuilder.build());
                            }
                            break;
                        case "ff":
                            mp.stop();
                            mNotificationManager.cancel(512);
                            break;
                        case "nt":
                            mNotificationManager.notify(512, mBuilder.build());
                            break;
                        case "nf":
                            mNotificationManager.cancel(512);
                            break;
                    }
                }
            }


        }
    };
}
