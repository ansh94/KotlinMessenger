package com.anshdeep.kotlinmessenger.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ansh on 13/10/18.
 */
object DateUtils {

    val fullFormattedTime = SimpleDateFormat("d MMM, h:mm a", Locale.US) // the format of your date
    val onlyTime = SimpleDateFormat("h:mm a", Locale.US) // the format of your date
    val onlyDate = SimpleDateFormat("d MMM", Locale.US) // the format of your date

    fun getFormattedTime(timeInMilis: Long): String {
        val date = Date(timeInMilis * 1000L) // *1000 is to convert seconds to milliseconds

        return when {
            isToday(date) -> onlyTime.format(date)
            isYesterday(date) -> "Yesterday"
            else -> onlyDate.format(date)
        }

    }

    fun getFormattedTimeChatLog(timeInMilis: Long): String {
        val date = Date(timeInMilis * 1000L) // *1000 is to convert seconds to milliseconds
        val fullFormattedTime = SimpleDateFormat("d MMM, h:mm a", Locale.US) // the format of your date
        val onlyTime = SimpleDateFormat("h:mm a", Locale.US) // the format of your date

        return when {
            isToday(date) -> onlyTime.format(date)
            else -> fullFormattedTime.format(date)
        }

    }

    fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    fun isToday(d: Date): Boolean {
        return DateUtils.isToday(d.time)
    }
}