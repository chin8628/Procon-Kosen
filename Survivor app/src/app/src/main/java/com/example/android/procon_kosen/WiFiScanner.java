package com.example.android.procon_kosen;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WiFiScanner extends Service {

    private WifiManager mainWifi;
    private Boolean mainStatus = false;
    private String keyComand[] = {"on", "ff", "nt", "nf"};
    private String commands = "Null";
    private String target = "Null";
    private ProfileHelper ph;
    private Handler handler;
    private boolean slience = false;
    private BroadcastReceiver mStausReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            mainStatus = intent.getBooleanExtra("mainstatus", false);
        }

    };
    private BroadcastReceiver  mSlienceReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            slience = true;
        }
    };

    public WiFiScanner() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        super.onCreate();

        ph = new ProfileHelper(this);

        //Initalize objects
        mainWifi = (WifiManager) this.getSystemService(this.WIFI_SERVICE);
        WifiReceiver mWifi = new WifiReceiver();
        registerReceiver(mWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(mStausReceiver, new IntentFilter("mainBroadcaster"));
        registerReceiver(mSlienceReciver, new IntentFilter("slience b"));
        handler = new Handler();

        WifiManager.WifiLock wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        wifiLock.acquire();


        handler.post(runnableCode);
        handler.post(sendCode);

    }


    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            List<String> ssidList = new ArrayList<>();
            for (int i = 0; i < wifiList.size(); i++) {
                ssidList.add(wifiList.get(i).SSID);
            }
            Log.v("asd", ssidList.toString());
            SsidValidation(ssidList);
            wifiList.clear();
            ssidList.clear();
            handler.postDelayed(runnableCode, 4500);
        }

    private void SsidValidation(List<String> ssidList) {

        //Check if ssid is valid
        int age = 0;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        String keyBlood[] = {"AA", ph.getBlood()};
        try {
            age = ph.getAge();
        }
        catch (ParseException e){
            // Nothing do anything
        }
        String temp;
        if(age <= 15)
        {
            temp = "ch";
        }
        else if (age <= 60)
        {
            temp = "ad";
        }
        else
        {
            temp = "ed";
        }
        String keyAge[] = {"aa", temp};
        int i, j ,k;
        for(i=0;i<4;i++)
        {
            for(j=0;j<2;j++)
            {
                for(k=0;k<2;k++)
                {
                    if(ssidList.contains(Integer.toString((formattedDate+keyComand[i]+keyAge[j]+keyBlood[k]).hashCode())))
                    {
                        commands = keyComand[i];
                        target = keyBlood[k];
                        return;
                    }
                }
            }
        }
    }
}

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            mainWifi.startScan();
        }
    };

    private Runnable sendCode = new Runnable() {
        @Override
        public void run() {

            Log.v("asd", mainStatus.toString());
            if((commands.equals("on") || commands.equals("ff")) && !slience){
                if (!mainStatus) {
                    Intent k = new Intent(WiFiScanner.this, MainActivity.class);
                    k.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(k);
                }
                Intent j = new Intent("command recived");
                j.putExtra("comamnds", commands);
                j.putExtra("target", target);
                sendBroadcast(j);
            }
            slience = false;
            commands = "NULL";
            target = "NULL" ;
            handler.postDelayed(sendCode, 10000);
        }
    };

}
