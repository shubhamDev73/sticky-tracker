package org.smoke.sticky.tracker.app

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.smoke.sticky.tracker.TimeUtils
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.model.StickyDao

class TrackerViewModel(private val stickyDao: StickyDao): ViewModel() {

    suspend fun recentStickies(day: Day): Flow<List<Sticky>> = withContext(Dispatchers.IO) {
        stickyDao.getRecentItems(day.startTime, day.startTime + TimeUtils.millisInDay)
    }

    suspend fun recentCount(day: Day): Flow<Float?> = withContext(Dispatchers.IO) {
        stickyDao.getRecentCount(day.startTime, day.startTime + TimeUtils.millisInDay)
    }

    fun recent(day: Day): LiveData<Float> {
        val count = MutableLiveData(0f)
        viewModelScope.launch {
            recentCount(day).collect {
                count.value = it ?: 0f
            }
        }
        return count
    }

    fun addSticky() {
        addSticky(1f)
    }

    fun addSticky(amount: Float) {
        viewModelScope.launch {
            val sticky = Sticky(amount = amount, timeMillis = System.currentTimeMillis())
            stickyDao.insert(sticky)
        }
    }

}

class TrackerViewModelFactory(private val stickyDao: StickyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(stickyDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}