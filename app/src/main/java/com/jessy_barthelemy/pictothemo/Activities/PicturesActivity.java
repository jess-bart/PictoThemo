package com.jessy_barthelemy.pictothemo.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.Adapters.CommentArrayAdapter;
import com.jessy_barthelemy.pictothemo.ApiObjects.Comment;
import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.AddCommentTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.DeleteCommentTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetImageTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.VotePictureTask;
import com.jessy_barthelemy.pictothemo.Dialogs.VoteDialog;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.Interfaces.IVoteResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.util.ArrayList;

public class PicturesActivity extends BaseActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout contentFrameLayout = (RelativeLayout) findViewById(R.id.content_main);
        getLayoutInflater().inflate(R.layout.activity_pictures, contentFrameLayout);

        this.sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        this.viewPager = (ViewPager) findViewById(R.id.pictures_container);
        this.viewPager.setAdapter(this.sectionsPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        Picture picture = ((PictureFragment)this.sectionsPagerAdapter.getItem(this.viewPager.getCurrentItem())).getPicture();

        Intent intent = new Intent();
        Bundle args = new Bundle();
        args.putSerializable(ApplicationHelper.EXTRA_PICTURES_LIST, picture);
        intent.putExtra(ApplicationHelper.EXTRA_PICTURES_LIST, args);


        this.setResult(ApplicationHelper.UPDATE_PICTURE, intent);
        super.onBackPressed();
    }

    public static class PictureFragment extends Fragment implements IAsyncApiObjectResponse, IVoteResponse, IAsyncResponse{

        private final int DELETE_COMMENT_MENU = 1;
        private Picture picture;
        private ImageView pictureView;
        private TextView picturePseudo;
        private TextView pictureTheme;
        private TextView picturePositiveVote;
        private TextView pictureNegativeVote;
        private TextView pictureCommentCount;
        private View pictureLoadbar;
        private ListView comments;
        private Button addComment;
        private View pictureVote;
        private User me;
        private CommentArrayAdapter commentAdapter;

        public PictureFragment() {}

        public void setPicture(Picture picture){
            this.picture = picture;
        }

        public static PictureFragment newInstance(Picture picture) {
            PictureFragment fragment = new PictureFragment();
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
            this.addComment = (Button) rootView.findViewById(R.id.comment_add);

            this.pictureVote = rootView.findViewById(R.id.vote);

            this.me = ApplicationHelper.getCurrentUser(this.getContext());
            this.commentAdapter = new CommentArrayAdapter(this.getContext(), R.layout.comment_row, this.picture.getComments());
            this.comments.setAdapter(this.commentAdapter);

            this.pictureVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                VoteDialog dialog = new VoteDialog(PictureFragment.this.getActivity(), PictureFragment.this);
                dialog.show();
                }
            });

            Point wSize = new Point();
            this.getActivity().getWindowManager().getDefaultDisplay().getSize(wSize);
            this.pictureView.setMaxHeight(wSize.x);

            GetImageTask imageTask = new GetImageTask(this.getContext(), this.pictureView, this.pictureLoadbar, this.picture.getId());
            imageTask.execute();

            this.updateImage();

            this.addComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                LayoutInflater inflater = PictureFragment.this.getActivity().getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.add_comment, null);
                final EditText commentText = (EditText) alertLayout.findViewById(R.id.add_comment_text);

                //commentText.setHint(R.string.comment_placeholder);

                new AlertDialog.Builder(PictureFragment.this.getContext())
                        .setTitle(R.string.comment)
                        .setView(R.layout.add_comment)
                        .setPositiveButton(R.string.comment_add, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                AddCommentTask addComment = new AddCommentTask(PictureFragment.this.picture.getId(), commentText.getText().toString(), PictureFragment.this.getContext(), PictureFragment.this);
                                addComment.execute();
                            }
                        })
                        .setNegativeButton(R.string.comment_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {}
                        })
                        .show();
                }
            });

            this.registerForContextMenu(this.comments);
            this.registerForContextMenu(this.pictureView);
            return rootView;
        }

        private void updateImage(){
            this.picturePseudo.setText(ApplicationHelper.handleUnknowPseudo(this.getContext(), this.picture.getUser().getPseudo()));
            this.pictureTheme.setText(String.format(getResources().getString(R.string.theme_name), this.picture.getTheme()));
            this.picturePositiveVote.setText(String.valueOf(this.picture.getPositiveVote()));
            this.pictureNegativeVote.setText(String.valueOf(this.picture.getNegativeVote()));
            this.updateCommentCount();
        }

        @Override
        public void asyncTaskSuccess(boolean positive) {
            VotePictureTask voteTask = new VotePictureTask(PictureFragment.this.picture, positive, this.getContext(), this);
            voteTask.execute();
        }

        @Override
        public void asyncTaskSuccess(Object response) {
            if(response instanceof Picture){
                this.picture = (Picture)response;
                PictureFragment.this.updateImage();
            }else if(response instanceof Comment){
                Comment comment = (Comment) response;
                this.picture.getComments().add(0, comment);
                this.commentAdapter.notifyDataSetChanged();

            }
        }

        //used for deleting comment
        @Override
        public void asyncTaskSuccess() {
            for(int i = (this.picture.getComments().size()-1); i >= 0; --i){
                if(this.picture.getComments().get(i).getUser().getId() == this.me.getId()){
                    this.picture.getComments().remove(i);
                }
            }

            this.commentAdapter.notifyDataSetChanged();
            this.updateCommentCount();
        }

        @Override
        public void asyncTaskFail(String errorMessage) {
            Toast.makeText(this.getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }

        public Picture getPicture(){
            return this.picture;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);

            switch(v.getId()){
                case R.id.picture:
                break;
                case R.id.picture_comments:
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                    Comment comment = this.commentAdapter.getItem(info.position);
                    if(comment.getUser().getId() != this.me.getId())
                        return;
                    menu.setHeaderTitle(R.string.comment);
                    menu.add(Menu.NONE, DELETE_COMMENT_MENU, Menu.NONE, R.string.delete);
                    break;
            }
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case DELETE_COMMENT_MENU:
                    DeleteCommentTask deleteTask = new DeleteCommentTask(this.picture.getId(), this.getContext(), this);
                    deleteTask.execute();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        private void updateCommentCount(){
            this.pictureCommentCount.setText(String.valueOf(this.picture.getComments().size()));
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
            return PictureFragment.newInstance(this.pictures.get(position));
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
