package com.example.pethoalpar.zxingexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.zxing.integration.android.IntentIntegrator;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabClickListener;

public class TakeAttendance extends AppCompatActivity {

    BottomBar mBottomBar;
    FragmentAdapter adapterViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("FirstTimes", true);
        editor.apply();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new FragmentAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(adapterViewPager);
        viewPager.setCurrentItem(1, true);
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        tabsStrip.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Toast.makeText(TakeAttendance.this,"Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageSelected(int position) {

                switch (position){
                    case 0:
                        // Case 0 - QR Scanner
                        Toast.makeText(TakeAttendance.this,"Selected page position: 1", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        // Case 1
                        Toast.makeText(TakeAttendance.this,"Selected page position: 2", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(TakeAttendance.this,"Selected page position: 3", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    //        mBottomBar = BottomBar.attach(this, savedInstanceState);
//        mBottomBar.setItemsFromMenu(R.menu.menu_bottombar, new OnMenuTabClickListener() {
//            @Override
//            public void onMenuTabSelected(@IdRes int i) {
//                if (i == R.id.bottombarscanner) {
//                    Scan f = new Scan();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, f).commit();
//                } else if (i == R.id.bottombarpin) {
//                    EnterStudentID f = new EnterStudentID();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, f).commit();
//                } else if (i == R.id.bottombarviewattendance) {
//                    DisplayResult f = new DisplayResult();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, f).commit();
//                }
//            }
//
//            @Override
//            public void onMenuTabReSelected(@IdRes int i) {
//
//            }
//        });
//        mBottomBar.mapColorForTab(0,"#E1BEE7");
//        mBottomBar.mapColorForTab(1,"#B3E5FC");
//        mBottomBar.mapColorForTab(2,"#FFECB3");
//
////        BottomBarBadge unread;
////        unread = mBottomBar.makeBadgeForTabAt(1,"#BDBDBD",13);
////        unread.show();
//    }

    public boolean onCreateOptionalMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bottombar,menu);
        return true;
    }
}
