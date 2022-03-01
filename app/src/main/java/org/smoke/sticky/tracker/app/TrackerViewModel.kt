package org.smoke.sticky.tracker.app

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.smoke.sticky.tracker.TimeUtils
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.model.StickyDao

class TrackerViewModel(private val stickyDao: StickyDao): ViewModel() {

    suspend fun recentStickies(startTime: Long): Flow<List<Sticky>> = withContext(Dispatchers.IO) {
        val time = if (startTime == 0L) {
            TimeUtils.getTodayStartTime()
        } else {
            startTime
        }
        stickyDao.getRecentItems(time, time + TimeUtils.millisInDay)
    }

    private val _count = MutableLiveData<Float>()
    val count: LiveData<Float> = _count

    init {
        _count.value = 0f
        viewModelScope.launch {
            recentStickies(0).collect {
                var stickies = 0f
                it.forEach { sticky -> stickies += sticky.amount }
                _count.value = stickies
            }
        }
    }

    fun addSticky() {
        addSticky(1f)
    }

    fun addSticky(amount: Float) {
        viewModelScope.launch {
            val sticky = Sticky(amount = amount, timeMillis = System.currentTimeMillis())
            stickyDao.insert(sticky)
            _count.value = _count.value?.plus(amount)
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