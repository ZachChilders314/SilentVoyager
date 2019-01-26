package com.x10host.burghporter31415.fragments;

public final class FragmentUtils {

    public static final String returnDateStamp(String[] dateStampArr) {

        int hour = Integer.parseInt(dateStampArr[3]); //Need to determine AM and PM

        return ((hour % 12) == 0 ? "12" : (hour % 12)) + ":" + dateStampArr[4] + ":" + dateStampArr[5]
                + (hour >= 12 ? " PM" : " AM") + " " + dateStampArr[1] + "/" /*If the hour is greater than OR equal to twelve, we are in the PM */
                + dateStampArr[2] + "/" + dateStampArr[0];

    }
}
