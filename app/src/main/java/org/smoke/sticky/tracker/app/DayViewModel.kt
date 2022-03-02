package org.smoke.sticky.tracker.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.smoke.sticky.tracker.TimeUtils
import org.smoke.sticky.tracker.model.Day

class DayViewModel: ViewModel() {

    private val _currentDay = MutableLiveData(TimeUtils.getToday())
    val currentDay: LiveData<Day> = _currentDay

    fun setCurrentDay(day: Day) {
        _currentDay.value = day
    }

    fun getWeek(): List<Day> {
        return TimeUtils.generateWeek()
    }

}