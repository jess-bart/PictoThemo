package com.jessy_barthelemy.pictothemo.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.ApiObjects.Comment;
import com.jessy_barthelemy.pictothemo.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CommentArrayAdapter extends ArrayAdapter<Comment> {

    private int layoutResource;

    public CommentArrayAdapter(Context context, int resource, ArrayList<Comment> items) {
        super(context, resource, items);
        this.layoutResource = resource;
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
            TextView commentText = (TextView) view.findViewById(R.id.comment_text);
            TextView commentAuthor = (TextView) view.findViewById(R.id.comment_author);
            TextView commentDate = (TextView) view.findViewById(R.id.comment_date);

            commentText.setText(comment.getText());
            commentAuthor.setText(comment.getUser().getPseudo());

            DateFormat formater = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
            commentDate.setText(formater.format(comment.getDate().getTime()));
        }

        return view;
    }

}