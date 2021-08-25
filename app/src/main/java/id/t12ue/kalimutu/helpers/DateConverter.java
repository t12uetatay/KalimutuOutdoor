package id.t12ue.kalimutu.helpers;

import android.text.format.DateFormat;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }


    public static String getTanggal(Date date) {
        String dateTime="-";
        if (date!=null){
            String day = (String) DateFormat.format("dd", date); // 20
            String monthString = (String) DateFormat.format("MMM", date); // Jun
            String yearString = (String) DateFormat.format("yyyy", date);
            String hourString = (String) DateFormat.format("HH", date);
            String minuteString = (String) DateFormat.format("mm", date);
            String seconString = (String) DateFormat.format("ss", date);
            dateTime = day+" "+monthString+" "+yearString+" "+hourString+":"+minuteString+":"+seconString;
        }
        return dateTime;
    }

    public static String getDateNotif(Date date) {
        String dateTime="-";
        if (date!=null){
            String day = (String) DateFormat.format("dd", date); // 20
            String monthString = (String) DateFormat.format("MMM", date); // Jun
            String yearString = (String) DateFormat.format("yy", date);
            String hourString = (String) DateFormat.format("HH", date);
            String minuteString = (String) DateFormat.format("mm", date);
            String seconString = (String) DateFormat.format("ss", date);
            dateTime = day+" "+monthString+" "+yearString+"\n"+hourString+":"+minuteString+":"+seconString;
        }
        return dateTime;
    }

    public static String getDateFormatID(Date date) {
        String dateTime="-";
        if (date!=null){
            String day = (String) DateFormat.format("dd", date); // 20
            String monthString = (String) DateFormat.format("MMM", date); // Jun
            String yearString = (String) DateFormat.format("yyyy", date);
            dateTime = day+"-"+monthString+"-"+yearString;
        }
        return dateTime;
    }

    public static String getDateFormat(Date date) {
        String dateTime="-";
        if (date!=null){
            String day = (String) DateFormat.format("dd", date); // 20
            String monthString = (String) DateFormat.format("MM", date); // Jun
            String yearString = (String) DateFormat.format("yyyy", date);
            dateTime = yearString+"-"+monthString+"-"+day;
        }
        return dateTime;
    }

    public static String getDayMonth(Date date) {
        String dateTime="-";
        if (date!=null){
            String day = (String) DateFormat.format("dd", date); // 20
            String monthString = (String) DateFormat.format("MMM", date); // Jun
            dateTime = day+"\n"+monthString;
        }
        return dateTime;
    }
}
