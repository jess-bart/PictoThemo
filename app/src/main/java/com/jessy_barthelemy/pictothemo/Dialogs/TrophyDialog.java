package com.jessy_barthelemy.pictothemo.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.Adapters.TrophyArrayAdapter;
import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.SetUserTask;
import com.jessy_barthelemy.pictothemo.Fragments.ProfilFragment;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

public class TrophyDialog extends Dialog implements IAsyncApiObjectResponse {

    private ListView trophyListView;
    private User user;
    private Activity activity;
    private ProfilFragment fragment;

    public TrophyDialog(ProfilFragment fragment, User user) {
        super(fragment.getActivity());
        this.activity = fragment.getActivity();
        this.user = user;
        this.fragment = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.trophy_dialog);

        this.trophyListView = (ListView) this.findViewById(R.id.trophy_list);

        TrophyArrayAdapter adapter = new TrophyArrayAdapter(activity.getBaseContext(), R.layout.trophy_rows, this.user.getTrophies());
        this.trophyListView.setAdapter(adapter);

        this.trophyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = ApplicationHelper.getCurrentUser(TrophyDialog.this.fragment.getActivity());
                if(user.getId() == TrophyDialog.this.user.getId() && TrophyDialog.this.user.getTrophies().get(i).isValidated()) {
                    TrophyDialog.this.fragment.setProfilId(TrophyDialog.this.user.getTrophies().get(i).getId());
                    SetUserTask task = new SetUserTask(TrophyDialog.this.user, TrophyDialog.this.fragment.getActivity(), TrophyDialog.this);
                    task.execute();
                }
            }
        });
    }

    @Override
    public void asyncTaskSuccess(Object response) {
        if(response instanceof Boolean){
            this.fragment.setProfilId(user.getProfil());
            this.dismiss();
        }
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        Toast.makeText(this.fragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();

    }
}