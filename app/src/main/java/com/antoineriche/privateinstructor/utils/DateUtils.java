package com.antoineriche.privateinstructor.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date getFirstSecond(Date pDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getLastSecond(Date pDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static boolean isToday(Date pDate){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(pDate);
        return c2.get(Calendar.DAY_OF_YEAR) == c1.get(Calendar.DAY_OF_YEAR)
                && c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR);
    }
}
