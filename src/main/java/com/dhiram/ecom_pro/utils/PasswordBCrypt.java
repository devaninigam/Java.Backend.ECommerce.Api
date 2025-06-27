package com.dhiram.ecom_pro.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordBCrypt {

    public static String hashPassword(String password) {
        String newPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return newPassword;
    }

    public boolean matchesPassword(String planPassword, String password) {
        boolean newPassword = BCrypt.checkpw(planPassword, password);
        return newPassword;
    }
}
