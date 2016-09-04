package com.example.android.procon_kosen;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.renderscript.RenderScript;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    StringBuilder sb = new StringBuilder();
    Handler handler = new Handler();
    AudioManager am;
    Uri notification;
    Ringtone r;
    SharedPreferences sharedpreferences;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);

        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        TextView name = (TextView) findViewById(R.id.name);
        TextView blood = (TextView) findViewById(R.id.blood);
        TextView sibling1 = (TextView) findViewById(R.id.sibling_phone1);
        TextView sibling2 = (TextView) findViewById(R.id.sibling_phone2);

        sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);
        name.setText(sharedpreferences.getString("name", ""));
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
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentText("You have been detected.");
        //Intent resultIntent = new Intent(this, MainActivity.class);
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
       // stackBuilder.addParentStack(MainActivity.class);
        //stackBuilder.addNextIntent(resultIntent);
        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        doInback();

    }

    class WifiReceiver extends BroadcastReceiver
    {

        public void onReceive(Context c, Intent intent)
        {
            detection = false;
            ArrayList<String> connections=new ArrayList<String>();
            ArrayList<Float> Signal_Strenth= new ArrayList<Float>();
            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            for(int i = 0; i < wifiList.size(); i++)
            {

                connections.add(wifiList.get(i).SSID);
                if(wifiList.get(i).SSID.length() >= 14 && wifiList.get(i).SSID.contains(ssidKey))
                {
                    if(wifiList.get(i).SSID.substring(ssidKey.length()+2).equals("AA") ||wifiList.get(i).SSID.substring(ssidKey.length()+2).equals(sharedpreferences.getString("blood", "")))
                    {
                        detection = true;
                        detectedSSID = wifiList.get(i).SSID;
                        commands = detectedSSID.substring(ssidKey.length(), ssidKey.length()+2);
                        target = detectedSSID.substring(ssidKey.length()+2);
                    }
                }
            }

        }
    }

    public void doInback()
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                //receiverWifi = new WifiReceiver();
                //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mainWifi.startScan();
                if (detection)
                {
                    if (!r.isPlaying() && commands.equals(onCommands))
                    {
                        am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), am.getStreamMaxVolume(AudioManager.STREAM_ALARM));
                        r.play();
                        mNotificationManager.notify(512, mBuilder.build());
                    }
                    else if (commands.equals(offCommands)){
                        r.stop();
                        mNotificationManager.cancel(512);
                    }
                }
                doInback();
            }
        }, 1000);
    }

    @Override
    protected void onPause()
    {
        //unregisterReceiver(receiverWifi);
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
}
