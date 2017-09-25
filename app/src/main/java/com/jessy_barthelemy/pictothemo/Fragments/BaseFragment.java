package com.jessy_barthelemy.pictothemo.Fragments;

import android.app.Fragment;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.Activities.BaseActivity;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;

public class BaseFragment extends Fragment implements IAsyncApiObjectResponse {
    @Override
    public void asyncTaskSuccess(Object response) {
        ((BaseActivity)this.getActivity()).asyncTaskSuccess(response);
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        Toast.makeText(this.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }
}
