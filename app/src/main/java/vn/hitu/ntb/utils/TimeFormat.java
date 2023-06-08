package vn.hitu.ntb.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import vn.hitu.ntb.R;

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 5/12/22
 */
public class TimeFormat {
    @SuppressLint("DefaultLocale")
    public static String timeAgoString(Context context, String date) {
        Date past = getDateFromString(date);
        Date now = new Date();
        try {
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
            long mouth = days / 30;
            if (seconds < 60) {
                return context.getString(R.string.finish_now);
            } else if (minutes < 60) {
                return String.format(context.getString(R.string.minute_ago), minutes);
            } else if (hours < 24) {
                return String.format(context.getString(R.string.hour_ago), hours);
            } else if (days < 30) {
                return String.format(context.getString(R.string.day_ago), days);
            } else if (days > 30 && mouth < 12) {
                return String.format(context.getString(R.string.month_ago), mouth);
            } else {
                return String.format("%s", date);
            }
        } catch (Exception j) {
            j.printStackTrace();
        }
        return String.format("%s", date);
    }

    public static Date getDateFromString(String str_date) {
        final String OLD_FORMAT = "MM-dd-yyyy HH:mm:ss";
        final String NEW_FORMAT = "HH:mm dd-MM-yyyy";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat(OLD_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = df.parse(str_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(Objects.requireNonNull(date));

        String newDateString = "";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(formattedDate);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(Objects.requireNonNull(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        Date dateData = null;
        try {
            dateData = dateFormat.parse(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateData;
    }

    public static String changeFormatTimeMessageChat(String strDate) {
        final String OLD_FORMAT = "MM-dd-yyyy HH:mm:ss";
        final String NEW_FORMAT = "dd/MM/yyyy HH:mm:ss";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat(OLD_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = df.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(Objects.requireNonNull(date));

        String newDateString = "";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(formattedDate);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(Objects.requireNonNull(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateString;
    }

    public static Boolean checkTimeIn24HourPhoneBook(String start_date,
                                                     String end_date) {
        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());

        // Try Class
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            //Calculate time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            //Calculate time difference in seconds,
            // minutes, hours, years, and days
            long difference_In_Seconds
                    = TimeUnit.MILLISECONDS
                    .toSeconds(difference_In_Time)
                    % 60;

            long difference_In_Minutes
                    = TimeUnit
                    .MILLISECONDS
                    .toMinutes(difference_In_Time)
                    % 60;

            long difference_In_Hours
                    = TimeUnit
                    .MILLISECONDS
                    .toHours(difference_In_Time)
                    % 24;

            long difference_In_Days
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    % 365;

            long difference_In_Years
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    / 365L;

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds
            System.out.print(
                    "Difference"
                            + " between two dates is: ");

            // Print result
            System.out.println(
                    difference_In_Years
                            + " years, "
                            + difference_In_Days
                            + " days, "
                            + difference_In_Hours
                            + " hours, "
                            + difference_In_Minutes
                            + " minutes, "
                            + difference_In_Seconds
                            + " seconds");

            return difference_In_Days == 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getCurrentTimeFormat(){
        String dateFormatTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        return String.format("%s %s", dateFormatTime, dateFormat);
    }
}
