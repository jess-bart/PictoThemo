package com.jessy_barthelemy.pictothemo.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.jessy_barthelemy.pictothemo.asyncInteractions.AddCommentTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.DeleteUserTask;
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
        Preference deleteAccount = this.findPreference(this.getString(R.string.settings_delete_account_key));

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

        deleteAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater inflater = SettingsFragment.this.getActivity().getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.delete_account, null);
                final EditText accountPassword = (EditText) alertLayout.findViewById(R.id.account_password);

                new AlertDialog.Builder(SettingsFragment.this.getActivity())
                        .setTitle(R.string.account)
                        .setView(alertLayout)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DeleteUserTask deleteTask = new DeleteUserTask(accountPassword.getText().toString(), SettingsFragment.this.getActivity());
                                deleteTask.execute();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {}
                        })
                        .show();

                return true;
            }
        });
    }
}
