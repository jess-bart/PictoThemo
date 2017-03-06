package com.jessy_barthelemy.pictothemo.Activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.Api.Picture;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.PictureTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class HomeActivity extends BaseActivity implements IAsyncApiObjectResponse {

    private ImageView potdView;
    private AppCompatButton theme1;
    private AppCompatButton theme2;
    private TextView potd_pseudo;
    private TextView potd_theme;
    private TextView potd_positive_vote;
    private TextView potd_negative_vote;
    private Picture potd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout contentFrameLayout = (RelativeLayout) findViewById(R.id.content_main);
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);

        this.potdView = (ImageView)findViewById(R.id.home_potd);
        this.theme1 = (AppCompatButton) findViewById(R.id.home_theme_1);
        this.theme2 = (AppCompatButton)findViewById(R.id.home_theme_2);
        this.potd_pseudo = (TextView)findViewById(R.id.home_potd_pseudo);
        this.potd_theme = (TextView)findViewById(R.id.home_potd_theme);
        this.potd_positive_vote = (TextView)findViewById(R.id.home_potd_positive_vote);
        this.potd_negative_vote = (TextView)findViewById(R.id.home_potd_negative_vote);

        Point wSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(wSize);
        this.potdView.setMaxHeight(wSize.x);

        Picasso.with(this)
                .load(ApiHelper.URL_POTD+(ApiHelper.RES_FORMAT.format(new Date())))
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_menu_send)
                .into(this.potdView);

        this.theme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme1.setSupportBackgroundTintList(ContextCompat.getColorStateList(HomeActivity.this, R.color.colorAccent));
                theme2.setSupportBackgroundTintList(ContextCompat.getColorStateList(HomeActivity.this, R.color.colorDisabled));
            }
        });

        this.theme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme1.setSupportBackgroundTintList(ContextCompat.getColorStateList(HomeActivity.this, R.color.colorDisabled));
                theme2.setSupportBackgroundTintList(ContextCompat.getColorStateList(HomeActivity.this, R.color.colorPrimary));
            }
        });

        PictureTask pictureTask = new PictureTask(new Date(), ApiHelper.FLAG_POTD, this);
        pictureTask.execute();
    }

    @Override
    public void asyncTaskSuccess(Object response) {
        if(response instanceof Picture){
            this.potd = (Picture)response;
            this.potd_pseudo.setText(ApplicationHelper.handleUnknowPseudo(this, this.potd.getUser().getPseudo()));
            this.potd_theme.setText("#"+this.potd.getTheme());
            this.potd_positive_vote.setText(String.valueOf(this.potd.getPositiveVote()));
            this.potd_negative_vote.setText(String.valueOf(this.potd.getNegativeVote()));
        }
    }

    @Override
    public void asyncTaskFail(String errorMessage) {

    }
}
