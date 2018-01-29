package com.jessy_barthelemy.pictothemo.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.jessy_barthelemy.pictothemo.Fragments.FirstLaunchFragment;

import java.util.ArrayList;

public class FirstLaunchPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<FirstLaunchFragment> fragments;

    public FirstLaunchPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragments = new ArrayList();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() { return fragments.size(); }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Test";
    }

    public void addFragment(FirstLaunchFragment fragment){
        this.fragments.add(fragment);
    }
}