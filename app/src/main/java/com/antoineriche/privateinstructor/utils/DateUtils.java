package com.antoineriche.privateinstructor.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    public static final long DAY = HOUR * 24;


    public static String getShortDate(Date pDate){
        return StringUtils.capitalizeFirstChar(new SimpleDateFormat("dd/MM/yy", Locale.FRANCE).format(pDate));
    }
    public static String getShortDate(long pDateInMillis){
        return getShortDate(new Date(pDateInMillis));
    }

    public static String getFriendlyDate(Date pDate){
        return StringUtils.capitalizeFirstChar(new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE).format(pDate));
    }

    public static String getFriendlyDate(long pDateInMillis){
        return getFriendlyDate(new Date(pDateInMillis));
    }

    public static String getFriendlyHour(long pDateInMillis){
        return getFriendlyHour(new Date(pDateInMillis));
    }

    public static String getFriendlyHour(Date pDate){
        return new SimpleDateFormat("HH'h'mm", Locale.FRANCE).format(pDate);
    }

    public static Date getFirstSecondOfTheDay(Date pDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getLastSecondOfTheDay(Date pDate){
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
