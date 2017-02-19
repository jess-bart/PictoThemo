package com.jessy_barthelemy.pictothemo;

import com.jessy_barthelemy.pictothemo.Helpers.FormHelper;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FormHelperTest {

    @Test
    public void checkEmailTest() throws Exception {
        FormHelper formHelper = new FormHelper();
        assertFalse(formHelper.validateEmail(null));
        assertFalse(formHelper.validateEmail(""));
        assertFalse(formHelper.validateEmail("test"));
        assertFalse(formHelper.validateEmail("test@"));
        assertTrue(formHelper.validateEmail("test@54654.fr"));
        assertTrue(formHelper.validateEmail("test@test.fr"));
    }

    @Test
    public void checkPasswordTest() throws Exception {
        FormHelper formHelper = new FormHelper();
        assertFalse(formHelper.validatePassword(null));
        assertFalse(formHelper.validatePassword("aaaaa"));
        assertTrue(formHelper.validatePassword("aaaaaa"));
    }
}