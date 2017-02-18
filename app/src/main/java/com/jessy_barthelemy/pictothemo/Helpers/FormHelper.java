package com.jessy_barthelemy.pictothemo.Helpers;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class FormHelper {
    public boolean validateEmail(String email){
        if(email == null){
            return false;
        }
        try{
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        }catch(AddressException e){
            return false;
        }
    }

    public boolean validatePassword(String password){
        return password != null && password.length() >= ApplicationHelper.PASSWORD_MAX_LENGTH;
    }

}
