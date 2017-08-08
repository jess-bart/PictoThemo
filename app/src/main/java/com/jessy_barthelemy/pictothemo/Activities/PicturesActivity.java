package com.jessy_barthelemy.pictothemo.Activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.Adapters.CommentArrayAdapter;
import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetImageTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.R;

import java.util.ArrayList;

public class PicturesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout contentFrameLayout = (RelativeLayout) findViewById(R.id.content_main);
        getLayoutInflater().inflate(R.layout.activity_pictures, contentFrameLayout);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.pictures_container);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    public static class PlaceholderFragment extends Fragment{
        private Picture picture;
        private ImageView pictureView;
        private TextView picturePseudo;
        private TextView pictureTheme;
        private TextView picturePositiveVote;
        private TextView pictureNegativeVote;
        private TextView pictureCommentCount;
        private View pictureLoadbar;
        private ListView comments;

        public PlaceholderFragment() {}

        public void setPicture(Picture picture){
            this.picture = picture;
        }

        public static PlaceholderFragment newInstance(Picture picture) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setPicture(picture);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pictures, container, false);
            this.pictureView = (ImageView)rootView.findViewById(R.id.picture);
            this.picturePseudo = (TextView)rootView.findViewById(R.id.picture_pseudo);
            this.pictureTheme = (TextView)rootView.findViewById(R.id.picture_theme);
            this.picturePositiveVote = (TextView)rootView.findViewById(R.id.picture_positive_vote);
            this.pictureNegativeVote = (TextView)rootView.findViewById(R.id.picture_negative_vote);
            this.pictureLoadbar = rootView.findViewById(R.id.picture_loadbar);
            this.pictureCommentCount = (TextView)rootView.findViewById(R.id.picture_comment_count);
            this.comments = (ListView) rootView.findViewById(R.id.picture_comments);

            this.comments.setAdapter(new CommentArrayAdapter(this.getContext(), R.layout.comment_row, this.picture.getComments()));

            Point wSize = new Point();
            this.getActivity().getWindowManager().getDefaultDisplay().getSize(wSize);
            this.pictureView.setMaxHeight(wSize.x);

            GetImageTask imageTask = new GetImageTask(this.getContext(), this.pictureView, this.pictureLoadbar, this.picture.getId());
            imageTask.execute();

            this.picturePseudo.setText(ApplicationHelper.handleUnknowPseudo(this.getContext(), this.picture.getUser().getPseudo()));
            this.pictureTheme.setText(String.format(getResources().getString(R.string.theme_name), this.picture.getTheme()));
            this.picturePositiveVote.setText(String.valueOf(this.picture.getPositiveVote()));
            this.pictureNegativeVote.setText(String.valueOf(this.picture.getNegativeVote()));
            this.pictureCommentCount.setText(String.valueOf(this.picture.getComments().size()));

            return rootView;
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Picture> pictures;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            Bundle args = getIntent().getBundleExtra(ApplicationHelper.EXTRA_PICTURES_LIST);
            this.pictures = (ArrayList<Picture>) args.getSerializable(ApplicationHelper.EXTRA_PICTURES_LIST);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(this.pictures.get(position));
        }

        @Override
        public int getCount() {
            return this.pictures.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.pictures.get(0).getTheme();
        }
    }
}
