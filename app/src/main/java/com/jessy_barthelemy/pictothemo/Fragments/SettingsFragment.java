package com.jessy_barthelemy.pictothemo.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.jessy_barthelemy.pictothemo.R;

public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
