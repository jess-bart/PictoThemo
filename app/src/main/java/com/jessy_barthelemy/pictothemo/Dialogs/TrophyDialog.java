package com.jessy_barthelemy.pictothemo.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.jessy_barthelemy.pictothemo.Adapters.TrophyArrayAdapter;
import com.jessy_barthelemy.pictothemo.ApiObjects.Trophy;
import com.jessy_barthelemy.pictothemo.R;

import java.util.ArrayList;

public class TrophyDialog extends Dialog{

    private ListView trophyListView;
    private ArrayList<Trophy> trophies;
    private Activity activity;

    public TrophyDialog(Activity activity, ArrayList<Trophy> trophies) {
        super(activity);
        this.activity = activity;
        this.trophies = trophies;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.trophy_dialog);

        this.trophyListView = (ListView) this.findViewById(R.id.trophy_list);

        TrophyArrayAdapter adapter = new TrophyArrayAdapter(activity.getBaseContext(), R.layout.trophy_rows, this.trophies);
        this.trophyListView.setAdapter(adapter);
    }
}