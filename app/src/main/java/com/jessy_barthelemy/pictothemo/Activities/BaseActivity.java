package com.jessy_barthelemy.pictothemo.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.ApiObjects.PictureList;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetPicturesInfoTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.SaveImageToDiskTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IAsyncResponse, IAsyncApiObjectResponse {

    private static final int SAVE_PICTURE_MENU = 2;
    private static final int SAVE_PICTURE_DESTINATION = 3;
    private ImageView pictureToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.nav_logout:
                ApplicationHelper.resetPreferences(this);
                ApplicationHelper.restartApp(this);
                break;
            case R.id.nav_picture_month:
                GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(null, null, null, null, null, ApiHelper.FLAG_POTD+"|"+ApiHelper.FLAG_COMMENTS, this);
                getPicturesInfosTask.execute();
                break;
            case R.id.nav_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch(v.getId()){
            case R.id.picture:
                this.pictureToSave = (ImageView)v;
                menu.setHeaderTitle(R.string.picture);
                menu.add(Menu.NONE, SAVE_PICTURE_MENU, Menu.NONE, R.string.save_picture);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SAVE_PICTURE_MENU:
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/png");
                startActivityForResult(intent, SAVE_PICTURE_DESTINATION);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == SAVE_PICTURE_DESTINATION && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri picturePath = resultData.getData();
                BitmapDrawable pictureDrawable = (BitmapDrawable) this.pictureToSave.getDrawable();

                try {
                    DocumentFile pictureFolder = DocumentFile.fromSingleUri(this, picturePath);
                    OutputStream out = getContentResolver().openOutputStream(pictureFolder.getUri());
                    SaveImageToDiskTask saveTask = new SaveImageToDiskTask(pictureDrawable.getBitmap(), out, this, this);
                    saveTask.execute();

                } catch (IOException e) {}
                this.pictureToSave = null;
            }
        }
    }

    //used for image saving
    @Override
    public void asyncTaskSuccess() {
        Toast.makeText(this, this.getResources().getString(R.string.save_picture_success), Toast.LENGTH_LONG).show();
    }

    @Override
    public void asyncTaskSuccess(Object response) {
        if(response instanceof PictureList){
            PictureList pictureList = (PictureList)response;
            ArrayList<Picture> pictures = pictureList.getPictures();

            if(pictures == null || pictures.size() == 0)
                return;

            Intent intent = new Intent(this, PicturesActivity.class);

            Bundle args = new Bundle();
            args.putSerializable(ApplicationHelper.EXTRA_PICTURES_LIST, pictures);
            intent.putExtra(ApplicationHelper.EXTRA_PICTURES_LIST, args);
            startActivityForResult(intent, ApplicationHelper.UPDATE_PICTURE);
        }
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}
