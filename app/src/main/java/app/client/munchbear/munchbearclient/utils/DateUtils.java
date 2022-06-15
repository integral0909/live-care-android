package app.client.munchbear.munchbearclient.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    private static SimpleDateFormat formatddMMMMyyyy = new SimpleDateFormat("dd MMMM yyyy");
    private static SimpleDateFormat formatHhMmUSA = new SimpleDateFormat("hh:mm a");
    private static SimpleDateFormat formatHHMM = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat formatReservation = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat formatOrder = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat formatReservationDate = new SimpleDateFormat("MMMM dd,yyyy \n hh:mm a");
    private static SimpleDateFormat formatMMMMddyyyy = new SimpleDateFormat("MMMM dd, yyyy");
    private static SimpleDateFormat formatddMMMMyyyyHHmm = new SimpleDateFormat("dd MMMM yyyy HH:mm");
    private static SimpleDateFormat formatddMMMMHHmm = new SimpleDateFormat("dd MMMM HH:mm");

    public static String getDateMMMMDYYYY(int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return formatddMMMMyyyy.format(calendar.getTime());
    }

    public static long getTimeHHmm(int hour, int minute) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }

    public static String getTimeHHmma(int hour, int minute) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return formatHhMmUSA.format(calendar.getTime());
    }

    public static String getDateMMMMDYYYY(long value) {
        return formatddMMMMyyyy.format(new Date(value));
    }

    public static String getTimeHHmma(long value) {
        return formatHhMmUSA.format(new Date(value));
    }

    public static String convert24toUSAFormat(String usaString) {
        String format = "";
        try {
            format = formatHhMmUSA.format(formatHHMM.parse(usaString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return format;
    }

    public static String getReservationFormat(Calendar calendar) {
        if (calendar != null) {
            formatReservation.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatReservation.format(calendar.getTime());
        } else {
            return "";
        }
    }

    public static String getDateMMMMddyyyyhhmma(long timestamp) {
        return formatReservationDate.format(new Date(timestamp * 1000L));
    }

    public static String getDateMMMMddyyyy(long timestamp) {
        formatMMMMddyyyy.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatMMMMddyyyy.format(new Date(timestamp * 1000L));
    }

    public static String getOrderTimeHHmma(long timestamp) {
        return formatHhMmUSA.format(new Date(timestamp * 1000L));
    }

    public static String getOrderTimeFormat(long selectedTimestamp) {
        return formatOrder.format(new Date(selectedTimestamp));
    }

    public static String convertServerOrderTimeToHHmm(String serverTime) {
        String format = "";
        try {
            format = formatHhMmUSA.format(formatOrder.parse(serverTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return format;
    }

    public static String convertServerOrderTimeToHHmm(long serverTime) {
        return formatHhMmUSA.format(new Date(serverTime * 1000L));
    }

    /**
     * @param placedTime of created order
     * @return String in some format depending on date of placedTime
     */
    public static String getOrderPlacedTime(long placedTime) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(placedTime * 1000L);
        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today " + formatHhMmUSA.format(new Date(placedTime * 1000L));
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ) {
            return "Yesterday " + formatHhMmUSA.format(new Date(placedTime * 1000L));
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return formatddMMMMHHmm.format(new Date(placedTime * 1000L));
        } else {
            return formatddMMMMyyyyHHmm.format(new Date(placedTime * 1000L));
        }
    }
}
