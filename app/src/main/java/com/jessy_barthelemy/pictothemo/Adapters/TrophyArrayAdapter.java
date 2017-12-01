package com.jessy_barthelemy.pictothemo.Adapters;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.ApiObjects.Trophy;
import com.jessy_barthelemy.pictothemo.Fragments.ProfilFragment;
import com.jessy_barthelemy.pictothemo.R;

import java.util.ArrayList;

public class TrophyArrayAdapter extends ArrayAdapter<Trophy> {

    private int layoutResource;
    private Context context;
    private ColorMatrixColorFilter disabledFilter;

    public TrophyArrayAdapter(Context context, int resource, ArrayList<Trophy> items) {
        super(context, resource, items);
        this.layoutResource = resource;
        this.context = context;

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        this.disabledFilter = new ColorMatrixColorFilter(matrix);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(this.layoutResource, null);
        }

        Trophy trophy = getItem(position);

        if (trophy != null) {
            ImageView trophyImg = (ImageView)view.findViewById(R.id.trophy_img);
            TextView trophyName = (TextView)view.findViewById(R.id.trophy_name);
            TextView trophyDesc = (TextView)view.findViewById(R.id.trophy_desc);

            trophyImg.setImageResource(ProfilFragment.getProfilDrawableByName(context, trophy.getId(), true));
            trophyName.setText(trophy.getTitle());
            trophyDesc.setText(trophy.getDescription());

            if(!trophy.isValidated()) {
                trophyImg.setColorFilter(this.disabledFilter);
            }else{
                trophyImg.setColorFilter(null);
            }
        }

        return view;
    }

}