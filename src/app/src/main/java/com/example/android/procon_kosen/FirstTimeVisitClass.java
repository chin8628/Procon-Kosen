package com.example.android.procon_kosen;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstTimeVisitClass {

    SharedPreferences spf;
    SharedPreferences.Editor editor;
    Context context;

    public FirstTimeVisitClass(Context v) {
        this.context = v;
        spf = this.context.getSharedPreferences("FirstTimeVisit", Context.MODE_PRIVATE);
        editor = spf.edit();
        editor.apply();
    }

    public void setVisited() {
        editor.putBoolean("visited", true);
        editor.apply();
    }

    public boolean getVisited() {
        return spf.getBoolean("visited", false);
    }

}
