package com.example.sp25_trandangquocdat_njs1706.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    public static String convertDatetimeToDate(String datetime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        try {
            Date date = inputFormat.parse(datetime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static Date stringToDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date convertDatetimeToDate(Date date){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        try {
            Date dateResult = inputFormat.parse(date.toString());
            return dateResult;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date MinusOrPlusDate(Date date, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Subtract 30 days from the calendar
        calendar.add(Calendar.DAY_OF_MONTH, day);

        // Get the new date
        return calendar.getTime();
    }

    public static String formatDate(Date date){
        SimpleDateFormat mdyFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Format the date to Strings
        return mdyFormat.format(date);
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the timezone to UTC if needed
        return sdf.format(date);
    }
}