package com.example.android.procon_kosen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //Start the wifi service after boot
        Intent App = new Intent(context, WiFiScanner.class);
        App.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(App);

    }
}