package com.antoineriche.privateinstructor.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;

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

    public static boolean isComing(Date pDate){
        return new Date().before(pDate);
    }

    public static boolean isComing(long pDateInMillis){
        return new Date().before(new Date(pDateInMillis));
    }

    public static boolean isPast(Date pDate){
        return !isComing(pDate);
    }

    public static boolean isPast(long pDateInMillis){
        return !isComing(pDateInMillis);
    }

    public static String formatRemainingTime(long pMilliseconds){
        int day = (int) (pMilliseconds / DAY);
        pMilliseconds -= day*DAY;
        int hour = (int) (pMilliseconds/HOUR);
        pMilliseconds -= hour*HOUR;
        int minute = (int) (pMilliseconds/MINUTE);
        pMilliseconds -= minute*MINUTE;
        int second = (int) (pMilliseconds/SECOND);
        String str = "";

        if(day > 0) {
            str = str.concat(String.format(Locale.FRANCE, "%dj. ", day));
        }

        if(hour > 0){
            str = str.concat(String.format(Locale.FRANCE, "%dh", hour));
        }

        if(minute > 0){
            str = str.concat(String.format(Locale.FRANCE, "%02d:", minute));
        }

        str = str.concat(String.format(Locale.FRANCE, "%02d", second));

        return str;
    }

    public static int getMonthCountBetweenDates(Date pStartDate, Date pEndDate){
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(pStartDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(pEndDate);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        return diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH) + 1;
    }

    public static int getMonthCountBetweenDates(long pStartDate, long pEndDate){
        return getMonthCountBetweenDates(new Date(pStartDate), new Date(pEndDate));
    }
}
