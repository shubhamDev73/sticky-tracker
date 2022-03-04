package org.smoke.sticky.tracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ZoomViewModel: ViewModel() {
    private val _scale = MutableLiveData(1f)
    var scaleData: LiveData<Float> = _scale
    private val _position = MutableLiveData(0f)
    var positionData: LiveData<Float> = _position

    var scale
        get() = scaleData.value ?: 1f
        set(value) { _scale.value = value }

    var position
        get() = positionData.value ?: 0f
        set(value) { _position.value = value }

}
