package com.example.android.procon_kosen;


import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

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
    private Location mLastLocation;
    private RequestQueue queue;
    private GoogleApiClient mGoogleApiClient;


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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        ph = new ProfileHelper(this);

        //Initalize objects
        mainWifi = (WifiManager) this.getSystemService(this.WIFI_SERVICE);
        final WifiReceiver mWifi = new WifiReceiver();
        registerReceiver(mWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(mStausReceiver, new IntentFilter("mainBroadcaster"));
        registerReceiver(mSlienceReciver, new IntentFilter("slience b"));
        handler = new Handler();

        WifiManager.WifiLock wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        wifiLock.acquire();

        queue = Volley.newRequestQueue(this);

        handler.post(runnableCode);
        handler.post(sendCode);
        handler.postDelayed(runnableScan, 60000 * 60);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.v("location", mLastLocation.toString());

            Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

            try {
                List<Address> address = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);

                Log.v("location", address.get(0).getLocality() + ".");
                Log.v("location", address.get(0).getAdminArea() + ".");

                BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("data.json")));
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(br).getAsJsonObject();

                JsonArray feature = obj.get("features").getAsJsonArray();


                String code = "";

                String province = address.get(0).getAdminArea();

                if(province.equals("Bangkok")){
                    province = "Bangkok Metropolis";
                }

                for(JsonElement item: feature){

                    JsonObject item2 = item.getAsJsonObject();

                    try{

                        String ccurrent = item2.get("properties").getAsJsonObject().get("name").getAsString();

                        if(province.equals(ccurrent)){

                            code = item2.getAsJsonObject("properties").get("hc-key").getAsString();
                            Log.v("request", code);
                            break;
                        }
                    } catch (Exception e){

                    }


                }

                String url =  "http://iamhere.cloudian.in.th/backend/api/insert_location?p=" + address.get(0).getAdminArea() +
                        "&c=Thailand&pk=" + code + "&d=" + address.get(0).getLocality();


                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v("Request", "Success");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("Request", "Error");
                    }
                });
                queue.add(stringRequest);

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Log.v("location", "GPS is off");
        }

        mGoogleApiClient.disconnect();

        handler.postDelayed(runnableScan, 60000 * 60);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            wifiList = mainWifi.getScanResults();
            ssidList = new ArrayList<>();
            for (int i = 0; i < wifiList.size(); i++) {
                ssidList.add(wifiList.get(i).SSID);
            }
            SsidValidation(ssidList);

            handler.postDelayed(runnableCode, 2500);
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

    private Runnable runnableScan = new Runnable() {
        @Override
        public void run() {

            Log.v("location", "Attemp to connect");
            mGoogleApiClient.connect();
        }
    };

    private Runnable sendCode = new Runnable() {
        @Override
        public void run() {

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
