package com.bill.mygitosc.utils;

/**
 * Created by liaobb on 2015/8/3.
 */
public class StringUtils {

    public static boolean ignoreCaseContain(String s1, String s2) {
        return s1.toLowerCase().indexOf(s2.toLowerCase()) >= 0;
    }
}
