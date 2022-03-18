package org.smoke.sticky.tracker.ui.timeline

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.RelativeLayout
import androidx.core.view.doOnAttach
import androidx.lifecycle.findViewTreeLifecycleOwner
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.sticky.StickyOptionsListener

class TimelineZoomView(
    context: Context,
    private val scrollEndListener: ScrollEndListener,
    stickyOptionsListener: StickyOptionsListener,
): RelativeLayout(context) {

    private val timelineViewModel = TimelineViewModel()
    private val timelineView = TimelineView(context, timelineViewModel)
    private val timelineStickyView = TimelineStickyView(context, timelineViewModel, stickyOptionsListener)
    private val panDetector = GestureDetector(context, PanListener())
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

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
            }
            addView(timelineView)
            timelineStickyView.layoutParams = params
            addView(timelineStickyView)
        }
    }

    fun updateStickies(stickies: List<Sticky>) {
        timelineStickyView.refresh(stickies)
    }

    fun updateDay(day: Day) {
        timelineViewModel.startTime = day.startTime
    }

    private fun refresh() {
        timelineView.refresh()
        timelineStickyView.refresh()
        invalidate()
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

        val scaledHeight = timelineViewModel.scaled(timelineViewModel.height)
        val scaledHalfHeight = timelineViewModel.scaled(timelineViewModel.height / 2)
        val limit = (scaledHeight - timelineViewModel.height)
        timelineViewModel.position =
            timelineViewModel.position.coerceAtLeast(limit - scaledHeight).coerceAtMost(scaledHeight)

        if(timelineViewModel.position < limit - scaledHalfHeight && scrollEndListener.onScrollNext()) {
            timelineViewModel.position = scaledHalfHeight
        }
        if(timelineViewModel.position > scaledHalfHeight && scrollEndListener.onScrollPrevious()) {
            timelineViewModel.position = limit - scaledHalfHeight
        }
    }

}