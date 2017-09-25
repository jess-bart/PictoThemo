package com.jessy_barthelemy.pictothemo.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.Activities.BaseActivity;
import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.ApiObjects.PictureList;
import com.jessy_barthelemy.pictothemo.ApiObjects.Theme;
import com.jessy_barthelemy.pictothemo.ApiObjects.ThemeList;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetImageTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetPicturesInfoTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetThemeTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.UploadPictureTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.VotePictureTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.VoteThemeTask;
import com.jessy_barthelemy.pictothemo.Dialogs.VoteDialog;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IVoteResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends BaseFragment implements IVoteResponse {

    private static final int UPLOAD_PICTURE = 4;

    private int theme1;
    private int theme2;
    private ImageView pictureView;
    private AppCompatButton theme1Button;
    private AppCompatButton theme2Button;
    private TextView picturePseudo;
    private TextView pictureTheme;
    private TextView pictureDate;
    private TextView picturePositiveVote;
    private TextView pictureNegativeVote;
    private View pictureVote;
    private TextView pictureCommentCount;
    private Picture picture;
    private boolean localRequest;
    private ImageView pictureImg;
    private View pictureLoadbar;
    private View uploadProgress;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.pictureView = (ImageView)view.findViewById(R.id.picture);
        this.picturePseudo = (TextView)view.findViewById(R.id.picture_pseudo);
        this.pictureTheme = (TextView)view.findViewById(R.id.picture_theme);
        this.pictureDate = (TextView)view.findViewById(R.id.picture_date);

        this.picturePositiveVote = (TextView)view.findViewById(R.id.picture_positive_vote);
        this.pictureNegativeVote = (TextView)view.findViewById(R.id.picture_negative_vote);
        this.pictureVote = view.findViewById(R.id.vote);
        this.pictureCommentCount = (TextView)view.findViewById(R.id.picture_comment_count);

        this.theme1Button = (AppCompatButton)view.findViewById(R.id.home_theme_1);
        this.theme2Button = (AppCompatButton)view.findViewById(R.id.home_theme_2);

        this.pictureImg = (ImageView)view.findViewById(R.id.is_potd);
        this.pictureImg.setVisibility(View.VISIBLE);

        this.pictureLoadbar = view.findViewById(R.id.loadbar);
        this.uploadProgress = view.findViewById(R.id.upload_progress);

        this.fab = (FloatingActionButton) view.findViewById(R.id.fab);

        GetImageTask imageTask = new GetImageTask(this.getActivity(), this.pictureView, pictureLoadbar, Calendar.getInstance(), false);
        imageTask.execute();

        this.localRequest = true;
        Calendar today = Calendar.getInstance();
        GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(today, today, null, null, null, ApiHelper.FLAG_POTD+"|"+ApiHelper.FLAG_COMMENTS, this);
        getPicturesInfosTask.execute();

        SimpleDateFormat dateFormat;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            dateFormat = new SimpleDateFormat("EEEE", getResources().getConfiguration().getLocales().get(0));
        else
            dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        Calendar themeDay = Calendar.getInstance();
        themeDay.add(Calendar.DAY_OF_YEAR, 2);

        GetThemeTask themeTask = new GetThemeTask(themeDay, this);
        themeTask.execute();
        this.theme1Button = (AppCompatButton)view.findViewById(R.id.home_theme_1);
        this.theme2Button = (AppCompatButton)view.findViewById(R.id.home_theme_2);
        TextView themeTitle = (TextView)view.findViewById(R.id.home_theme_title);
        themeTitle.setText(getResources().getString(R.string.theme_title, dateFormat.format(themeDay.getTime())));

        this.theme1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment.this.setThemeButtonColor(true);
                VoteThemeTask voteTask = new VoteThemeTask(HomeFragment.this.theme1, HomeFragment.this.getActivity(), HomeFragment.this);
                voteTask.execute();
            }
        });

        this.theme2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment.this.setThemeButtonColor(false);
                VoteThemeTask voteTask = new VoteThemeTask(HomeFragment.this.theme2, HomeFragment.this.getActivity(), HomeFragment.this);
                voteTask.execute();
            }
        });

        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, UPLOAD_PICTURE);
            }
        });

        this.registerForContextMenu(HomeFragment.this.pictureView);

        return view;
    }

    private void setThemeButtonColor(boolean theme1){
        ColorStateList disabled = ContextCompat.getColorStateList(HomeFragment.this.getActivity(), R.color.colorDisabled);
        if(theme1){
            ViewCompat.setBackgroundTintList(this.theme1Button, ContextCompat.getColorStateList(HomeFragment.this.getActivity(), R.color.colorAccent));
            ViewCompat.setBackgroundTintList(this.theme2Button, disabled);
        }else{
            ViewCompat.setBackgroundTintList(this.theme1Button, disabled);
            ViewCompat.setBackgroundTintList(this.theme2Button, ContextCompat.getColorStateList(HomeFragment.this.getActivity(), R.color.colorPrimary));
        }
    }

    protected void updatePicture(){
        this.picturePseudo.setText(ApplicationHelper.handleUnknowPseudo(this.getActivity(), this.picture.getUser().getPseudo()));
        this.pictureTheme.setText(String.format(getResources().getString(R.string.theme_name), this.picture.getTheme().getName()));
        DateFormat formater = android.text.format.DateFormat.getDateFormat(this.getActivity());
        this.pictureDate.setText(formater.format(this.picture.getTheme().getCandidateDate().getTime()));

        this.picturePositiveVote.setText(String.valueOf(this.picture.getPositiveVote()));
        this.pictureNegativeVote.setText(String.valueOf(this.picture.getNegativeVote()));
        this.pictureCommentCount.setText(String.valueOf(this.picture.getComments().size()));

        this.pictureVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteDialog dialog = new VoteDialog(HomeFragment.this.getActivity(), HomeFragment.this);
                dialog.show();
            }
        });

        this.pictureCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ((BaseActivity)HomeFragment.this.getActivity()).openPOTD(HomeFragment.this.picture);
            }
        });

        this.pictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ((BaseActivity)HomeFragment.this.getActivity()).openPOTD(HomeFragment.this.picture);
            }
        });
    }

    @Override
    public void asyncTaskSuccess(Object response) {
        if(response instanceof String){
            Toast.makeText(this.getActivity(), response.toString(), Toast.LENGTH_LONG).show();
        }else if(response instanceof PictureList){
            //if the request was not made by HomeFragment lets BaseActivity handle it
            if(this.localRequest) {
                this.localRequest = false;

                PictureList pictureList = (PictureList) response;
                ArrayList<Picture> pictures = pictureList.getPictures();

                if (pictures == null || pictures.size() == 0)
                    return;

                this.picture = pictures.get(0);
                this.updatePicture();

            }else
                ((BaseActivity)getActivity()).asyncTaskSuccess(response);

        }else if(response instanceof Picture){
            this.picture = (Picture)response;
            this.updatePicture();
        }else if(response instanceof ThemeList){
            ThemeList themeList = (ThemeList) response;
            if(themeList.getThemes() != null && themeList.getThemes().size() > 1){
                this.theme1 = themeList.getThemes().get(0).getId();
                this.theme2 = themeList.getThemes().get(1).getId();

                this.theme1Button.setText(themeList.getThemes().get(0).getName());
                this.theme2Button.setText(themeList.getThemes().get(1).getName());

                this.theme1Button.setEnabled(true);
                this.theme2Button.setEnabled(true);

                SharedPreferences settings = this.getActivity().getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
                boolean theme1enabled = settings.getBoolean(ApplicationHelper.THEME_PREFS_PREFIX+this.theme1, false);
                boolean theme2enabled = settings.getBoolean(ApplicationHelper.THEME_PREFS_PREFIX+this.theme2, false);

                if(theme1enabled || theme2enabled)
                    HomeFragment.this.setThemeButtonColor(theme1enabled);
            }
        }else if(response instanceof Theme){
            Theme theme = (Theme)response;

            SharedPreferences settings = this.getActivity().getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean(ApplicationHelper.THEME_PREFS_PREFIX+String.valueOf(theme.getId()), true);

            editor.remove((this.theme1 == theme.getId())?ApplicationHelper.THEME_PREFS_PREFIX+String.valueOf(this.theme2):ApplicationHelper.THEME_PREFS_PREFIX+String.valueOf(this.theme1));
            editor.apply();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case  ApplicationHelper.UPDATE_PICTURE:
                Bundle args = data.getBundleExtra(ApplicationHelper.EXTRA_PICTURES_LIST);
                if(args == null)
                    return;

                this.picture = (Picture) args.getSerializable(ApplicationHelper.EXTRA_PICTURES_LIST);
                this.updatePicture();
                break;
            case UPLOAD_PICTURE:
                if (data != null) {
                    Uri picturePath = data.getData();

                    try{
                        InputStream in = getActivity().getContentResolver().openInputStream(picturePath);
                        Cursor filenameCursor = getActivity().getContentResolver().query(picturePath, null, null, null, null);

                        if(filenameCursor != null && filenameCursor.moveToFirst())
                        {
                            this.fab.setVisibility(View.GONE);
                            this.uploadProgress.setVisibility(View.VISIBLE);
                            String filename = filenameCursor.getString(filenameCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            UploadPictureTask uploadTask = new UploadPictureTask(in, filename, this.uploadProgress, this.fab, this.getActivity(), this);
                            uploadTask.execute();
                        }else
                            Toast.makeText(this.getActivity(), R.string.upload_error, Toast.LENGTH_LONG);
                    }catch (Exception e){
                        Toast.makeText(this.getActivity(), R.string.upload_error, Toast.LENGTH_LONG);
                    }
                }
                break;
        }
    }

    @Override
    public void asyncTaskSuccess(boolean positive) {
        VotePictureTask voteTask = new VotePictureTask(this.picture, positive, this.getActivity(), this);
        voteTask.execute();
    }
}