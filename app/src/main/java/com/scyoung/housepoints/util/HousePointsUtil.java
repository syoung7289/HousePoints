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
    public static SimpleDateFormat formatter;

    public static int getActiveHousePoints(SharedPreferences prefs) {
        int housePoints = 0;
        SharedPreferences.Editor editor = prefs.edit();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String[] varArray = entry.getKey().split("~");
            if ("ACTIVE".equals(varArray[0])) {
                if (isWithinActiveDateRange(varArray[2], (String)entry.getValue(), prefs)) {
                    housePoints += Integer.parseInt(prefs.getString((String) entry.getValue(), "1"));
                    Log.d("pts add", entry.getKey() + ": " + entry.getValue().toString());
                }
                else {
                    editor.remove(entry.getKey());
                    editor.putString("INACTIVE~" +
                            varArray[1] + "~" +
                            varArray[2] + "~" +
                            varArray[3], (String)entry.getValue());
                }
            }
        }
        editor.commit();
        return housePoints;
    }
    private static boolean isWithinActiveDateRange(String createDateString,
                                            String infraction,
                                            SharedPreferences prefs) {
        boolean isWithin;
        try {
            Date createDate = getFormatter().parse(createDateString);
            int duration = Integer.parseInt(prefs.getString(infraction + "_duration", "7"));
//            Calendar cal = GregorianCalendar.getInstance();
//            cal.add(Calendar.DAY_OF_YEAR, -(duration));
            Calendar cal = GregorianCalendar.getInstance();
            cal.add(Calendar.MINUTE, -(duration));
            Date todayMinusDuration = cal.getTime();
            isWithin = createDate.after(todayMinusDuration);
        }
        catch (Exception e) {
            isWithin = false;
        }
        return isWithin;
    }

    public static SimpleDateFormat getFormatter() {
        if (formatter == null) {
            formatter  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        }
        return formatter;
    }
}
