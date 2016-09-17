package com.example.android.procon_kosen;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
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
    private String temp = "ch";
    private String keyComand[] = {"on", "ff", "nt", "nf"};
    private String commands = "Null";
    private String target = "Null";
    private String ageGroup = "Null";
    private Context context;
    private ProfileHelper ph;
    private Handler handler;
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

        ph = new ProfileHelper(this);

        //Initalize objects
        mainWifi = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiReceiver mWifi = new WifiReceiver();
        registerReceiver(mWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(mStausReceiver, new IntentFilter("mainBroadcaster"));
        handler = new Handler();
        String keyAge[] = {"aa", "ch"};
        String keyBlood[] = {"AA", ph.getBlood()};

        handler.post(runnableCode);

    }


    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            List<String> ssidList = new ArrayList<String>();
            for (int i = 0; i < wifiList.size(); i++) {
                ssidList.add(wifiList.get(i).SSID);
            }

            if (SsidValidation(ssidList)) {
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
            wifiList.clear();
            ssidList.clear();
            handler.postDelayed(runnableCode, 30000);
        }

    private boolean SsidValidation(List<String> ssidList) {

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
                        ageGroup = keyAge[j];
                        target = keyBlood[k];
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            mainWifi.startScan();
        }
    };
}