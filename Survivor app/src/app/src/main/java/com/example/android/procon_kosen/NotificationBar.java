package com.example.android.procon_kosen;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class NotificationBar {

    private ViewGroup mViewNotificationBar;

    public NotificationBar(Activity v) {
        mViewNotificationBar = (ViewGroup) v.findViewById(R.id.notification_bar);
    }
    
    public void show() {
        mViewNotificationBar.setVisibility(View.VISIBLE);
    }
    
    public void hide() {
        mViewNotificationBar.setVisibility(View.INVISIBLE);
    }

}
