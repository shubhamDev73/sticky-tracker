package org.smoke.sticky.tracker.app

import androidx.lifecycle.ViewModel
import org.smoke.sticky.tracker.TimeUtils
import org.smoke.sticky.tracker.model.Day

class DayViewModel: ViewModel() {

    fun getWeek(): List<Day> {
        return TimeUtils.generateWeek()
    }

}