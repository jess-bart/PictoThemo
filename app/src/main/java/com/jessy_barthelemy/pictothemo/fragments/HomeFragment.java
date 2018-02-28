package com.jessy_barthelemy.pictothemo.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.activities.BaseActivity;
import com.jessy_barthelemy.pictothemo.apiObjects.Picture;
import com.jessy_barthelemy.pictothemo.apiObjects.PictureList;
import com.jessy_barthelemy.pictothemo.apiObjects.Theme;
import com.jessy_barthelemy.pictothemo.apiObjects.ThemeList;
import com.jessy_barthelemy.pictothemo.asyncInteractions.AddCommentTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.GetImageTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.GetPicturesInfoTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.GetThemeTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.UploadPictureTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.VotePictureTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.VoteThemeTask;
import com.jessy_barthelemy.pictothemo.dialogs.VoteDialog;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IVoteResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends BaseFragment implements IVoteResponse {

    private static final int UPLOAD_PICTURE = 4;

    private Theme theme1;
    private Theme theme2;
    private ImageView pictureView;
    private ImageView pictureProfil;
    private AppCompatButton voteThemeButton;
    private AppCompatButton theme1Button;
    private AppCompatButton theme2Button;
    private TextView picturePseudo;
    private TextView pictureTheme;
    private TextView pictureDate;
    private TextView picturePositiveVote;
    private TextView pictureNegativeVote;
    private View pictureVote;
    private LinearLayout themeLayout;
    private TextView pictureCommentCount;
    private Picture picture;
    private boolean localRequest;
    private View uploadProgress;
    private FloatingActionButton fab;

    public void showReviewDialog(){
        new AlertDialog.Builder(this.getActivity())
                .setTitle(R.string.review_title)
                .setMessage(R.string.review_description)
                .setPositiveButton(R.string.review_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApplicationHelper.setShowReview(HomeFragment.this.getActivity());

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=com.jessy_barthelemy.pictothemo"));
                        HomeFragment.this.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.review_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApplicationHelper.setShowReview(HomeFragment.this.getActivity());
                    }
                })
                .setNeutralButton(R.string.review_later, null)
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.pictureView = (ImageView)view.findViewById(R.id.picture);
        this.pictureProfil = (ImageView)view.findViewById(R.id.picture_profil);
        this.picturePseudo = (TextView)view.findViewById(R.id.picture_pseudo);
        this.pictureTheme = (TextView)view.findViewById(R.id.picture_theme);
        this.pictureDate = (TextView)view.findViewById(R.id.picture_date);

        this.picturePositiveVote = (TextView)view.findViewById(R.id.picture_positive_vote);
        this.pictureNegativeVote = (TextView)view.findViewById(R.id.picture_negative_vote);
        this.pictureVote = view.findViewById(R.id.vote);
        this.pictureCommentCount = (TextView)view.findViewById(R.id.picture_comment_count);

        this.voteThemeButton = (AppCompatButton)view.findViewById(R.id.home_vote);
        this.theme1Button = (AppCompatButton)view.findViewById(R.id.home_theme_1);
        this.theme2Button = (AppCompatButton)view.findViewById(R.id.home_theme_2);
        this.themeLayout = (LinearLayout) view.findViewById(R.id.theme_layout);

        ImageView pictureImg = (ImageView) view.findViewById(R.id.is_potd);
        pictureImg.setVisibility(View.VISIBLE);

        View pictureLoadbar = view.findViewById(R.id.loadbar);
        this.uploadProgress = view.findViewById(R.id.upload_progress);

        this.fab = (FloatingActionButton) view.findViewById(R.id.fab);

        GetImageTask imageTask = new GetImageTask(this.getActivity(), this.pictureView, pictureLoadbar, Calendar.getInstance(), false);
        imageTask.execute();

        this.localRequest = true;
        Calendar today = Calendar.getInstance();
        GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(
                                                        today,
                                                        today,
                                                        null, null, null,
                                                        true,
                                                        this.getActivity(),
                                                        this);
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

        this.voteThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DATE, 1);
                GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(tomorrow, tomorrow, null, null, null, null, HomeFragment.this.getActivity(), HomeFragment.this);
                getPicturesInfosTask.execute();
            }
        });

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

        if(ApplicationHelper.hasToShowReview(HomeFragment.this.getActivity()))
            showReviewDialog();
        return view;
    }

    private void setThemeButtonColor(boolean theme1){
        int disabled = ContextCompat.getColor(HomeFragment.this.getActivity(), R.color.colorDisabled);
        if(theme1){
            this.theme1Button.getBackground().setColorFilter(ContextCompat.getColor(HomeFragment.this.getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC);
            this.theme2Button.getBackground().setColorFilter(disabled, PorterDuff.Mode.SRC);
        }else{
            this.theme1Button.getBackground().setColorFilter(disabled, PorterDuff.Mode.SRC);
            this.theme2Button.getBackground().setColorFilter(ContextCompat.getColor(HomeFragment.this.getActivity(), R.color.colorPrimary), PorterDuff.Mode.SRC);
        }
    }

    protected void updatePicture(){
        try{
            this.pictureProfil.setImageResource(ProfilFragment.getProfilDrawableByName(this.getActivity(), this.picture.getUser().getProfil(), false));
        }catch(Resources.NotFoundException ignored){}

        this.picturePseudo.setText(ApplicationHelper.handleUnknowPseudo(this.getActivity(), this.picture.getUser().getPseudo()));
        this.pictureTheme.setText(String.format(getResources().getString(R.string.theme_name), this.picture.getTheme().getName()));
        DateFormat formater = android.text.format.DateFormat.getDateFormat(this.getActivity());
        this.pictureDate.setText(formater.format(this.picture.getTheme().getCandidateDate().getTime()));

        this.picturePositiveVote.setText(String.valueOf(this.picture.getPositiveVote()));
        this.pictureNegativeVote.setText(String.valueOf(this.picture.getNegativeVote()));
        this.pictureCommentCount.setText(String.valueOf(this.picture.getComments().size()));

        this.picturePseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HomeFragment.this.picture.getUser().getId() > 0)
                    ((BaseActivity)HomeFragment.this.getActivity()).openProfil(HomeFragment.this.picture.getUser().getId());
            }
        });

        this.pictureProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HomeFragment.this.picture.getUser().getId() > 0)
                    ((BaseActivity)HomeFragment.this.getActivity()).openProfil(HomeFragment.this.picture.getUser().getId());
            }
        });

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
        if (getActivity() == null) return;

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
            if(themeList.getThemes() != null && themeList.getThemes().size() > 2){
                Theme theme0 = themeList.getThemes().get(0);
                this.theme1 = themeList.getThemes().get(1);
                this.theme2 = themeList.getThemes().get(2);

                this.voteThemeButton.setText(theme0.getName());
                this.theme1Button.setText(theme1.getName());
                this.theme2Button.setText(theme2.getName());

                this.voteThemeButton.setEnabled(true);
                this.theme1Button.setEnabled(true);
                this.theme2Button.setEnabled(true);

                SharedPreferences settings = this.getActivity().getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
                boolean theme1enabled = settings.getBoolean(ApplicationHelper.THEME_PREFS_PREFIX+this.theme1.getId(), false);
                boolean theme2enabled = settings.getBoolean(ApplicationHelper.THEME_PREFS_PREFIX+this.theme2.getId(), false);

                if(theme1enabled || theme2enabled)
                    HomeFragment.this.setThemeButtonColor(theme1enabled);
            }
        }else if(response instanceof Theme){
            Theme theme = (Theme)response;

            SharedPreferences settings = this.getActivity().getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean(ApplicationHelper.THEME_PREFS_PREFIX+String.valueOf(theme.getId()), true);

            editor.remove((this.theme1.getId() == theme.getId())?ApplicationHelper.THEME_PREFS_PREFIX+String.valueOf(this.theme2.getId()):ApplicationHelper.THEME_PREFS_PREFIX+String.valueOf(this.theme1.getId()));
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
                    Cursor filenameCursor = null;
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picturePath);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, ApplicationHelper.UPLOAD_IMAGE_COMPRESSION, bos);

                        byte[] bitmapdata = bos.toByteArray();
                        InputStream in = new ByteArrayInputStream(bitmapdata);
                        filenameCursor = getActivity().getContentResolver().query(picturePath, null, null, null, null);

                        if(filenameCursor != null && filenameCursor.moveToFirst())
                        {
                            this.fab.setVisibility(View.GONE);
                            this.uploadProgress.setVisibility(View.VISIBLE);
                            String filename = filenameCursor.getString(filenameCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                            UploadPictureTask uploadTask = new UploadPictureTask(in, filename, this.uploadProgress, this.fab, this.getActivity().getApplicationContext(), this);
                            uploadTask.execute();
                        }else
                            Toast.makeText(this.getActivity(), R.string.upload_error, Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        Toast.makeText(this.getActivity(), R.string.upload_error, Toast.LENGTH_LONG).show();
                    }finally {
                        if(filenameCursor != null)
                            filenameCursor.close();
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