package com.onseen.livecare.models.Utils

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class UtilsDate {

    companion object {
        fun getDateTimeFromStringWithFormat(value: String?, format: String?, timeZone: TimeZone?): Date? {
            if(value == null || value.equals("")) return null

            val strFormat = format ?: EnumDateTimeFormat.MMddyyyy_hhmma.value

            val df: SimpleDateFormat = SimpleDateFormat( strFormat, Locale.US)
            df.timeZone = timeZone ?: TimeZone.getDefault()

            val date: Date? = df.parse(value)
            return date
        }

        fun getStringFromDateTimeWithFormat(dateTime: Date?, format: String?, timeZone: TimeZone?): String {
            if(dateTime == null) return ""

            val df:SimpleDateFormat = SimpleDateFormat(format ?: EnumDateTimeFormat.MMddyyyy_hhmma.value, Locale.US)
            df.timeZone = timeZone ?: TimeZone.getDefault()

            return df.format(dateTime)
        }

        fun getDaysBetweenTwoDates(firstDate: Date?, secondDate: Date?): Int {
            if(firstDate == null || secondDate == null) return 0

            val dateFormat: SimpleDateFormat = SimpleDateFormat(EnumDateTimeFormat.MMddyyyy.value, Locale.US)

            val today: Date = Date()
            var todayWithZeroTime: Date? = null

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today))

            var cYear: Int = 0
            var cMonth: Int = 0
            var cDay: Int = 0

            if(firstDate.after(todayWithZeroTime)) {
                val cCal: Calendar = Calendar.getInstance()
                cCal.time = firstDate
                cYear = cCal.get(Calendar.YEAR)
                cMonth = cCal.get(Calendar.MONTH)
                cDay = cCal.get(Calendar.DAY_OF_MONTH)
            } else {
                val cCal: Calendar = Calendar.getInstance()
                cCal.time = todayWithZeroTime
                cYear = cCal.get(Calendar.YEAR)
                cMonth = cCal.get(Calendar.MONTH)
                cDay = cCal.get(Calendar.DAY_OF_MONTH)
            }

            val eCal: Calendar = Calendar.getInstance()
            eCal.time = secondDate

            val eYear: Int = eCal.get(Calendar.YEAR)
            val eMonth: Int = eCal.get(Calendar.MONTH)
            val eDay: Int = eCal.get(Calendar.DAY_OF_MONTH)

            val date1: Calendar = Calendar.getInstance()
            val date2: Calendar = Calendar.getInstance()
            date1.clear()
            date1.set(cYear, cMonth, cDay)
            date2.clear()
            date2.set(eYear, eMonth, eDay)

            val diff: Long = date2.timeInMillis - date1.timeInMillis
            val dayCount: Long = diff / (24 * 60 * 60 * 1000)

            return dayCount.toString().toInt()
        }

        fun addDaysToDate(date: Date, days: Int): Date {
            val ca: Calendar = Calendar.getInstance()
            ca.time = date
            ca.add(Calendar.DATE, days)

            return ca.time
        }

        fun isSameDate(date1: Date?, date2: Date?): Boolean {
            if(date1 == null || date2 == null) return false

            val dateString1: String = getStringFromDateTimeWithFormat(date1, EnumDateTimeFormat.yyyyMMdd.value, null)
            val dateString2: String = getStringFromDateTimeWithFormat(date2, EnumDateTimeFormat.yyyyMMdd.value, null)

            return dateString1.equals(dateString2)
        }

        fun isToday(date: Date?): Boolean {
            if(date == null) return false

            val today: Date = Date()
            return isSameDate(date, today)
        }
    }
}

enum class EnumDateTimeFormat(val value: String) {

    yyyyMMdd_HHmmss_UTC("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),        // 1989-03-17T11:00:00.000Z
    yyyyMMdd_HHmmss("yyyy-MM-dd'T'HH:mm:ss"),                   // 1989-03-17T11:00:00
    yyyyMMdd("yyyy-MM-dd"),                                     // 1989-03-17
    MMddyyyy_hhmma("MM-dd-yyyy hh:mm a"),                       // 03-17-1989 02:00 AM
    MMddyyyy("MM-dd-yyyy"),                                     // 03-17-1989
    MMdd("MM/dd"),                                              // 03/17
    EEEEMMMMdyyyy("EEEE, MMMM d, yyyy"),                        // Friday, March 17, 1989
    MMMdyyyy("MMM d, yyyy"),                                    // Mar 17, 1989
    MMMMdd("MMMM dd"),                                          // March 17
    hhmma("hh:mm a"),                                           // 02:00 AM
    hhmma_MMMd("hh:mm a, MMM d")                                // 02:00 AM, Mar 17
}