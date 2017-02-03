package com.example.android.procon_kosen;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WiFiScanner extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private WifiManager mainWifi;
    private Boolean mainStatus = false;
    private String keyComand[] = {"on", "ff", "nt", "nf"};
    private String commands = "Null";
    private String target = "Null";
    private ProfileHelper ph;
    private Handler handler;
    private boolean slience = false;
    private String ssidName = "";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    List<String> ssidList;
    List<ScanResult> wifiList;

    private BroadcastReceiver mStausReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            mainStatus = intent.getBooleanExtra("mainstatus", false);
        }

    };
    private BroadcastReceiver mSlienceReciver = new BroadcastReceiver() {
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

        // Create an instance of GoogleAPIClient

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();

        handler.post(runnableCode);
        handler.post(sendCode);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    public Location getLastKnowLocation() {

        return mLastLocation;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {

            mainWifi.disconnect();
            wifiList = mainWifi.getScanResults();
            ssidList = new ArrayList<>();
            for (int i = 0; i < wifiList.size(); i++) {
                ssidList.add(wifiList.get(i).SSID);
            }
            Log.v("asd", ssidList.toString());
            SsidValidation(ssidList);
            handler.postDelayed(runnableCode, 2500);

            Location tempLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(tempLocation != null){
                mLastLocation = tempLocation;
            }
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
                        ssidName = Integer.toString((formattedDate+keyComand[i]+keyAge[j]+keyBlood[k]).hashCode());
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
                j.putExtra("level", wifiList.get(ssidList.indexOf(ssidName)).level);
                sendBroadcast(j);
                ConnectWifi(ssidName);
            }
            slience = false;
            commands = "NULL";
            target = "NULL" ;
            handler.postDelayed(sendCode, 5000);
        }
    };

    private void ConnectWifi(String name){

        Log.v("asd", "attemp to connect " + name);
        mainWifi.setWifiEnabled(true);


        WifiConfiguration wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = String.format("\"%s\"", name);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        //wifiConfig.preSharedKey = String.format("\"%s\"", "Wifi password");
        int netId = mainWifi.addNetwork(wifiConfig);
        mainWifi.disconnect();
        mainWifi.enableNetwork(netId, true);
        mainWifi.reconnect();
    }

    private void DisconnectWifi(){
        mainWifi.disconnect();
    }
}
