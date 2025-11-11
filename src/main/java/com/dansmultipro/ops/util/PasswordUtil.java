package com.dansmultipro.ops.util;

import java.security.SecureRandom;

public class PasswordUtil {
    public static String generateRandomPassword() {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String allChars = uppercase + lowercase + digits;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 10 random characters
        for (int i = 0; i < 10; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        return password.toString();
    }
}
