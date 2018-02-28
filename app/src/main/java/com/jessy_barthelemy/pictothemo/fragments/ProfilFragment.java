package com.jessy_barthelemy.pictothemo.fragments;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.apiObjects.Trophy;
import com.jessy_barthelemy.pictothemo.apiObjects.User;
import com.jessy_barthelemy.pictothemo.asyncInteractions.GetPicturesInfoTask;
import com.jessy_barthelemy.pictothemo.asyncInteractions.GetUserTask;
import com.jessy_barthelemy.pictothemo.dialogs.TrophyDialog;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.layouts.FlowLayout;
import com.jessy_barthelemy.pictothemo.R;

public class ProfilFragment extends BaseFragment implements IAsyncApiObjectResponse{

    private User user;
    private TextView profilUserName;
    private TextView profilRegistrationDate;
    private AppCompatButton profilPictures;
    private ImageView profilPicture;
    private FlowLayout trophyList;
    private ColorMatrixColorFilter disabledFilter;
    private TrophyDialog dialog;

    private RelativeLayout main;
    private RelativeLayout loader;

    public void setUserId(long userId){
        this.user = new User();
        this.user.setId(userId);

        GetUserTask userTask = new GetUserTask(this.user.getId(), this.getActivity(), this);
        userTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        this.profilUserName = (TextView) view.findViewById(R.id.profil_user_name);
        this.profilRegistrationDate = (TextView) view.findViewById(R.id.profil_registration_date);
        this.profilPictures = (AppCompatButton) view.findViewById(R.id.profil_pictures);
        this.trophyList = (FlowLayout) view.findViewById(R.id.trophy_list);
        this.profilPicture = (ImageView) view.findViewById(R.id.profil_picture);
        this.main = (RelativeLayout) view.findViewById(R.id.main);
        this.loader = (RelativeLayout) view.findViewById(R.id.loader);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        this.disabledFilter = new ColorMatrixColorFilter(matrix);

        return view;
    }

    @Override
    public void asyncTaskSuccess(Object response) {
        if (getActivity() == null) return;

        this.main.setVisibility(View.VISIBLE);
        this.loader.setVisibility(View.GONE);
        if(response instanceof User){
            this.user = (User)response;
            this.profilUserName.setText(this.user.getPseudo());
            String date = ApplicationHelper.convertDateToString(this.user.getRegistrationDate(), false, true);
            this.profilRegistrationDate.setText(this.getString(R.string.profil_registration_date, date));

            ImageView trophyImg;

            for(Trophy trophy : user.getTrophies()){
                trophyImg = new ImageView(this.getActivity());
                trophyImg.setImageResource(ProfilFragment.getProfilDrawableByName(this.getActivity(), trophy.getId(),  false));
                trophyImg.setPadding(0, 0, 15, 15);

                if(!trophy.isValidated())
                    trophyImg.setColorFilter(this.disabledFilter);

                this.trophyList.addView(trophyImg);
            }

            this.setProfilId(this.user.getProfil());

            this.profilPictures.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(null, null, null,
                                                                ProfilFragment.this.user.getPseudo(),
                                                                null,
                                                                null,
                                                                ProfilFragment.this.getActivity(),
                                                                ProfilFragment.this);
                    getPicturesInfosTask.execute();
                }
            });

            this.trophyList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfilFragment.this.dialog = new TrophyDialog(ProfilFragment.this, user);
                    ProfilFragment.this.dialog.show();
                }
            });

            this.profilPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfilFragment.this.dialog = new TrophyDialog(ProfilFragment.this, user);
                    ProfilFragment.this.dialog.show();
                }
            });
        }else{
            super.asyncTaskSuccess(response);
        }
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        if (getActivity() == null) return;

        super.asyncTaskFail(errorMessage);
        this.main.setVisibility(View.VISIBLE);
        this.loader.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(ProfilFragment.this.dialog != null && ProfilFragment.this.dialog.isShowing())
            ProfilFragment.this.dialog.dismiss();

        ProfilFragment.this.dialog = null;
    }

    public static int getProfilDrawableByName(Context context, int profil, boolean big){
        if(context == null)
            return -1;
        String name = big ? context.getString(R.string.profil_big) : context.getString(R.string.profil_normal);
        return context.getResources().getIdentifier(String.format(name, profil), "drawable", context.getPackageName());
    }

    public void setProfilId(int id){
        this.user.setProfil(id);
        this.profilPicture.setImageResource(ProfilFragment.getProfilDrawableByName(this.getActivity(), id,  true));
    }
}
