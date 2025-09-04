package com.example.validator;

import java.util.regex.Pattern;

public class Validator {


    private static final Pattern STR_PATTERN = Pattern.compile("^[A-Za-z]+\\s[A-Za-z]+$");
    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");

    public static boolean validateEmployee(String[] arr) {
        if (arr == null || arr.length != 5) {return false;}
        if (!"Employee".equals(arr[0])) {return false;}
        if (!isPositive(arr[1])) return false;
        if (!STR_PATTERN.matcher(arr[2]).matches()) return false;
        if (!isPositive(arr[3])) return false;
        return isPositive(arr[4]);
    }
    public static boolean validateManager(String[] arr) {
        if (arr == null || arr.length != 5) {return false;}
        if (!"Manager".equals(arr[0])) {return false;}
        if (!isPositive(arr[1])) return false;
        if (!STR_PATTERN.matcher(arr[2]).matches()) return false;
        if (!isPositive(arr[3])) return false;
        return arr[4] != null && !arr[4].isBlank();
    }

    private static boolean isPositive(String str) {
        if (!INT_PATTERN.matcher(str).matches()) {
            return false;
        }
        try {
            return Integer.parseInt(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
