package org.smoke.sticky.tracker.sticky

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.smoke.sticky.tracker.utils.TimeUtils
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.model.StickyDao
import org.smoke.sticky.tracker.model.Tag
import org.smoke.sticky.tracker.utils.PreferenceUtils

class StickyViewModel(private val stickyDao: StickyDao): ViewModel() {

    private val _allStickies = MutableLiveData<List<Sticky>>()

    // filtered stickies
    private val _stickies = MutableLiveData<List<Sticky>>()
    val stickies: LiveData<List<Sticky>> = _stickies

    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> = _tags

    init {
        _tags.value = PreferenceUtils.getTags()
    }

    fun updateDay(day: Day) {
        viewModelScope.launch(Dispatchers.IO) {
            stickyDao.getRecentItems(day.startTime, day.startTime + TimeUtils.millisInDay).collect {
                _allStickies.postValue(it)
                filterStickies(it)
            }
        }
    }

    fun updateTag(tag: Tag, checked: Boolean) {
        if (checked) {
            addTag(tag)
        } else {
            removeTag(tag)
        }
        PreferenceUtils.setTags(getTags())
        filterStickies()
    }

    private fun addTag(tag: Tag) {
        val tags = getTags()
        if (!tags.contains(tag)) {
            _tags.value = tags.plus(tag)
        }
    }

    private fun removeTag(tag: Tag) {
        val tags = getTags()
        if (tags.contains(tag)) {
            _tags.value = tags.minus(tag)
        }
    }

    fun getTags(): List<Tag> {
        return _tags.value ?: listOf()
    }

    private fun filterStickies(stickies: List<Sticky>) {
        val tags = getTags()
        _stickies.postValue(stickies.filter { sticky ->
            tags.contains(sticky.tag)
        })
    }

    private fun filterStickies() {
        filterStickies(_allStickies.value ?: listOf())
    }

    fun recentCount(day: Day): LiveData<Float> {
        val count = MutableLiveData(0f)
        viewModelScope.launch(Dispatchers.IO) {
            stickyDao.getRecentItems(day.startTime, day.startTime + TimeUtils.millisInDay).collect { stickies ->
                val tags = getTags()
                val filteredStickies = stickies.filter { sticky ->
                    tags.contains(sticky.tag)
                }
                count.postValue(filteredStickies.sumOf { it.amount.toDouble() }.toFloat())
            }
        }
        return count
    }

    fun addSticky(tag: Tag) {
        addSticky(1f, tag)
    }

    fun addSticky(amount: Float, tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            val sticky = Sticky(amount = amount, timeMillis = System.currentTimeMillis(), tag = tag)
            stickyDao.insert(sticky)
        }
    }

    fun delete(sticky: Sticky) {
        viewModelScope.launch(Dispatchers.IO) {
            stickyDao.delete(sticky)
        }
    }

    fun edit(sticky: Sticky) {
        viewModelScope.launch(Dispatchers.IO) {
            stickyDao.update(sticky)
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