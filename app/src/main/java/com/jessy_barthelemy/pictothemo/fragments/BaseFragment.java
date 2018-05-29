package com.jessy_barthelemy.pictothemo.fragments;

import android.app.Fragment;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.activities.BaseActivity;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

public class BaseFragment extends Fragment implements IAsyncApiObjectResponse {
    @Override
    public void asyncTaskSuccess(Object response) {
        if (getActivity() == null) return;

        ((BaseActivity)this.getActivity()).asyncTaskSuccess(response);
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        if (getActivity() == null) return;

        Toast.makeText(this.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleNoConnection() {
        ((BaseActivity)this.getActivity()).handleNoConnection();
    }
}
