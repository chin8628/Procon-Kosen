package com.example.android.procon_kosen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileHelper extends AppCompatActivity {

    private String mName;
    private String mBlood;
    private String mBirthday;
    private String mSiblingPhone1;
    private String mSiblingPhone2;
    private SharedPreferences mSpf;

    public ProfileHelper(Context v) {
        mSpf = v.getSharedPreferences("contentProfile", Context.MODE_PRIVATE);
        mName = mSpf.getString("name", "");
        mBlood = mSpf.getString("blood", "");
        mBirthday = mSpf.getString("birthday", "");
        mSiblingPhone1 = mSpf.getString("sibling1", "");
        mSiblingPhone2 = mSpf.getString("sibling2", "");
    }

    public int getAge() throws ParseException {
        // Get string of birthday for parse to Calendar
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy", Locale.US);
        Date date = sdf.parse(this.getBirthday());
        Calendar calBirthday = Calendar.getInstance(Locale.US);
        calBirthday.setTime(date);

        // Get Calendar of current day
        Calendar calToday = Calendar.getInstance(Locale.US);

        // Calculate age then check if birthday of this year isn't passed, Back for 1 year
        int year = calToday.get(Calendar.YEAR) - calBirthday.get(Calendar.YEAR);
        if (calToday.get(Calendar.DAY_OF_YEAR) < calBirthday.get(Calendar.DAY_OF_YEAR)) {
            year--;
        }

        return year;
    }

    public String getName() {
        mName = mSpf.getString("name", "");
        return this.mName;
    }

    public String getBlood() {
        mBlood = mSpf.getString("blood", "");
        return this.mBlood;
    }

    public String getBirthday() {
        mBirthday = mSpf.getString("birthday", "");
        return this.mBirthday;
    }

    public String[] getSiblingPhone1() {
        //Return array string of number phone

        mSiblingPhone1 = mSpf.getString("sibling1", "");
        mSiblingPhone2 = mSpf.getString("sibling2", "");
        String[] phoneNumber = {this.mSiblingPhone1, this.mSiblingPhone2};
        return phoneNumber;
    }
}
