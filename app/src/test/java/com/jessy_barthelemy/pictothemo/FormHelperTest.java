package com.jessy_barthelemy.pictothemo;

import com.jessy_barthelemy.pictothemo.Helpers.FormHelper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FormHelperTest {
    private FormHelper formHelper;

    @Before
    public void setUpBeforeClass() throws Exception {
        this.formHelper = new FormHelper();
    }

    @Test
    public void checkEmailTest() throws Exception {
        assertFalse(formHelper.validateEmail(null));
        assertFalse(formHelper.validateEmail(""));
        assertFalse(formHelper.validateEmail("test"));
        assertFalse(formHelper.validateEmail("test@"));
        assertTrue(formHelper.validateEmail("test@54654.fr"));
        assertTrue(formHelper.validateEmail("test@test.fr"));
    }

    @Test
    public void checkPasswordTest(){
        assertFalse(formHelper.validatePassword(null));
        assertFalse(formHelper.validatePassword("aaaaa"));
        assertTrue(formHelper.validatePassword("aaaaaa"));
    }
}