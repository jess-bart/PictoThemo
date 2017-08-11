package com.jessy_barthelemy.pictothemo.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.ApiObjects.Theme;
import com.jessy_barthelemy.pictothemo.ApiObjects.ThemeList;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetImageTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetPicturesInfoTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetThemeTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.VotePictureTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.VoteThemeTask;
import com.jessy_barthelemy.pictothemo.Dialogs.VoteDialog;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.Interfaces.IVoteResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends BaseActivity implements IAsyncApiObjectResponse, IVoteResponse {

    private ImageView potdView;
    private AppCompatButton theme1Button;
    private AppCompatButton theme2Button;
    private int theme1;
    private int theme2;
    private TextView potdPseudo;
    private TextView potdTheme;
    private TextView potdPositiveVote;
    private TextView potdNegativeVote;
    private View potdVote;
    private TextView potdCommentCount;
    private Picture potd;
    private Point wSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout contentFrameLayout = (RelativeLayout) findViewById(R.id.content_main);
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);

        this.potdView = (ImageView)findViewById(R.id.picture);
        this.potdPseudo = (TextView)findViewById(R.id.picture_pseudo);
        this.potdTheme = (TextView)findViewById(R.id.picture_theme);
        this.potdPositiveVote = (TextView)findViewById(R.id.picture_positive_vote);
        this.potdNegativeVote = (TextView)findViewById(R.id.picture_negative_vote);
        this.potdVote = findViewById(R.id.vote);
        this.potdCommentCount = (TextView)findViewById(R.id.picture_comment_count);

        this.theme1Button = (AppCompatButton) findViewById(R.id.home_theme_1);
        this.theme2Button = (AppCompatButton)findViewById(R.id.home_theme_2);

        View potdLoadbar = findViewById(R.id.picture_loadbar);
        TextView theme_title = (TextView)findViewById(R.id.home_theme_title);

        this.wSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(this.wSize);
        this.potdView.setMaxHeight(this.wSize.x);

        GetImageTask imageTask = new GetImageTask(this, this.potdView, potdLoadbar, Calendar.getInstance());
        imageTask.execute();

        this.theme1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.this.setThemeButtonColor(true);
                VoteThemeTask voteTask = new VoteThemeTask(HomeActivity.this.theme1, HomeActivity.this, HomeActivity.this);
                voteTask.execute();
            }
        });

        this.theme2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.this.setThemeButtonColor(false);
                VoteThemeTask voteTask = new VoteThemeTask(HomeActivity.this.theme2, HomeActivity.this, HomeActivity.this);
                voteTask.execute();
            }
        });

        Calendar today = Calendar.getInstance();
        GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(today, ApiHelper.FLAG_POTD+"|"+ApiHelper.FLAG_COMMENTS, this);
        getPicturesInfosTask.execute();

        GetThemeTask themeTask = new GetThemeTask(today, this);
        themeTask.execute();
        theme_title.setText(getResources().getString(R.string.theme_title, this.getThemeDay()));
        this.registerForContextMenu(this.potdView);
    }

    private void setThemeButtonColor(boolean theme1){
        ColorStateList disabled = ContextCompat.getColorStateList(HomeActivity.this, R.color.colorDisabled);
        if(theme1){
            ViewCompat.setBackgroundTintList(this.theme1Button, ContextCompat.getColorStateList(HomeActivity.this, R.color.colorAccent));
            ViewCompat.setBackgroundTintList(this.theme2Button, disabled);
        }else{
            ViewCompat.setBackgroundTintList(this.theme1Button, disabled);
            ViewCompat.setBackgroundTintList(this.theme2Button, ContextCompat.getColorStateList(HomeActivity.this, R.color.colorPrimary));
        }
    }

    private String getThemeDay(){
        SimpleDateFormat dateFormat;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            dateFormat = new SimpleDateFormat("EEEE", getResources().getConfiguration().getLocales().get(0));
        else
            dateFormat = new SimpleDateFormat("EEEE", getResources().getConfiguration().locale);
        Calendar themeDay = Calendar.getInstance();
        themeDay.add(Calendar.DAY_OF_YEAR, 2);
        return dateFormat.format(themeDay.getTime());
    }

    @Override
    public void asyncTaskSuccess(Object response) {
        if(response instanceof Picture){
            this.potd = (Picture)response;
            this.potdPseudo.setText(ApplicationHelper.handleUnknowPseudo(this, this.potd.getUser().getPseudo()));
            this.potdTheme.setText(String.format(getResources().getString(R.string.theme_name), this.potd.getTheme()));
            this.potdPositiveVote.setText(String.valueOf(this.potd.getPositiveVote()));
            this.potdNegativeVote.setText(String.valueOf(this.potd.getNegativeVote()));
            this.potdCommentCount.setText(String.valueOf(this.potd.getComments().size()));

            this.potdCommentCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity.this.openPOTD();
                }
            });

            this.potdView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                HomeActivity.this.openPOTD();
                }
            });

            this.potdVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                VoteDialog dialog = new VoteDialog(HomeActivity.this, HomeActivity.this);
                dialog.show();
                }
            });

        }else if(response instanceof ThemeList){
            ThemeList themeList = (ThemeList) response;
            if(themeList.getThemes() != null && themeList.getThemes().size() > 1){
                this.theme1 = themeList.getThemes().get(0).getId();
                this.theme2 = themeList.getThemes().get(1).getId();

                this.theme1Button.setText(themeList.getThemes().get(0).getName());
                this.theme2Button.setText(themeList.getThemes().get(1).getName());

                this.theme1Button.setEnabled(true);
                this.theme2Button.setEnabled(true);

                SharedPreferences settings = this.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
                boolean theme1enabled = settings.getBoolean(ApplicationHelper.THEME_PREFS_PREFIX+this.theme1, false);
                boolean theme2enabled = settings.getBoolean(ApplicationHelper.THEME_PREFS_PREFIX+this.theme2, false);

                if(theme1enabled || theme2enabled)
                    HomeActivity.this.setThemeButtonColor(theme1enabled);
            }
        }else if(response instanceof Theme){
            Theme theme = (Theme)response;

            SharedPreferences settings = this.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean(ApplicationHelper.THEME_PREFS_PREFIX+String.valueOf(theme.getId()), true);

            editor.remove((this.theme1 == theme.getId())?ApplicationHelper.THEME_PREFS_PREFIX+String.valueOf(this.theme2):ApplicationHelper.THEME_PREFS_PREFIX+String.valueOf(this.theme1));
            editor.apply();
        }
    }

    private void openPOTD(){
        Intent intent = new Intent(HomeActivity.this, PicturesActivity.class);

        ArrayList<Picture> pictures = new ArrayList<>();
        pictures.add(HomeActivity.this.potd);
        Bundle args = new Bundle();
        args.putSerializable(ApplicationHelper.EXTRA_PICTURES_LIST, pictures);
        intent.putExtra(ApplicationHelper.EXTRA_PICTURES_LIST, args);
        startActivityForResult(intent, ApplicationHelper.UPDATE_PICTURE);
    }

    @Override
    public void asyncTaskSuccess(boolean positive) {
        VotePictureTask voteTask = new VotePictureTask(this.potd, positive, this, this);
        voteTask.execute();
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ApplicationHelper.UPDATE_PICTURE){
            Bundle args = data.getBundleExtra(ApplicationHelper.EXTRA_PICTURES_LIST);
            if(args == null)
                return;

            this.potd = (Picture) args.getSerializable(ApplicationHelper.EXTRA_PICTURES_LIST);
            this.asyncTaskSuccess(this.potd);
        }
    }
}
