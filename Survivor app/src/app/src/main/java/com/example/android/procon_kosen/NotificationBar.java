package com.example.android.procon_kosen;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationBar {

    private ViewGroup mViewNotificationBar;
    private TextView mTextViewNotificationBar;
    private String mNotification_text;

    public NotificationBar(Activity v) {
        // Default notification's message is Notify_Detected (Find in string resource)

        mViewNotificationBar = (ViewGroup) v.findViewById(R.id.notification_bar);
        mTextViewNotificationBar = (TextView) mViewNotificationBar.findViewById(R.id.notification_text);
        mNotification_text = v.getResources().getString(R.string.notify_detected);
    }
    
    public void show() {
        mViewNotificationBar.setVisibility(View.VISIBLE);
    }
    
    public void hide() {
        mViewNotificationBar.setVisibility(View.GONE);
    }

    public void setText(String msg) {
        mNotification_text = msg;
        mTextViewNotificationBar.setText(msg);
    }

}
