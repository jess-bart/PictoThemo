package com.jessy_barthelemy.pictothemo.exceptions;

import org.json.JSONException;
import org.json.JSONObject;

public class PictothemoException extends Exception{
    private final String ERROR_MESSAGE = "message";

    private String message;

    public PictothemoException(JSONObject error) {
        if (error == null)
            return;

        try {
            this.message = (error.has(ERROR_MESSAGE)) ? error.getString(ERROR_MESSAGE) : null;
        } catch (JSONException e) {
        }
    }

    public String getMessage(){
        return this.message;
    }
}
