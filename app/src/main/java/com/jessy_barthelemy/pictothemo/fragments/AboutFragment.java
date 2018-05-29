package com.jessy_barthelemy.pictothemo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jessy_barthelemy.pictothemo.R;

public class AboutFragment extends BaseFragment{

    private View separator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        this.separator = view.findViewById(R.id.about_separator);
        this.separator.animate().scaleX(1f).alpha(1).setDuration(400).start();
        return view;
    }
}