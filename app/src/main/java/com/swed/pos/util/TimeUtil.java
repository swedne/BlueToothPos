package com.swed.pos.util;

/**
 * Created by Administrator on 2018/2/12.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static final String DATA_FORMAT = "HH:mm";
    public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public static boolean afterCurrent(String paramString)
            throws ParseException {
        return afterCurrent(string2Data(paramString));
    }

    public static boolean afterCurrent(Date paramDate)
            throws ParseException {
        return paramDate.after(string2Data(sdf.format(new Date())));
    }

    public static boolean beforeCurrent(String paramString)
            throws ParseException {
        return beforeCurrent(string2Data(paramString));
    }

    public static boolean beforeCurrent(Date paramDate)
            throws ParseException {
        return paramDate.before(string2Data(sdf.format(new Date())));
    }

    public static boolean isCurrentTimeAcrossIntervals(String paramString1, String paramString2)
            throws ParseException {
        Object localObject = new Date();
        localObject = sdf.format((Date) localObject);
        localObject = sdf.parse((String) localObject);
        Date date1 = string2Data(paramString1);
        Date date2 = string2Data(paramString2);
        if (date2.before(date1)) {
            if (((Date) localObject).before(date2)) {
                ((Date) localObject).setTime(((Date) localObject).getTime() + 86400000L);
            }
            date2.setTime(date2.getTime() + 86400000L);
        }
        System.out.println(localObject + "----" + ((Date) localObject).before(date2) + "------" + ((Date) localObject).after(date1));
        return (((Date) localObject).before(date2)) && (((Date) localObject).after(date1));
    }

    public static Date string2Data(String paramString)
            throws ParseException {
        try {
            Date localDate = sdf.parse(paramString);
            return localDate;
        } catch (ParseException localParseException) {
            paramString = paramString + ":00";
            Date date = sdf.parse(paramString);
            localParseException.printStackTrace();
            return date;
        }
    }
}

