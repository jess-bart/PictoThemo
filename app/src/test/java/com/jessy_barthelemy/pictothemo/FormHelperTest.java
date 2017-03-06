package com.jessy_barthelemy.pictothemo;

import com.jessy_barthelemy.pictothemo.Helpers.FormHelper;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FormHelperTest {

    @Test
    public void checkPseudoTest() throws Exception {
        FormHelper formHelper = new FormHelper();
        assertFalse(formHelper.validatePseudo(null));
        assertFalse(formHelper.validatePseudo(""));
        assertFalse(formHelper.validatePseudo("siz"));
        assertFalse(formHelper.validatePseudo("stest@"));
        assertTrue(formHelper.validatePseudo("oOpseudo_51Oo"));
        assertTrue(formHelper.validatePseudo("jES858X6-"));
    }

    @Test
    public void checkPasswordTest() throws Exception {
        FormHelper formHelper = new FormHelper();
        assertFalse(formHelper.validatePassword(null));
        assertFalse(formHelper.validatePassword("aaaaa"));
        assertTrue(formHelper.validatePassword("aaaaaa"));
    }
}