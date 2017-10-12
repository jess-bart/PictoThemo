package com.jessy_barthelemy.pictothemo.Fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.text.DateFormat;

public class PictureFragment extends Fragment implements IAsyncApiObjectResponse, IVoteResponse, IAsyncResponse {

    private final int DELETE_COMMENT_MENU = 1;
    private Picture picture;
    private ImageView pictureIsPotd;
    private TextView picturePseudo;
    private TextView pictureTheme;
    private TextView pictureDate;
    private TextView picturePositiveVote;
    private TextView pictureNegativeVote;
    private TextView pictureCommentCount;
    private TextView pictureCommentEmpty;
    private ListView comments;
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

        ImageView pictureView = (ImageView) rootView.findViewById(R.id.picture);
        this.picturePseudo = (TextView)rootView.findViewById(R.id.picture_pseudo);
        this.pictureTheme = (TextView)rootView.findViewById(R.id.picture_theme);
        this.pictureDate = (TextView)rootView.findViewById(R.id.picture_date);
        this.picturePositiveVote = (TextView)rootView.findViewById(R.id.picture_positive_vote);
        this.pictureNegativeVote = (TextView)rootView.findViewById(R.id.picture_negative_vote);
        View pictureLoadbar = rootView.findViewById(R.id.loadbar);
        this.pictureCommentCount = (TextView)rootView.findViewById(R.id.picture_comment_count);
        this.pictureCommentEmpty = (TextView)rootView.findViewById(R.id.picture_comment_empty);
        this.comments = (ListView) rootView.findViewById(R.id.picture_comments);
        this.pictureIsPotd = (ImageView)rootView.findViewById(R.id.is_potd);

        View pictureVote = rootView.findViewById(R.id.vote);

        if(this.picture.isPotd())
            this.pictureIsPotd.setVisibility(View.VISIBLE);

        this.me = ApplicationHelper.getCurrentUser(this.getActivity());
        this.commentAdapter = new CommentArrayAdapter(this.getActivity(), R.layout.comment_row, this.picture.getComments());
        this.comments.setAdapter(this.commentAdapter);

        this.comments.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        if(this.picture.getComments().size() == 0)
            this.pictureCommentEmpty.setVisibility(View.VISIBLE);

        pictureVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteDialog dialog = new VoteDialog(PictureFragment.this.getActivity(), PictureFragment.this);
                dialog.show();
            }
        });

        Point wSize = new Point();
        this.getActivity().getWindowManager().getDefaultDisplay().getSize(wSize);
        pictureView.setMaxHeight(wSize.x);

        GetImageTask imageTask = new GetImageTask(this.getActivity(), pictureView, pictureLoadbar, this.picture.getId(), false);
        imageTask.execute();

        this.updateImage();

        this.registerForContextMenu(this.comments);
        this.registerForContextMenu(pictureView);
        return rootView;
    }

    private void updateImage(){
        this.picturePseudo.setText(ApplicationHelper.handleUnknowPseudo(this.getActivity(), this.picture.getUser().getPseudo()));
        this.pictureTheme.setText(String.format(getResources().getString(R.string.theme_name), this.picture.getTheme().getName()));

        DateFormat formater = android.text.format.DateFormat.getDateFormat(this.getActivity());
        this.pictureDate.setText(formater.format(this.picture.getTheme().getCandidateDate().getTime()));
        this.pictureIsPotd.setVisibility(this.picture.isPotd()?View.VISIBLE:View.GONE);

        this.picturePositiveVote.setText(String.valueOf(this.picture.getPositiveVote()));
        this.pictureNegativeVote.setText(String.valueOf(this.picture.getNegativeVote()));
        this.updateCommentCount();
    }

    @Override
    public void asyncTaskSuccess(boolean positive) {
        VotePictureTask voteTask = new VotePictureTask(PictureFragment.this.picture, positive, this.getActivity(), this);
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
            this.updateCommentCount();
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
        Toast.makeText(this.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public Picture getPicture(){
        return this.picture;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch(v.getId()){
            case R.id.picture_comments:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                Comment comment = this.commentAdapter.getItem(info.position);
                if(comment != null && comment.getUser().getId() != this.me.getId())
                    return;
                menu.setHeaderTitle(R.string.comment);
                menu.add(Menu.NONE, DELETE_COMMENT_MENU, Menu.NONE, R.string.delete);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            switch (item.getItemId()) {
                case DELETE_COMMENT_MENU:
                    DeleteCommentTask deleteTask = new DeleteCommentTask(this.picture.getId(), this.getActivity(), this);
                    deleteTask.execute();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        } else
            return false;
    }

    private void updateCommentCount(){
        this.pictureCommentCount.setText(String.valueOf(this.picture.getComments().size()));
        this.pictureCommentEmpty.setVisibility((this.picture.getComments().size() > 0)?View.GONE:View.VISIBLE);
        this.setListViewHeightBasedOnChildren(this.comments);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < this.commentAdapter.getCount(); i++) {
            view = this.commentAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewPager.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (this.commentAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void showCommentDialog(){
        LayoutInflater inflater = PictureFragment.this.getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_comment, null);
        final EditText commentText = (EditText) alertLayout.findViewById(R.id.add_comment_text);
        new AlertDialog.Builder(PictureFragment.this.getActivity())
                .setTitle(R.string.comment)
                .setView(alertLayout)
                .setPositiveButton(R.string.comment_add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AddCommentTask addComment = new AddCommentTask(PictureFragment.this.picture.getId(), commentText.getText().toString(), PictureFragment.this.getActivity(), PictureFragment.this);
                        addComment.execute();
                    }
                })
                .setNegativeButton(R.string.comment_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
    }
}