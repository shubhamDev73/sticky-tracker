package org.smoke.sticky.tracker.ui.timeline

import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.RelativeLayout
import androidx.core.view.doOnAttach
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import org.smoke.sticky.tracker.databinding.StickyDetailsElementBinding
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.sticky.StickyOptionsListener
import org.smoke.sticky.tracker.utils.TimeUtils

class TimelineZoomView(
    context: Context,
    private val day: Day,
    private val stickyOptionsListener: StickyOptionsListener,
): RelativeLayout(context) {

    private val timelineViewModel = TimelineViewModel()
    private val timelineView = TimelineView(context, timelineViewModel)
    private val panDetector = GestureDetector(context, PanListener())
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    private val stickyBindings = mutableListOf<StickyBinding>()

    val stickies = MutableLiveData<List<Sticky>>()
    private var adjustedTop = false

    init {
        layoutParams = params
        doOnAttach {
            findViewTreeLifecycleOwner()?.let {
                timelineViewModel.scaleData.observe(it) {
                    refresh()
                }
                timelineViewModel.positionData.observe(it) {
                    refresh()
                }
                stickies.observe(it) { stickies ->
                    if (!adjustedTop) {
                        if (day.today) {
                            timelineViewModel.scale = 4f
                            timelineViewModel.position = 100f - timelineViewModel.getPosition(TimeUtils.getCurrentTime())
                            clamp()
                            invalidate()
                            adjustedTop = true
                        }
                    }
                    refresh(stickies)
                }
            }
            addView(timelineView)
            timelineViewModel.height = timelineView.height.toFloat()
            timelineViewModel.startTime = day.startTime
        }
    }

    private fun refresh() {
        timelineView.refresh()
        stickyBindings.forEach {
            it.binding.sticky = it.sticky
            it.binding.root.y = getPosition(it.sticky)
            it.binding.options.isVisible = timelineViewModel.scale >= 3f
        }
        invalidate()
    }

    private fun refresh(stickies: List<Sticky>) {
        // add missing stickies
        stickies.filter { sticky ->
            stickyBindings.find {
                it.sticky == sticky
            } == null
        }.forEach { sticky ->
            createStickyDetail(sticky)
        }

        // remove extra stickies
        val removeStickies = stickyBindings.filter { !stickies.contains(it.sticky) }
        removeStickies.forEach {
            removeView(it.binding.root)
        }
        stickyBindings.removeIf { !stickies.contains(it.sticky) }

        refresh()
    }

    private fun createStickyDetail(sticky: Sticky) {
        val binding = StickyDetailsElementBinding.inflate(LayoutInflater.from(context), this, false)
        binding.sticky = sticky
        binding.stickyOptionsListener = stickyOptionsListener
        stickyBindings.add(StickyBinding(sticky, binding))
        addView(binding.root)
        binding.root.x = resources.displayMetrics.widthPixels / 2f - 20f
        binding.root.y = getPosition(sticky)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.pointerCount) {
            1 -> panDetector.onTouchEvent(ev)
            2 -> scaleDetector.onTouchEvent(ev)
        }
        return true
    }

    private inner class PanListener: GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            timelineViewModel.position -= distanceY / timelineViewModel.scale
            clamp()
            invalidate()
            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            timelineViewModel.scale *= detector.scaleFactor
            clamp()
            invalidate()
            return true
        }
    }

    private fun clamp() {
        timelineViewModel.scale = timelineViewModel.scale.coerceAtLeast(1f).coerceAtMost(10f)

        val limit = (height / timelineViewModel.scale - height)
        timelineViewModel.position = if (timelineViewModel.scale < 1) {
            timelineViewModel.position.coerceAtLeast(0f).coerceAtMost(limit)
        } else {
            timelineViewModel.position.coerceAtLeast(limit).coerceAtMost(0f)
        }
    }

    private fun getPosition(sticky: Sticky): Float {
        return timelineViewModel.getPosition(sticky) * timelineViewModel.scale - 56f
    }

    private data class StickyBinding(val sticky: Sticky, val binding: StickyDetailsElementBinding)

}