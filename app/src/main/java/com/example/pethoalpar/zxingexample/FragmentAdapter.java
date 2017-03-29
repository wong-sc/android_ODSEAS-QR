package com.example.pethoalpar.zxingexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class FragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Insert Pin", "QR Scanner", "Attendance" };
    private Context context;


    public FragmentAdapter (FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return EnterStudentID.newInstance(0);
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return Scan.newInstance(1);
            case 2: // Fragment # 1 - This will show SecondFragment
                return DisplayResult.newInstance(2);
            default:
                return Scan.newInstance(1);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}