package com.jessy_barthelemy.pictothemo.ApiObjects;

import com.jessy_barthelemy.pictothemo.Enum.CommentStatus;

public class CommentResult {

    private Comment comment;
    private CommentStatus result;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public CommentStatus getResult() {
        return result;
    }

    public void setResult(CommentStatus result) {
        this.result = result;
    }
}
