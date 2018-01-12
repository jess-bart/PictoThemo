package com.jessy_barthelemy.pictothemo.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.ApiObjects.Comment;
import com.jessy_barthelemy.pictothemo.Fragments.ProfilFragment;
import com.jessy_barthelemy.pictothemo.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CommentArrayAdapter extends ArrayAdapter<Comment> {

    private int layoutResource;
    private Context context;

    public CommentArrayAdapter(Context context, int resource, ArrayList<Comment> items) {
        super(context, resource, items);
        this.layoutResource = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(this.layoutResource, null);
        }

        Comment comment = getItem(position);

        if (comment != null) {
            ImageView commentProfil = (ImageView) view.findViewById(R.id.comment_profil);
            TextView commentText = (TextView) view.findViewById(R.id.comment_text);
            TextView commentAuthor = (TextView) view.findViewById(R.id.comment_author);
            TextView commentDate = (TextView) view.findViewById(R.id.comment_date);

            commentText.setText(comment.getText());
            commentAuthor.setText(comment.getUser().getPseudo());
            if(comment.getUser() != null)
                commentProfil.setImageResource(ProfilFragment.getProfilDrawableByName(this.context, comment.getUser().getProfil(),  false));

            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
            commentDate.setText(formatter.format(comment.getDate().getTime()));
        }

        return view;
    }

}