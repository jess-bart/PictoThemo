package com.jessy_barthelemy.pictothemo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.jessy_barthelemy.pictothemo.adapters.FirstLaunchPagerAdapter;
import com.jessy_barthelemy.pictothemo.fragments.FirstLaunchFragment;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.R;

public class FirstLaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        if(!settings.getBoolean(ApplicationHelper.TUTORIAL_PREF, true)){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        setContentView(R.layout.activity_first_time);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorAccentDark));

        final ImageView firstCircle = (ImageView) this.findViewById(R.id.first_circle);
        final ImageView secondCircle = (ImageView) this.findViewById(R.id.second_circle);
        final ImageView thirdCircle = (ImageView) this.findViewById(R.id.third_circle);
        final View tutoInfo = this.findViewById(R.id.first_launch_tuto);

        tutoInfo.setAlpha(0.0f);

        FirstLaunchPagerAdapter sectionsPagerAdapter = new FirstLaunchPagerAdapter(this.getFragmentManager());

        FirstLaunchFragment firstFragment = FirstLaunchFragment.newInstance(R.drawable.first_launch_picture, R.string.first_launch_title1, R.string.first_launch_desc1);
        firstFragment.setIsFirstFragment();
        sectionsPagerAdapter.addFragment(firstFragment);

        sectionsPagerAdapter.addFragment(FirstLaunchFragment.newInstance(R.drawable.first_launch_vote, R.string.first_launch_title2, R.string.first_launch_desc2));

        FirstLaunchFragment lastFragment = FirstLaunchFragment.newInstance(R.drawable.first_launch_trophy, R.string.first_launch_title3, R.string.first_launch_desc3);
        lastFragment.setIsLastFragment();
        sectionsPagerAdapter.addFragment(lastFragment);

        ViewPager viewPager = (ViewPager) this.findViewById(R.id.fragment_first_launch);
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 1:
                        secondCircle.setAlpha(1f);
                        firstCircle.setAlpha(0.3f);
                        thirdCircle.setAlpha(0.3f);
                        tutoInfo.animate().alpha(0).setDuration(400).start();
                        break;
                    case 2:
                        thirdCircle.setAlpha(1f);
                        firstCircle.setAlpha(0.3f);
                        secondCircle.setAlpha(0.3f);
                        tutoInfo.animate().alpha(1).setDuration(400).start();
                        break;
                    default:
                        firstCircle.setAlpha(1f);
                        secondCircle.setAlpha(0.3f);
                        thirdCircle.setAlpha(0.3f);
                        tutoInfo.animate().alpha(0).setDuration(400).start();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
}
