package com.jessy_barthelemy.pictothemo;

import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationHelperTest {
    @Test
    public void hashPasswordTest() throws Exception {
        assertEquals(ApplicationHelper.hashPassword("test"), "098f6bcd4621d373cade4e832627b4f6");
        assertEquals(ApplicationHelper.hashPassword(null), "");
        assertEquals(ApplicationHelper.hashPassword(""), "");
    }
}