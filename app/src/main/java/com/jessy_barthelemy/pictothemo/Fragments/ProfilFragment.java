package com.jessy_barthelemy.pictothemo.Fragments;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetPicturesInfoTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetUserTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

public class ProfilFragment extends BaseFragment implements IAsyncApiObjectResponse{

    private User user;
    private TextView profilUserName;
    private TextView profilRegistrationDate;
    private AppCompatButton profilPictures;
    private RelativeLayout main;
    private RelativeLayout loader;

    public void setUserId(int userId){
        this.user = new User();
        this.user.setId(userId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        this.profilUserName = (TextView) view.findViewById(R.id.profil_user_name);
        this.profilRegistrationDate = (TextView) view.findViewById(R.id.profil_registration_date);
        this.profilPictures = (AppCompatButton) view.findViewById(R.id.profil_pictures);
        this.main = (RelativeLayout) view.findViewById(R.id.main);
        this.loader = (RelativeLayout) view.findViewById(R.id.loader);

        if(this.user != null){
            GetUserTask userTask = new GetUserTask(this.user.getId(), this.getActivity(), this);
            userTask.execute();
        }

        return view;
    }

    @Override
    public void asyncTaskSuccess(Object response) {
        this.main.setVisibility(View.VISIBLE);
        this.loader.setVisibility(View.GONE);
        if(response instanceof User){
            this.user = (User)response;
            this.profilUserName.setText(this.user.getPseudo());
            String date = ApplicationHelper.convertDateToString(this.user.getRegistrationDate(), false, true);
            this.profilRegistrationDate.setText(this.getString(R.string.profil_registration_date, date));

            this.profilPictures.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetPicturesInfoTask getPicturesInfosTask = new GetPicturesInfoTask(null, null, null,
                                                                ProfilFragment.this.user.getPseudo(),
                                                                null,
                                                                ApiHelper.FLAG_COMMENTS,
                                                                ProfilFragment.this.getActivity(),
                                                                ProfilFragment.this);
                    getPicturesInfosTask.execute();
                }
            });
        }else{
            super.asyncTaskSuccess(response);
        }
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        super.asyncTaskFail(errorMessage);
        this.main.setVisibility(View.VISIBLE);
        this.loader.setVisibility(View.GONE);
    }
}
