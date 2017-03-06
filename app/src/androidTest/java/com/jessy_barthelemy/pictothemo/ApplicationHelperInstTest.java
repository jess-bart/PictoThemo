package com.jessy_barthelemy.pictothemo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.jessy_barthelemy.pictothemo.Api.TokenInformations;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ApplicationHelperInstTest {
    @Test
    public void preferencesTest() throws Exception {
        // Context of the app under test.
        Context context = InstrumentationRegistry.getTargetContext();
        TokenInformations tokenInfos = new TokenInformations("access", "2017-01-11 18:32:19", "pseudo", "password", true);
        ApplicationHelper.savePreferences(context, tokenInfos);

        TokenInformations tokenInfosRestored = ApplicationHelper.getTokenInformations(context);
        assertNotNull(tokenInfosRestored);
        assertEquals(tokenInfosRestored.getAccessToken(), tokenInfos.getAccessToken());
        assertEquals(tokenInfosRestored.getExpiresToken(), tokenInfos.getExpiresToken());
        assertEquals(tokenInfosRestored.getPseudo(), tokenInfos.getPseudo());
        assertEquals(tokenInfosRestored.getPassword(), tokenInfos.getPassword());
        assertEquals(tokenInfosRestored.isPasswordSalted(), tokenInfos.isPasswordSalted());

        ApplicationHelper.resetPreferences(context);
        TokenInformations tokenReseted = ApplicationHelper.getTokenInformations(context);
        assertEquals(tokenReseted.getAccessToken(), "");
        assertEquals(tokenReseted.getExpiresToken(), null);
        assertEquals(tokenReseted.getPseudo(), "");
        assertEquals(tokenReseted.getPassword(), "");
        assertEquals(tokenReseted.isPasswordSalted(), false);
    }

    @Test
    public void pseudoTest() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        assertEquals(ApplicationHelper.handleUnknowPseudo(context, ""), "Anonyme");
        assertEquals(ApplicationHelper.handleUnknowPseudo(context, "John"), "John");
    }
}
