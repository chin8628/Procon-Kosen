package com.example.android.procon_kosen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class intro_slide extends FragmentActivity {

    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.intro);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new IntroFragment1();
                case 1:
                    return new IntroFragment2();
                case 2:
                    return new IntroFragment3();
                case 3:
                    return new IntroFragment4();
                case 4:
                    return new IntroFragment5();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
