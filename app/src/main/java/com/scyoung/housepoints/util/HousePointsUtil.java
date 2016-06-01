package com.scyoung.housepoints.util;

import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by scyoung on 5/29/16.
 */
public class HousePointsUtil {
    private static SimpleDateFormat formatter;
    public static int nextPointCount = 0;
    public static long nextPointHour = 9999999999l;
    public static long nextPointMinute = 99999999999l;

    public static int getActiveHousePoints(SharedPreferences prefs) {
        int housePoints = 0;
        nextPointCount = 0;
        nextPointHour = 9999999999l;
        nextPointMinute = 99999999999l;
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String[] varArray = entry.getKey().split("~");
            if ("ACTIVE".equals(varArray[0])) {
                if (isWithinActiveDateRange(varArray[1], (String)entry.getValue(), prefs)) {
                    housePoints += Integer.parseInt(prefs.getString((String) entry.getValue(), "1"));
                    Log.d("pts add", entry.getKey() + ": " + entry.getValue().toString());
                }
                else {
                    changePointStatus(entry.getKey(), (String)entry.getValue(), "INACTIVE", prefs);
                }
            }
        }
        return housePoints;
    }

    private static boolean isWithinActiveDateRange(String createDateString,
                                            String infraction,
                                            SharedPreferences prefs) {
        boolean isWithin;
        try {
            Date createDate = getFormatter().parse(createDateString);
            int cost = Integer.parseInt(prefs.getString(infraction, "1"));
            Date todayMinusDuration = determineDateByInfraction(null, infraction, prefs, false);
            isWithin = createDate.after(todayMinusDuration);
            if (isWithin) {
                findNextPointToExpire(createDate, todayMinusDuration, cost);
            }
        }
        catch (Exception e) {
            isWithin = false;
        }
        return isWithin;
    }

    private static void findNextPointToExpire(Date createDate, Date expireDate, int pointCost) {
        long expireMinutes = createDate.getTime() - expireDate.getTime();
        long hour = 1000 * 60 * 60;
        long minute = 1000 * 60;
        long thisPointHour = expireMinutes / hour;
        long thisPointMinute = (expireMinutes % hour) / minute;
        if (thisPointHour <= nextPointHour && thisPointMinute < nextPointMinute) {
            nextPointCount = pointCost;
            nextPointHour = thisPointHour;
            nextPointMinute = thisPointMinute;
        }
        else if (thisPointHour == nextPointHour && thisPointMinute == nextPointMinute) {
            nextPointCount += pointCost;
        }
    }

    public static void changePointStatus(String key, String value, String status, SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        String[] varArray = key.split("~");
        editor.remove(key);
        editor.putString(status + "~" +
                         varArray[1] + "~" +
                         varArray[2] + "~" +
                         varArray[3], value);
        editor.commit();
    }

    public static Date determineDateByInfraction(Date compareDate, String infractionType,
                                                 SharedPreferences prefs, boolean addDays) {
        int duration = Integer.parseInt(prefs.getString(infractionType + "_duration", "7"));
//            Calendar cal = GregorianCalendar.getInstance();
//            cal.add(Calendar.DAY_OF_YEAR, -(duration));
        Calendar cal = GregorianCalendar.getInstance();
        if (compareDate != null) {
            cal.setTime(compareDate);
        }
        if (addDays) {
            cal.add(Calendar.MINUTE, (duration));
        }
        else {
            cal.add(Calendar.MINUTE, -(duration));
        }
        return cal.getTime();
    }

    public static SimpleDateFormat getFormatter() {
        if (formatter == null) {
            formatter  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        }
        return formatter;
    }

    public static Date parseDateString(String dateString) {
        Date date;
        try {
            date = getFormatter().parse(dateString);
        }
        catch (Exception e) {
            date = null;
        }
        return date;
    }

}
