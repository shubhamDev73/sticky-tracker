package org.smoke.sticky.tracker.ui

import android.content.Context
import android.view.*
import android.widget.RelativeLayout
import androidx.core.view.doOnAttach
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import org.smoke.sticky.tracker.databinding.StickyDetailsElementBinding
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Sticky

class TimelineZoomView(context: Context, day: Day): RelativeLayout(context) {

    private val zoomViewModel = ZoomViewModel()
    private val timelineView = TimelineView(context, day, zoomViewModel)
    private val panDetector = GestureDetector(context, PanListener())
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    private val stickyBindings = mutableListOf<StickyBinding>()

    val stickies = MutableLiveData<List<Sticky>>()

    init {
        layoutParams = params
        doOnAttach {
            findViewTreeLifecycleOwner()?.let {
                zoomViewModel.scaleData.observe(it) {
                    refresh()
                }
                zoomViewModel.positionData.observe(it) {
                    refresh()
                }
                stickies.observe(it) { stickies ->
                    refresh(stickies)
                }
            }
            addView(timelineView)
        }
    }

    private fun refresh() {
        timelineView.refresh()
        stickyBindings.forEach {
            it.binding.root.y = timelineView.getPosition(it.sticky) * zoomViewModel.scale - 24f
        }
    }

    private fun refresh(stickies: List<Sticky>) {
        timelineView.refresh(stickies)
        stickies.filter { sticky ->
            stickyBindings.find {
                it.sticky == sticky
            } == null
        }.forEach { sticky ->
            createStickyDetail(sticky)
        }
        val removeStickies = stickyBindings.filter { !stickies.contains(it.sticky) }
        removeStickies.forEach {
            removeView(it.binding.root)
        }
        stickyBindings.removeIf { !stickies.contains(it.sticky) }
    }

    private fun createStickyDetail(sticky: Sticky) {
        val binding = StickyDetailsElementBinding.inflate(LayoutInflater.from(context), this, false)
        binding.sticky = sticky
        stickyBindings.add(StickyBinding(sticky, binding))
        addView(binding.root)
        binding.root.x = resources.displayMetrics.widthPixels / 2f + 20f
        binding.root.y = timelineView.getPosition(sticky) * zoomViewModel.scale - 24f
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
            zoomViewModel.position -= distanceY / zoomViewModel.scale
            clamp()
            invalidate()
            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            zoomViewModel.scale *= detector.scaleFactor
            clamp()
            invalidate()
            return true
        }
    }

    private fun clamp() {
        zoomViewModel.scale = zoomViewModel.scale.coerceAtLeast(1f).coerceAtMost(10f)

        val limit = (height / zoomViewModel.scale - height)
        zoomViewModel.position = if (zoomViewModel.scale < 1) {
            zoomViewModel.position.coerceAtLeast(0f).coerceAtMost(limit)
        } else {
            zoomViewModel.position.coerceAtLeast(limit).coerceAtMost(0f)
        }
    }

    private data class StickyBinding(val sticky: Sticky, val binding: StickyDetailsElementBinding)

}