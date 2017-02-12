package com.ahlan.ahlanapp;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2/12/2017.
 */

/*
class with static methods of the use of the others classes
 */

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
}
