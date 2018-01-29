package com.jessy_barthelemy.pictothemo.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.Activities.LoginActivity;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.R;

public class FirstLaunchFragment extends Fragment {

    private View separator;
    private boolean isFirstFragment;
    private boolean isLastFragment;
    private boolean separatorAnimated;

    private int imageResource;
    private int title;
    private int description;

    public static FirstLaunchFragment newInstance(int imageResource, int title, int description) {
        FirstLaunchFragment fragment = new FirstLaunchFragment();
        fragment.imageResource = imageResource;
        fragment.title = title;
        fragment.description = description;
        return fragment;
    }

    public void setIsFirstFragment(){
        this.isFirstFragment = true;
    }

    public void setIsLastFragment(){
        this.isLastFragment = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_launch, container, false);
        ImageView image = (ImageView)rootView.findViewById(R.id.first_lauch_icon);
        TextView titleTextView = (TextView) rootView.findViewById(R.id.first_launch_title);
        TextView descriptionTextView = (TextView) rootView.findViewById(R.id.first_launch_description);
        Button validation = (Button) rootView.findViewById(R.id.first_launch_ok);

        this.separator = rootView.findViewById(R.id.first_launch_separator);

        image.setImageResource(this.imageResource);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            titleTextView.setText(Html.fromHtml(this.getResources().getText(this.title).toString(), Html.FROM_HTML_MODE_COMPACT));
            descriptionTextView.setText(Html.fromHtml(this.getResources().getText(this.description).toString(), Html.FROM_HTML_MODE_COMPACT));

        } else {
            titleTextView.setText(Html.fromHtml(this.getResources().getText(this.title).toString()));
            descriptionTextView.setText(Html.fromHtml(this.getResources().getText(this.description).toString()));
        }

        if(this.isFirstFragment)
            this.animateSeparator();

        if(separatorAnimated)
            this.separator.setScaleX(1);

        if(this.isLastFragment)
            validation.setVisibility(View.VISIBLE);

        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ApplicationHelper.setTutorialPref(FirstLaunchFragment.this.getActivity().getApplicationContext(), false);

            Intent intent = new Intent(FirstLaunchFragment.this.getActivity(), LoginActivity.class);
            startActivity(intent);
            }
        });

        return rootView;
    }

    private void animateSeparator(){
        if(!this.separatorAnimated){
            this.separator.setAlpha(0.0f);
            this.separator.animate().scaleX(1f).alpha(1).setDuration(400).start();
            this.separatorAnimated = true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.separator != null && isVisibleToUser) {
            this.animateSeparator();
        }
    }
}