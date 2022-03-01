package org.smoke.sticky.tracker.app

import androidx.lifecycle.ViewModel
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.generateWeek

class DayViewModel: ViewModel() {

    fun getWeek(): List<Day> {
        return generateWeek()
    }

}