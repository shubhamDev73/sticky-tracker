package org.smoke.sticky.tracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackerViewModel: ViewModel() {

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

    fun addSticky(value: Float) {
        _stickies.value = _stickies.value?.plus(Sticky(value, System.currentTimeMillis()))
        _count.value = _count.value?.plus(value)
    }

}