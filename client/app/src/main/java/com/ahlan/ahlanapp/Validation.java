package com.ahlan.ahlanapp;

import java.util.regex.Pattern;

public class Validation
{
    public static boolean isNameValid(String name) {
        Pattern p = Pattern.compile("[^a-zA-Z0-9|_]");
        return !p.matcher(name).find() && name.length() >= 4;
    }

    public static boolean isPasswordValid(String password) {
        Pattern p = Pattern.compile("[^a-zA-Z0-9|_]");
        return !p.matcher(password).find() && password.length() >= 4;
    }

    public static boolean isPhoneValid(String phone) {
        Pattern p = Pattern.compile("[^0-9]");
        return !p.matcher(phone).find();
    }
}
