package org.smoke.sticky.tracker.utils

import org.smoke.sticky.tracker.model.Day
import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {

    companion object {
        const val millisInDay: Long = 24 * 60 * 60 * 1000

        private fun getTodayStartTime(): Long {
            val currentDate = Calendar.getInstance()
            currentDate.set(Calendar.HOUR_OF_DAY, 0)
            currentDate.set(Calendar.MINUTE, 0)
            currentDate.set(Calendar.SECOND, 0)
            return currentDate.timeInMillis
        }

        fun getCurrentTime(): Long {
            return Calendar.getInstance().timeInMillis
        }

        fun generateWeek(): List<Day> {
            val dayString = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            val list = mutableListOf<Day>()

            val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
            var startTime = getTodayStartTime() - dayOfWeek * millisInDay
            for (dayNumber in 0..7) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = startTime
                list.add(Day(startTime, dayString[calendar.get(Calendar.DAY_OF_WEEK) - 1], dayNumber == dayOfWeek, dayNumber <= dayOfWeek))
                startTime += millisInDay
            }
            return list
        }

        fun getToday(): Day {
            return Day(getTodayStartTime(), "Today", today = true)
        }

        fun getTimeString(timeMillis: Long): String =  SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.getDefault()).format(timeMillis).uppercase()
        fun getDateTimeString(timeMillis: Long): String =  SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).format(timeMillis).uppercase()
    }
}