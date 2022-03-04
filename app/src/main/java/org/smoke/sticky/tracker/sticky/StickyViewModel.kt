package org.smoke.sticky.tracker.sticky

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.smoke.sticky.tracker.utils.TimeUtils
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.model.StickyDao

class StickyViewModel(private val stickyDao: StickyDao): ViewModel() {

    fun recentStickies(day: Day): LiveData<List<Sticky>>{
        val stickies = MutableLiveData<List<Sticky>>()
        viewModelScope.launch(Dispatchers.IO) {
            stickyDao.getRecentItems(day.startTime, day.startTime + TimeUtils.millisInDay).collect {
                stickies.postValue(it)
            }
        }
        return stickies
    }

    fun recentCount(day: Day): LiveData<Float> {
        val count = MutableLiveData(0f)
        viewModelScope.launch(Dispatchers.IO) {
            stickyDao.getRecentCount(day.startTime, day.startTime + TimeUtils.millisInDay).collect {
                count.postValue(it ?: 0f)
            }
        }
        return count
    }

    fun addSticky() {
        addSticky(1f)
    }

    fun addSticky(amount: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val sticky = Sticky(amount = amount, timeMillis = System.currentTimeMillis())
            stickyDao.insert(sticky)
        }
    }

    fun delete(sticky: Sticky) {
        viewModelScope.launch(Dispatchers.IO) {
            stickyDao.delete(sticky)
        }
    }

}

class StickyViewModelFactory(private val stickyDao: StickyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StickyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StickyViewModel(stickyDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}