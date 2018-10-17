package com.antoineriche.privateinstructor.utils;

public class StringUtils {

    public static String capitalizeFirstChar(String pString){
        return pString.substring(0, 1).toUpperCase().concat(pString.substring(1));
    }
}
