package com.example.android.procon_kosen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class WiFiScanner extends Service {
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
        Toast.makeText(getApplicationContext(), "Service Created",1).show();
        super.onCreate();
    }
}
