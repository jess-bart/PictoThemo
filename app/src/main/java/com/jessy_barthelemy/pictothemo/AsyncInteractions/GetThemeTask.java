package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.Theme;
import com.jessy_barthelemy.pictothemo.ApiObjects.ThemeList;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class GetThemeTask extends AsyncTask<String, Void, ThemeList> {

    private Calendar date;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;

    public GetThemeTask(Calendar date, IAsyncApiObjectResponse delegate){
        this.date = date;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected ThemeList doInBackground(String... params) {
        ArrayList<Theme> themeList = new ArrayList<Theme>();
        ApiHelper helper = new ApiHelper();
        try {
            JSONObject response = null;
            response = helper.getThemes(this.date);

            if(response.length() == 0)
                return null;

            JSONArray themes = response.getJSONArray(ApiHelper.ENTITY_THEMES);
            for (int i = 0, len = themes.length();i < len; i++) {
                JSONObject theme = themes.getJSONObject(i);
                int id = theme.has(ApiHelper.ID)?theme.getInt(ApiHelper.ID):0;
                String name = theme.has(ApiHelper.THEME_NAME)?theme.getString(ApiHelper.THEME_NAME):"";
                themeList.add(new Theme(id, name));
            }
        } catch (Exception e) {
            themeList = null;
        }

        return new ThemeList(themeList);
    }

    protected void onPostExecute(ThemeList themes) {
        delegate.asyncTaskSuccess(themes);
    }
}