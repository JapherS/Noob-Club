package edu.neu.madcourse.noobclub.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss", Locale.ENGLISH);
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

    public static String formatDate(long timeStamp)
    {
        Date date = new Date();
        date.setTime(timeStamp);
        return dateFormat.format(date);
    }

    public static String formatOnlyDate(long timeStamp)
    {
        Date date = new Date();
        date.setTime(timeStamp);
        return DATE_FORMAT.format(date);
    }

    public static String formPostCreateTime(long timeStamp)
    {
        long current = System.currentTimeMillis();
        long distance = current - timeStamp;
        long seconds = distance / 1000 ;
        if(seconds < 60){
            return seconds +" sec";
        }
        long minutes = seconds / 60;
        if(minutes < 60) {
            return minutes +" min";
        }
        long hour = minutes / 60;
        if(hour < 24){
            return hour +" hr";
        }
        long day = hour / 24 ;
        if(day < 30){
            return day +" day";
        }
        long month = day / 30 ;
        if(month < 12){
            return month +" month";
        }
        long year = month / 12;
        return year +" year";
    }
}
