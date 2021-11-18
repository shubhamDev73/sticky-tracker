package org.smoke.sticky.tracker

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TrackerViewModel(private val stickyDao: StickyDao): ViewModel() {

    private val _stickies = MutableLiveData<List<Sticky>>()
    val stickies: LiveData<List<Sticky>> = _stickies

    private val _count = MutableLiveData<Float>()
    val count: LiveData<Float> = _count

    init {
        _stickies.value = emptyList()
        _count.value = 0f
    }

    fun addSticky() {
        addSticky(1f)
    }

    fun addSticky(amount: Float) {
        viewModelScope.launch {
            val sticky = Sticky(amount = amount, timeMillis = System.currentTimeMillis())
            stickyDao.insert(sticky)
            _stickies.value = _stickies.value?.plus(sticky)
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