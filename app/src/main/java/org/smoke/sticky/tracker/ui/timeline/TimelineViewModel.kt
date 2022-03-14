package org.smoke.sticky.tracker.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.utils.TimeUtils

class TimelineViewModel: ViewModel() {
    private val _scale = MutableLiveData<Float>()
    var scaleData: LiveData<Float> = _scale
    private val _position = MutableLiveData<Float>()
    var positionData: LiveData<Float> = _position

    var scale
        get() = scaleData.value ?: 1f
        set(value) { _scale.value = value }

    var position
        get() = positionData.value ?: 0f
        set(value) { _position.value = value }

    var height = 0f
    var startTime = 0L

    fun getPosition(sticky: Sticky): Float {
        return getPosition(sticky.timeMillis)
    }

    fun getPosition(timeMillis: Long): Float {
        return position + (timeMillis - startTime) * height / TimeUtils.millisInDay
    }

}
