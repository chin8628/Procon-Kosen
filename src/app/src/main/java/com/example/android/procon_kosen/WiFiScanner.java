package com.example.android.procon_kosen;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class WiFiScanner extends Service {

    WifiManager mainWifi;
    private String status = "";
    Handler handler = new Handler();
    Boolean mainStatus = false;

    private BroadcastReceiver mStausReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            mainStatus = intent.getBooleanExtra("mainstatus", false);
            Log.v("Service", String.valueOf(mainStatus));
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
        // TODO Auto-generated method stub

        super.onCreate();

        Toast.makeText(WiFiScanner.this, "Service Started", Toast.LENGTH_SHORT).show();
        mainWifi = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiReceiver mWifi = new WifiReceiver();
        registerReceiver(mWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(mStausReceiver, new IntentFilter("mainBroadcaster"));

        doInback();
    }


    class WifiReceiver extends BroadcastReceiver {
        private String ssidKey = "kitsuchart";

        public void onReceive(Context c, Intent intent) {
            boolean detection = false;
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            String commands = "Null";
            String target = "Null";
            for (int i = 0; i < wifiList.size(); i++) {

                if (SsidValidation(wifiList.get(i).SSID)) {
                    commands = wifiList.get(i).SSID.substring(ssidKey.length(), ssidKey.length() + 2);
                    target = wifiList.get(i).SSID.substring(ssidKey.length() + 2);
                    detection = true;
                    break;
                }
            }

            if (detection) {
                Intent j = new Intent("command recived");
                j.putExtra("comamnds", commands);
                j.putExtra("target", target);
                sendBroadcast(j);
                Log.v("Broadcast", commands);
                Log.v("Broadcast", status);
                if (!mainStatus) {
                    Intent k = new Intent(WiFiScanner.this, MainActivity.class);
                    k.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(k);
                }
            }
        }

        private boolean SsidValidation(String ssid) {

            if (ssid.length() >= 14 && ssid.contains(ssidKey)) {
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