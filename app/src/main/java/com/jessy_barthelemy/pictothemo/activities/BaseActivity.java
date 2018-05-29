package com.jessy_barthelemy.pictothemo.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.apiObjects.Picture;
import com.jessy_barthelemy.pictothemo.apiObjects.PictureList;
import com.jessy_barthelemy.pictothemo.apiObjects.User;
import com.jessy_barthelemy.pictothemo.asyncInteractions.GetPicturesInfoTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.SaveImageToDiskTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.UploadPictureTask;
import com.jessy_barthelemy.pictothemo.fragments.AboutFragment;
import com.jessy_barthelemy.pictothemo.fragments.HomeFragment;
import com.jessy_barthelemy.pictothemo.fragments.PicturesFragment;
import com.jessy_barthelemy.pictothemo.fragments.ProfilFragment;
import com.jessy_barthelemy.pictothemo.fragments.SearchFragment;
import com.jessy_barthelemy.pictothemo.fragments.SettingsFragment;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.interfaces.IBackPressedEventHandler;
import com.jessy_barthelemy.pictothemo.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IAsyncApiObjectResponse {

    private static final int SAVE_PICTURE_MENU = 2;
    private static final int SET_WALLPAPER_MENU = 3;
    public static final int REMOVE_PICTURE = 4;

    private static final int SAVE_PICTURE_DESTINATION = 3;
    private static final int UPLOAD_PICTURE_MENU = 5;
    private static final String CURRENT_FRAGMENT = "CURRENT";
    private ImageView pictureToSave;
    private LinearLayout connectionFailedLayout;
    private Button refreshFragment;

    protected NavigationView navigationView;


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

        this.connectionFailedLayout = (LinearLayout) findViewById(R.id.connection_failed);
        this.refreshFragment = (Button) findViewById(R.id.refresh_fragment);

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);

        Fragment home = new HomeFragment();
        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, home)
                .commit();

        this.refreshFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                ApplicationHelper.setTitleBarColor(BaseActivity.this, R.color.colorPrimaryDark, R.color.colorPrimary);

                BaseActivity.this.connectionFailedLayout.setVisibility(View.GONE);
                fragmentManager.beginTransaction().detach(currentFragment).attach(currentFragment).commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);

        if(fragment instanceof IBackPressedEventHandler)
            ((IBackPressedEventHandler)fragment).handleBackPress();
        else{
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer != null && drawer.isDrawerOpen(GravityCompat.START))
                drawer.closeDrawer(GravityCompat.START);
        }

        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(item.getTitle());

        switch(id){
            case R.id.nav_potd:
                fragment = new HomeFragment();
                break;
            case R.id.nav_profil:
                User user = ApplicationHelper.getCurrentUser(this);
                ProfilFragment profil = new ProfilFragment();
                profil.setUserId(user.getId());
                fragment = profil;
                break;
            case R.id.nav_logout:
                ApplicationHelper.resetPreferences(this);
                ApplicationHelper.restartApp(this);
                break;
            case R.id.nav_send:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, UPLOAD_PICTURE_MENU);
                break;
            case R.id.nav_picture_month:
                Calendar startOfMonth = Calendar.getInstance();
                startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
                GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(startOfMonth, Calendar.getInstance(), null, null, null, true, this, this);
                getPicturesInfosTask.execute();
                break;
            case R.id.nav_search:
                fragment = new SearchFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_about:
                fragment = new AboutFragment();
                break;
        }

        if(fragment != null)
            this.setCurrentFragment(fragment, false);

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
                menu.add(Menu.NONE, SET_WALLPAPER_MENU, Menu.NONE, R.string.set_as_wallaper);
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
            case SET_WALLPAPER_MENU:
                BitmapDrawable pictureDrawable = (BitmapDrawable) this.pictureToSave.getDrawable();

                ApplicationHelper.setWallpaper(this, pictureDrawable.getBitmap());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        switch(requestCode){
            case SAVE_PICTURE_DESTINATION:
                if (resultData != null) {
                    Uri picturePath = resultData.getData();
                    BitmapDrawable pictureDrawable = (BitmapDrawable) this.pictureToSave.getDrawable();

                    try {
                        DocumentFile pictureFolder = DocumentFile.fromSingleUri(this, picturePath);
                        OutputStream out = getContentResolver().openOutputStream(pictureFolder.getUri());
                        SaveImageToDiskTask saveTask = new SaveImageToDiskTask(pictureDrawable.getBitmap(), out, this, this);
                        saveTask.execute();

                    } catch (IOException ignored) {}
                    this.pictureToSave = null;
                }
                break;
            case UPLOAD_PICTURE_MENU:
                if (resultData != null) {
                    Uri picturePath = resultData.getData();
                    Cursor filenameCursor = null;
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picturePath);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, ApplicationHelper.UPLOAD_IMAGE_COMPRESSION, bos);

                        byte[] bitmapdata = bos.toByteArray();

                        filenameCursor = this.getContentResolver().query(picturePath, null, null, null, null);

                        if(filenameCursor != null && filenameCursor.moveToFirst())
                        {
                            String filename = filenameCursor.getString(filenameCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            UploadPictureTask uploadTask = new UploadPictureTask(bitmapdata, filename, null, null, this.getApplicationContext(), this);
                            uploadTask.execute();
                        }else
                            Toast.makeText(this, R.string.upload_error, Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        Toast.makeText(this, R.string.upload_error, Toast.LENGTH_LONG).show();
                    }finally {
                        if(filenameCursor != null)
                            filenameCursor.close();
                    }
                }
                break;
        }
    }

    @Override
    public void asyncTaskSuccess(Object response) {
        if(response instanceof String){
            Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
        }
        else if(response instanceof PictureList){
            PictureList pictureList = (PictureList)response;
            ArrayList<Picture> pictures = pictureList.getPictures();

            if(pictures == null || pictures.size() == 0){
                Toast.makeText(this, R.string.save_picture_empty, Toast.LENGTH_LONG).show();
                return;
            }

            PicturesFragment picturesFragment = new PicturesFragment();
            picturesFragment.setPictures(pictures);

            this.setCurrentFragment(picturesFragment, true);
        }
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleNoConnection() {
        ApplicationHelper.setTitleBarColor(this, R.color.colorAccentDark, R.color.colorAccent);
        this.connectionFailedLayout.setVisibility(View.VISIBLE);
    }

    protected void setCurrentFragment(Fragment fragment, boolean addToBackStack){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(R.id.fragment_container,  fragment, CURRENT_FRAGMENT);

        if(addToBackStack)
            transaction.addToBackStack(fragment.getClass().getName());
        else{
            int count = fragmentManager.getBackStackEntryCount();

            while(count > 0){
                fragmentManager.popBackStack();
                count--;
            }
        }
        transaction.commit();
    }

    public void openPOTD(Picture picture){
        ArrayList<Picture> pictures = new ArrayList<>();
        pictures.add(picture);

        PicturesFragment picturesFragment = new PicturesFragment();
        picturesFragment.setPictures(pictures);

        this.setCurrentFragment(picturesFragment, true);
    }

    public void openProfil(long userId){
        ProfilFragment profilFragment = new ProfilFragment();
        profilFragment.setUserId(userId);
        this.setCurrentFragment(profilFragment, false);
    }
}