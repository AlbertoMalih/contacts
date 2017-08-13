package com.example.administrator1.contacts;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

    public static boolean checkValidNumber(String number){
        return NUMBER_PATTERN.matcher(number).matches();
    }

    public static boolean checkValidEmail(String number){
        return number.matches("[\\w\\W]+\\@[\\w\\W]{4,}");
    }

    public static CharSequence getStringResourceByName(Context c, String aString) {
        int resId = c.getResources().getIdentifier(aString, "string", c.getPackageName());
        return c.getString(resId);
    }
}
