package com.example.android.procon_kosen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by frostoxin on 9/5/2016.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent App = new Intent(context, MainActivity.class);
        App.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(App);
        //Intent service = new Intent(context, WiFiScanner.class);
        //context.startService(service);
    }
}