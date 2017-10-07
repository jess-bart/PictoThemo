package com.jessy_barthelemy.pictothemo.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.Enum.PictureOrder;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IBackPressedEventHandler;
import com.jessy_barthelemy.pictothemo.R;

import java.util.ArrayList;

public class PicturesFragment extends BaseFragment implements IBackPressedEventHandler{

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private ArrayList<Picture> pictures;
    private View fabOptions;
    private FloatingActionButton fab;

    public void setPictures(ArrayList<Picture> pictures){
        this.pictures = pictures;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pictures_vp, container, false);

        this.sectionsPagerAdapter = new SectionsPagerAdapter(this.getChildFragmentManager(), this.pictures);
        this.viewPager = (ViewPager) view.findViewById(R.id.pictures_container);
        this.viewPager.setAdapter(this.sectionsPagerAdapter);
        this.fabOptions = view.findViewById(R.id.fab_options);

        final View clickDetector = view.findViewById(R.id.clickDetector);
        FloatingActionButton orderByVoteAsc = (FloatingActionButton) view.findViewById(R.id.order_by_vote_plus);
        FloatingActionButton orderByVoteDesc = (FloatingActionButton) view.findViewById(R.id.order_by_vote_minus);
        FloatingActionButton orderbyDate = (FloatingActionButton) view.findViewById(R.id.order_by_date);
        FloatingActionButton orderByComment = (FloatingActionButton) view.findViewById(R.id.order_by_comment);
        this.fab = (FloatingActionButton) view.findViewById(R.id.fab);
        FloatingActionButton addComment = (FloatingActionButton) view.findViewById(R.id.add_comment);

        final Animation slideDown = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_down);
        final Animation slideUp = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_up);
        final Animation fadeIn = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fade_in);
        final Animation fadeOut = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fade_out);

        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                PicturesFragment.this.fabOptions.setVisibility(View.GONE);
                PicturesFragment.this.fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        clickDetector.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(PicturesFragment.this.fabOptions.getVisibility() == View.VISIBLE)
                    PicturesFragment.this.closeClickDetector(clickDetector, slideDown, fadeOut);
                return false;
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturesFragment.this.closeClickDetector(clickDetector, slideDown, fadeOut);
                ((PictureFragment)sectionsPagerAdapter.getCurrentFragment()).showCommentDialog();
            }
        });

        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturesFragment.this.fabOptions.setVisibility(View.VISIBLE);
                PicturesFragment.this.fabOptions.startAnimation(slideUp);
                PicturesFragment.this.fab.setVisibility(View.GONE);

                clickDetector.setVisibility(View.VISIBLE);
                clickDetector.startAnimation(fadeIn);
            }
        });

        orderbyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturesFragment.this.closeClickDetector(clickDetector, slideDown, fadeOut);
                PicturesFragment.this.sortPictures(PictureOrder.DATE);
            }
        });

        orderByComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturesFragment.this.closeClickDetector(clickDetector, slideDown, fadeOut);
                PicturesFragment.this.sortPictures(PictureOrder.COMMENT);
            }
        });

        orderByVoteDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturesFragment.this.closeClickDetector(clickDetector, slideDown, fadeOut);
                PicturesFragment.this.sortPictures(PictureOrder.VOTE_NEG);
            }
        });

        orderByVoteAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturesFragment.this.closeClickDetector(clickDetector, slideDown, fadeOut);
                PicturesFragment.this.sortPictures(PictureOrder.VOTE_POS);
            }
        });

        PicturesFragment.this.sortPictures(PictureOrder.DATE);
        return view;
    }

    private void closeClickDetector(final View clickDetector, Animation slideDown, Animation fadeOut){
        clickDetector.setVisibility(View.INVISIBLE);
        clickDetector.startAnimation(fadeOut);

        this.fabOptions.startAnimation(slideDown);
    }

    public void sortPictures(PictureOrder order){
        PicturesFragment.this.sectionsPagerAdapter.sortPictures(order);
        PicturesFragment.this.sectionsPagerAdapter.notifyDataSetChanged();
        PicturesFragment.this.viewPager.setAdapter(PicturesFragment.this.sectionsPagerAdapter);
    }

    @Override
    public void handleBackPress() {
        Picture picture = ((PictureFragment)this.sectionsPagerAdapter.getItem(this.viewPager.getCurrentItem())).getPicture();

        Intent intent = new Intent();
        Bundle args = new Bundle();
        args.putSerializable(ApplicationHelper.EXTRA_PICTURES_LIST, picture);
        intent.putExtra(ApplicationHelper.EXTRA_PICTURES_LIST, args);
        this.getActivity().setResult(ApplicationHelper.UPDATE_PICTURE, intent);
    }
}
