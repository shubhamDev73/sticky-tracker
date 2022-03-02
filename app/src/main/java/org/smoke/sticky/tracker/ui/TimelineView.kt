package org.smoke.sticky.tracker.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import androidx.core.view.doOnAttach
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import org.smoke.sticky.tracker.R
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.utils.TimeUtils

class TimelineView(
    context: Context,
    private val day: Day,
) : View(context) {

    private val COLOR = Color.BLUE
    private val TEXT_COLOR = Color.GRAY
    private val LINE_STROKE_WIDTH = 7f
    private val CIRCLE_RADIUS = 16f
    private val TEXT_SIZE = 48

    private val paint: Paint = Paint().apply { color = COLOR }
    private val textPaint: Paint = Paint().apply { color = TEXT_COLOR }

    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val panDetector = GestureDetector(context, PanListener())

    private var scale = 1f
    private var position = 0f

    private lateinit var canvas: Canvas
    val stickies = MutableLiveData<List<Sticky>>()

    init {
        doOnAttach {
            findViewTreeLifecycleOwner()?.let {
                stickies.observe(it) {
                    draw(canvas)
                }
            }
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.pointerCount) {
            1 -> panDetector.onTouchEvent(ev)
            2 -> scaleDetector.onTouchEvent(ev)
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        this.canvas = canvas

        super.onDraw(canvas)
        canvas.save()
        canvas.scale(scale, scale)

        val x = resources.displayMetrics.widthPixels / (2 * scale)

        // line
        paint.strokeWidth = LINE_STROKE_WIDTH / scale
        canvas.drawLine(x, position, x, position + height, paint)

        // time text
        val times = resources.getStringArray(R.array.times)
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = TEXT_SIZE / scale
        times.forEachIndexed { index, time ->
            canvas.drawText(time, x - 250f / scale, position + 50f + (height - 50f) * index / (times.size - 1), textPaint)
        }
        stickies.value?.let { drawStickies(it) }
        canvas.restore()
    }

    private fun drawStickies(stickies: List<Sticky>) {
        val x = resources.displayMetrics.widthPixels / (2 * scale)
        stickies.forEach { sticky ->
            val y = (sticky.timeMillis - day.startTime) * height / TimeUtils.millisInDay
            canvas.drawCircle(x, position + y, CIRCLE_RADIUS / scale, paint)
        }
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scale *= detector.scaleFactor
            clamp()
            invalidate()
            return true
        }
    }

    private inner class PanListener: SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            position -= distanceY / scale
            clamp()
            invalidate()
            return true
        }
    }

    private fun clamp() {
        scale = scale.coerceAtLeast(1f).coerceAtMost(10.0f)

        val diff = height - height * scale
        position = if (scale < 1) {
            position.coerceAtLeast(0f).coerceAtMost(diff / scale)
        } else {
            position.coerceAtLeast(diff / scale).coerceAtMost(0f)
        }
    }

}
