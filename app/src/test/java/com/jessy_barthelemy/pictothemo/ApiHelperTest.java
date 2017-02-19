package com.jessy_barthelemy.pictothemo;

import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.security.InvalidParameterException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ApiHelperTest {

    @Test
    public void createDeleteUser() throws Exception{
        ApiHelper helper = new ApiHelper();

        JSONObject result = helper.createUser("john.doe@gmail.com", "john");
        String salt = result.getString(ApiHelper.SALT);
        assertNotNull(result);

        boolean checkDuplicate = false;
        try{
            assertNotNull(helper.createUser("john.doe@gmail.com", "john"));
        }catch(InvalidParameterException e){
            checkDuplicate = true;
        }

        assertTrue(checkDuplicate);
        checkDuplicate = false;

        try{
            assertNotNull(helper.createUser("john.doe@gmail.com", "john2"));
        }catch(InvalidParameterException e){
            checkDuplicate = true;
        }

        assertTrue(checkDuplicate);
        assertTrue(helper.deleteUser("john.doe@gmail.com", ApplicationHelper.hashPassword("john"+salt)));
    }

    @Test
    public void getAccessToken() throws Exception{
        ApiHelper helper = new ApiHelper();

        JSONObject result = helper.getAccessToken("john.always@test.fr", "johnjohn", null);
        String access_token = result.getString(ApiHelper.ACCESS_TOKEN);
        String expire_token = result.getString(ApiHelper.EXPIRES_TOKEN);
        String salt = result.getString(ApiHelper.SALT);

        assertEquals(salt, "122464189958aa180f6c1748.82167168");
        assertNotNull(access_token);
        assertNotNull(expire_token);

        result = helper.getAccessToken("john.always@test.fr", "fb1b0ce9e5e4183eeb800f73b01c6189", ApiHelper.FLAG_SALT);
        access_token = result.getString(ApiHelper.ACCESS_TOKEN);
        expire_token = result.getString(ApiHelper.EXPIRES_TOKEN);

        assertNotNull(access_token);
        assertNotNull(expire_token);
    }
}
