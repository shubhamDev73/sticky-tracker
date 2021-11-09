package org.smoke.sticky.tracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackerViewModel: ViewModel() {

    private val _stickies = MutableLiveData<Float>()
    val stickies: LiveData<Float> = _stickies

    init {
        _stickies.value = 0f
    }

    fun addSticky() {
        _stickies.value = stickies.value?.plus(1)
    }

    fun addSticky(stickies: Float) {
        _stickies.value = this.stickies.value?.plus(stickies)
    }

}