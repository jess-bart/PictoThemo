package com.jessy_barthelemy.pictothemo;

import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformations;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.security.InvalidParameterException;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ApiHelperTest {

    @Test
    public void createDeleteUser() throws Exception{
        ApiHelper helper = new ApiHelper();

        TokenInformations result = helper.createUser("john.doe@test.com", "john");
        assertNotNull(result);

        boolean checkDuplicate = false;
        try{
            assertNotNull(helper.createUser("john.doe@test.com", "john"));
        }catch(InvalidParameterException e){
            checkDuplicate = true;
        }

        assertTrue(checkDuplicate);
        checkDuplicate = false;

        try{
            assertNotNull(helper.createUser("john.doe@test.com", "john2"));
        }catch(InvalidParameterException e){
            checkDuplicate = true;
        }

        assertTrue(checkDuplicate);
        helper.getAccessToken("john.doe@test.com", "john", null);
        assertTrue(helper.deleteUser("john.doe@test.com", result.getPassword()));
    }

    @Test
    public void getAccessToken() throws Exception{
        ApiHelper helper = new ApiHelper();

        TokenInformations result = helper.getAccessToken("john.always@test.fr", "johnjohn", null);

        assertNotNull(result);
        assertNotNull(result.getAccessToken());
        assertNotNull(result.getExpiresToken());

        result = null;
        result = helper.getAccessToken("john.always@test.fr", "fb1b0ce9e5e4183eeb800f73b01c6189", ApiHelper.FLAG_SALT);

        assertNotNull(result);
        assertNotNull(result.getAccessToken());
        assertNotNull(result.getExpiresToken());
    }

    @Test
    public void getPictures() throws  Exception{
        ApiHelper helper = new ApiHelper();

        JSONObject result = helper.getPictures(Calendar.getInstance(), null);
        JSONObject picture1 = result.getJSONArray(ApiHelper.ENTITY_PICTURES).getJSONObject(0);
        JSONObject picture2 = result.getJSONArray(ApiHelper.ENTITY_PICTURES).getJSONObject(1);

        assertEquals(result.getJSONArray(ApiHelper.ENTITY_PICTURES).length(), 2);
        assertEquals(picture1.getInt(ApiHelper.ID), 1);
        assertEquals(picture1.getString(ApiHelper.THEME_NAME), "Fire");
        assertEquals(picture1.getString(ApiHelper.PSEUDO), "");
        assertEquals(picture1.getInt(ApiHelper.POSITIVE_VOTE), 1);
        assertEquals(picture1.getInt(ApiHelper.NEGATIVE_VOTE), 1);

        assertEquals(picture2.getInt(ApiHelper.ID), 2);
        assertEquals(picture2.getString(ApiHelper.THEME_NAME), "Fire");
        assertEquals(picture2.getString(ApiHelper.PSEUDO), "john.always@test.fr");
        assertEquals(picture2.getInt(ApiHelper.POSITIVE_VOTE), 0);
        assertEquals(picture2.getInt(ApiHelper.NEGATIVE_VOTE), 2);

        result = helper.getPictures(Calendar.getInstance(), ApiHelper.FLAG_POTD);
        picture1 = result.getJSONArray(ApiHelper.ENTITY_PICTURES).getJSONObject(0);

        assertEquals(result.getJSONArray(ApiHelper.ENTITY_PICTURES).length(), 1);
        assertEquals(picture1.getInt(ApiHelper.ID), 1);
        assertEquals(picture1.getString(ApiHelper.THEME_NAME), "Fire");
        assertEquals(picture1.getString(ApiHelper.PSEUDO), "");
        assertEquals(picture1.getInt(ApiHelper.POSITIVE_VOTE), 1);
        assertEquals(picture1.getInt(ApiHelper.NEGATIVE_VOTE), 1);

        assertNotNull(result);
    }

    @Test
    public void getThemes() throws  Exception{
        ApiHelper helper = new ApiHelper();

        JSONArray result = helper.getThemes(Calendar.getInstance()).getJSONArray(ApiHelper.ENTITY_THEMES);

        String themeName1 = result.getJSONObject(0).getString(ApiHelper.THEME_NAME);
        String themeName2 = result.getJSONObject(1).getString(ApiHelper.THEME_NAME);


        assertEquals(result.length(), 2);
        assertEquals(themeName1, "Fire");
        assertEquals(themeName2, "Ice");
    }
}
