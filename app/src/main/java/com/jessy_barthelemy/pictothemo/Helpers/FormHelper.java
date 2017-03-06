package com.jessy_barthelemy.pictothemo.Helpers;

import java.util.regex.Pattern;

public class FormHelper {
    public boolean validatePseudo(String pseudo){
        if(pseudo == null){
            return false;
        }
        Pattern p = Pattern.compile("^[a-zA-Z0-9-_]{"+ApplicationHelper.PSEUDO_MAX_LENGTH+",}$");
        return p.matcher(pseudo).matches();
    }

    public boolean validatePassword(String password){
        return password != null && password.length() >= ApplicationHelper.PASSWORD_MAX_LENGTH;
    }

}
