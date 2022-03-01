package org.smoke.sticky.tracker.model

import java.util.*

data class Day(val startTime: Long? = null, val label: String)

private const val millisInDay: Long = 24 * 60 * 60 * 1000

private fun getTodayStartTime(): Long {
    val currentDate = Calendar.getInstance()
    val currentTime = Date().time
    return currentTime - (
            currentDate.get(Calendar.HOUR) * 3600
            + currentDate.get(Calendar.MINUTE) * 60
            + currentDate.get(Calendar.SECOND)
        ) * 1000
}

fun generateWeek(): List<Day> {
    val dayString = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val list = mutableListOf<Day>()

    val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    var startTime = getTodayStartTime() - dayOfWeek * millisInDay
    for (day in 0..7) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTime
        val time = if (day <= dayOfWeek) startTime else null
        list.add(Day(time, dayString[calendar.get(Calendar.DAY_OF_WEEK) - 1]))
        startTime += millisInDay
    }
    return list
}

fun today(): Day {
    return Day(getTodayStartTime(), "Today")
}