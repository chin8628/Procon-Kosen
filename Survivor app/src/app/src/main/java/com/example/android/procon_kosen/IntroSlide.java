package com.example.android.procon_kosen;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroSlide extends FragmentActivity {

    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private Button mNextBtn, mBackBtn;
    private TextView[] dots;
    private LinearLayout dotsLayout;
    private int[] layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.intro);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Initial value from layout navigate
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        layouts = new int[]{
                R.layout.intro_slide_fragment_1,
                R.layout.intro_slide_fragment_2,
                R.layout.intro_slide_fragment_3,
                R.layout.intro_slide_fragment_4,
                R.layout.intro_slide_fragment_5 };
        addBottomDots(0);

        mNextBtn = (Button) findViewById(R.id.btn_next);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mPager.getCurrentItem() + 1;
                if (current < NUM_PAGES) {
                    // move to next screen
                    mPager.setCurrentItem(current);
                } else {
                    Intent i = new Intent(getBaseContext(), EditProfile.class);
                    startActivity(i);
                }
            }
        });

        mBackBtn = (Button) findViewById(R.id.btn_back);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mPager.getCurrentItem() - 1;
                if (current >= -1) {
                    // move to previous screen
                    mPager.setCurrentItem(current);
                }
            }
        });

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                addBottomDots(position);
                Log.v("PageSelected", Integer.toString(position));
                if (mPager.getCurrentItem() == NUM_PAGES - 1) {
                    mNextBtn.setText(R.string.button_edit_profile);
                } else if (position == 0) {
                    mBackBtn.setVisibility(View.GONE);
                }
                else {
                    mNextBtn.setText(R.string.next);
                    mBackBtn.setVisibility(View.VISIBLE);
                    mBackBtn.setText(R.string.back);
                }
            }
        });

    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int colorsActive = ContextCompat.getColor(this, R.color.colorActive);
        int colorsInactive = ContextCompat.getColor(this, R.color.colorInActive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0){
            dots[currentPage].setTextColor(colorsActive);
        }
    }

    @Override
    public void onBackPressed() {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
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
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
