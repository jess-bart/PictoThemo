package com.jessy_barthelemy.pictothemo.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.jessy_barthelemy.pictothemo.asyncInteractions.GetImageTask;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.R;

import java.util.Calendar;

public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference setWallpaper = this.findPreference(this.getString(R.string.settings_wallpaper_key));
        setWallpaper.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean hasToChangeBackground = (boolean) newValue;
                hasToChangeBackground = ApplicationHelper.hasToChangeBackgroundToday(SettingsFragment.this.getActivity(), hasToChangeBackground);
                GetImageTask imageTask = new GetImageTask(SettingsFragment.this.getActivity(), null, null, Calendar.getInstance(), hasToChangeBackground);
                imageTask.execute();
                return true;
            }
        });
    }
}
