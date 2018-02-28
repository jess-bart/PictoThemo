package com.jessy_barthelemy.pictothemo.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.jessy_barthelemy.pictothemo.interfaces.IVoteResponse;
import com.jessy_barthelemy.pictothemo.R;

public class VoteDialog extends Dialog{

    private Button plus, minus;
    private IVoteResponse delegate;

    public VoteDialog(Activity activity, IVoteResponse delegate) {
        super(activity);
        this.delegate = delegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.vote_dialog);
        this.plus = (Button) findViewById(R.id.vote_plus);
        this.minus = (Button) findViewById(R.id.vote_minus);

        this.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            VoteDialog.this.vote(true);
            }
        });

        this.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteDialog.this.vote(false);
            }
        });
    }

    private void vote(boolean positive){
        this.delegate.asyncTaskSuccess(positive);
        this.dismiss();
    }
}
