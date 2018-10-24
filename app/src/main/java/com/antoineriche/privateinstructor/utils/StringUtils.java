package com.antoineriche.privateinstructor.utils;

import java.util.Locale;

public class StringUtils {

    public static String capitalizeFirstChar(String pString){
        return pString.substring(0, 1).toUpperCase().concat(pString.substring(1));
    }

    public static String formatDouble(double pDouble){
        return String.format(Locale.FRANCE, "%.2f", pDouble).replace(",", ".");
    }
}
