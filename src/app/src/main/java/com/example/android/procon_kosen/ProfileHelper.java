package com.example.android.procon_kosen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

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
        mSpf = v.getSharedPreferences("contentProfile", MODE_PRIVATE);
        mName = mSpf.getString("name", null);
        mBlood = mSpf.getString("blood", null);
        mBirthday = mSpf.getString("birthday", null);
        mSiblingPhone1 = mSpf.getString("sibling1", null);
        mSiblingPhone2 = mSpf.getString("sibling2", null);
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
        return this.mName;
    }

    public String getBlood() {
        return this.mBlood;
    }

    public String getBirthday() {
        return this.mBirthday;
    }

    public String[] getSiblingPhone1() {
        //Return array string of number phone

        String[] phoneNumber = {this.mSiblingPhone1, this.mSiblingPhone2};
        return phoneNumber;
    }
}
