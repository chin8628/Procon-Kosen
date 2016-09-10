package com.example.android.procon_kosen;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class WiFiScanner extends Service {

    private WifiManager mainWifi;
    private Handler handler = new Handler();
    private Boolean mainStatus = false;
    private SharedPreferences sharedpreferences;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    Security s = new Security();

    private BroadcastReceiver mStausReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            mainStatus = intent.getBooleanExtra("mainstatus", false);
        }

    };

    public WiFiScanner() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        super.onCreate();


        //Initalize objects
        mainWifi = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiReceiver mWifi = new WifiReceiver();
        registerReceiver(mWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(mStausReceiver, new IntentFilter("mainBroadcaster"));
        sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);

        //Begin core loop
        doInback();
    }


    class WifiReceiver extends BroadcastReceiver {
        private String ssidKey = "kitsuchart";
        ProfileHelper ph = new ProfileHelper();
        public void onReceive(Context c, Intent intent) {
            boolean detection = false;
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            String commands = "Null";
            String target = "Null";
            String ageGroup = "Null";
            for (int i = 0; i < wifiList.size(); i++) {

                if (SsidValidation(wifiList.get(i).SSID)) {
                    commands = wifiList.get(i).SSID.substring(ssidKey.length(), ssidKey.length() + 2);
                    target = wifiList.get(i).SSID.substring(ssidKey.length() + 2);
                    detection = true;
                    break;
                }
            }

            if (detection) {
                if(target.equals("AA") || target.equals(sharedpreferences.getString("blood", "")))
                {
                    Intent j = new Intent("command recived");
                    j.putExtra("comamnds", commands);
                    j.putExtra("target", target);
                    sendBroadcast(j);
                    if (!mainStatus) {
                        Intent k = new Intent(WiFiScanner.this, MainActivity.class);
                        k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(k);
                    }
                }
            }
        }

        private boolean SsidValidation(String ssid) {


            if (ssid.length() >= 13 && ssid.contains(ssidKey)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void doInback() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainWifi.startScan();
                doInback();
            }
        }, 1000);
    }

}