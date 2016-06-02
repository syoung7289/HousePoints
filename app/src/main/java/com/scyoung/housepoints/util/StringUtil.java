package com.scyoung.housepoints.util;

/**
 * Created by scyoung on 6/2/16.
 */
public class StringUtil {
    public static String convertToTitleCase(String stringToConvert) {
        String tc = stringToConvert.toLowerCase();
        if (tc.length() > 0) {
            String[] arr = tc.split(" ");
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < arr.length; i++) {
                if (arr[i].length() > 0) {
                    sb.append(Character.toUpperCase(arr[i].charAt(0)))
                            .append(arr[i].substring(1)).append(" ");
                }
            }
            tc = sb.toString().trim();
        }
        return tc;
    }
}
