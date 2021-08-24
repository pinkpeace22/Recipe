package kr.ac.hs.recipe;

import java.text.SimpleDateFormat;

public class DateUtils {
    public static String getConvertTime(long timeMils) {
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        return date.format(timeMils);
    }
}
