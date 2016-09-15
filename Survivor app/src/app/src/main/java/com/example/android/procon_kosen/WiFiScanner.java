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
import java.util.Calendar;
import java.util.List;

public class WiFiScanner extends Service {

    private WifiManager mainWifi;
    private Handler handler = new Handler();
    private Boolean mainStatus = false;
    private String temp = "ch";
    private String keyComand[] = {"on", "ff", "nt", "nf"};
    private String commands = "Null";
    private String target = "Null";
    private String ageGroup = "Null";
    private Context context;
    private ProfileHelper ph;

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
        String keyAge[] = {"aa", "ch"};
        String keyBlood[] = {"AA", ph.getBlood()};


        //Begin core loop
        doInback();
    }


    class WifiReceiver extends BroadcastReceiver {
        private String ssidKey = "kitsuchart";

        public void onReceive(Context c, Intent intent) {
            boolean detection = false;
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();



            for (int i = 0; i < wifiList.size(); i++) {

                if (SsidValidation(wifiList.get(i).SSID)) {
                    detection = true;
                    break;
                }
            }

            if (detection) {
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

        private boolean SsidValidation(String ssid) {

            //Check if ssid is valid
            int age = 0;
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
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
            else if (age <= 50)
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
                        if(ssid.equals(Integer.toString((formattedDate+keyComand[i]+keyAge[j]+keyBlood[k]).hashCode())))
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