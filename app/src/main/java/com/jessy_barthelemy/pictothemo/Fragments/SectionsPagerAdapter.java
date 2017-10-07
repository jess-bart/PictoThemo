package com.jessy_barthelemy.pictothemo.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.Enum.PictureOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class SectionsPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<PictureFragment> picturesFragment;
    private PictureFragment currentFragment;
    private Comparator<PictureFragment> votePosComparator;
    private Comparator<PictureFragment> voteNegComparator;
    private Comparator<PictureFragment> dateComparator;
    private Comparator<PictureFragment> commentComparator;

    SectionsPagerAdapter(FragmentManager fragmentManager, final ArrayList<Picture> pictures) {
        super(fragmentManager);
        this.picturesFragment = new ArrayList<>();

        for(Picture picture : pictures){
            this.picturesFragment.add(PictureFragment.newInstance(picture));
        }

        this.dateComparator = new Comparator<PictureFragment>() {
            @Override
            public int compare(PictureFragment picture1, PictureFragment picture2) {
                boolean res = picture2.getPicture().getId() > picture1.getPicture().getId();
                return res ? 1 : -1;
            }
        };

        this.votePosComparator = new Comparator<PictureFragment>() {
            @Override
            public int compare(PictureFragment picture1, PictureFragment picture2) {
                boolean res = picture2.getPicture().getPositiveVote() - picture2.getPicture().getNegativeVote() > picture1.getPicture().getPositiveVote() - picture1.getPicture().getNegativeVote();
                return res ? 1 : -1;
            }
        };

        this.voteNegComparator = new Comparator<PictureFragment>() {
            @Override
            public int compare(PictureFragment picture1, PictureFragment picture2) {
                boolean res = picture1.getPicture().getPositiveVote() - picture1.getPicture().getNegativeVote() > picture2.getPicture().getPositiveVote() - picture2.getPicture().getNegativeVote();
                return res ? 1 : -1;
            }
        };

        this.commentComparator = new Comparator<PictureFragment>() {
            @Override
            public int compare(PictureFragment picture1, PictureFragment picture2) {
                boolean res = picture2.getPicture().getComments().size() > picture1.getPicture().getComments().size();
                return res ? 1 : -1;
            }
        };
    }

    Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((PictureFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        return picturesFragment.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        PictureFragment fragment = (PictureFragment)object;
        return this.picturesFragment.indexOf(fragment);
    }

    @Override
    public long getItemId(int position) {
        return this.picturesFragment.get(position).getPicture().getId();
    }

    @Override
    public int getCount() {
        return this.picturesFragment.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.picturesFragment.get(position).getPicture().getTheme().getName();
    }

    void sortPictures(PictureOrder order){
        switch (order){
            case VOTE_POS:
                Collections.sort(this.picturesFragment, this.votePosComparator);
                break;
            case VOTE_NEG:
                Collections.sort(this.picturesFragment, this.voteNegComparator);
                break;
            case DATE:
                Collections.sort(this.picturesFragment, this.dateComparator);
                break;
            case COMMENT:
                Collections.sort(this.picturesFragment, this.commentComparator);
                break;
        }
    }
}