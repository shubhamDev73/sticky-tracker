package org.smoke.sticky.tracker.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import org.smoke.sticky.tracker.R
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.utils.TimeUtils

class TimelineView(
    context: Context,
    private val day: Day,
    private val zoomViewModel: ZoomViewModel,
) : View(context) {

    private val COLOR = Color.BLUE
    private val TEXT_COLOR = Color.GRAY
    private val LINE_STROKE_WIDTH = 7f
    private val CIRCLE_RADIUS = 16f
    private val TEXT_SIZE = 48f

    private val paint = Paint().apply { color = COLOR }
    private val markPaint = Paint().apply {
        color = TEXT_COLOR
        strokeCap = Paint.Cap.ROUND
    }
    private val textPaint = Paint().apply {
        color = TEXT_COLOR
        textAlign = Paint.Align.LEFT
    }

    private lateinit var stickies: List<Sticky>
    private lateinit var mCanvas: Canvas

    fun refresh(stickies: List<Sticky>) {
        this.stickies = stickies
        if(!::mCanvas.isInitialized) return
        draw(mCanvas)
    }

    fun refresh() {
        if(!::mCanvas.isInitialized) return
        draw(mCanvas)
    }

    override fun onDraw(canvas: Canvas) {
        if(!::mCanvas.isInitialized) mCanvas = canvas

        super.onDraw(mCanvas)
        mCanvas.save()
        mCanvas.scale(zoomViewModel.scale, zoomViewModel.scale)

        // line
        paint.strokeWidth = scaled(LINE_STROKE_WIDTH)
        mCanvas.drawLine(getCentre(), zoomViewModel.position, getCentre(), zoomViewModel.position + height, paint)

        // time text
        val times = resources.getStringArray(R.array.times)
        textPaint.textSize = scaled(TEXT_SIZE)
        markPaint.strokeWidth = scaled(LINE_STROKE_WIDTH / 1.5f)
        times.forEachIndexed { index, time ->
            val y = zoomViewModel.position + height * index / (times.size - 1)
            mCanvas.drawText(time, getCentre() - scaled(250f), y + scaled(16f), textPaint)
            mCanvas.drawLine(getCentre() - scaled(27f), y, getCentre() + scaled(27f), y, markPaint)
        }
        drawStickies()
        mCanvas.restore()
    }

    private fun drawStickies() {
        if(!::stickies.isInitialized) return

        stickies.forEach { sticky ->
            mCanvas.drawCircle(getCentre(), getPosition(sticky), scaled(CIRCLE_RADIUS), paint)
        }
    }

    private fun scaled(x: Float): Float {
        return x / zoomViewModel.scale
    }

    private fun getCentre(): Float {
        return scaled(resources.displayMetrics.widthPixels / 2f)
    }

    fun getPosition(sticky: Sticky): Float {
        return zoomViewModel.position + (sticky.timeMillis - day.startTime) * height / TimeUtils.millisInDay
    }

}
