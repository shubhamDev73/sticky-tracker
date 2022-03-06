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
    private val CURRENT_MARK_COLOR = Color.RED
    private val LINE_STROKE_WIDTH = 7f
    private val MARK_STROKE_WIDTH = 4f
    private val CIRCLE_RADIUS = 16f
    private val TEXT_SIZE = 48f
    private val MARK_WIDTH = 54f

    private val paint = Paint().apply { color = COLOR }
    private val markPaint = Paint().apply {
        color = TEXT_COLOR
        strokeCap = Paint.Cap.ROUND
    }
    private val currentMarkPaint = Paint().apply {
        color = CURRENT_MARK_COLOR
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

        val markMargin = scaled(MARK_WIDTH / 2f)
        val textMarginTop = scaled(16f)
        val textMarginRight = scaled(250f)

        // line
        paint.color = COLOR
        paint.strokeWidth = scaled(LINE_STROKE_WIDTH)
        mCanvas.drawLine(getCentre(), zoomViewModel.position, getCentre(), zoomViewModel.position + height, paint)

        if (day.today) {
            // current mark
            val currentTime = TimeUtils.getCurrentTime()
            val currentMarkY = getPosition(currentTime)
            currentMarkPaint.strokeWidth = scaled(MARK_STROKE_WIDTH)
            mCanvas.drawLine(getCentre() - markMargin, currentMarkY, getCentre() + markMargin, currentMarkY, currentMarkPaint)
            mCanvas.drawText(TimeUtils.getTimeString(currentTime), getCentre() - textMarginRight, currentMarkY + textMarginTop, textPaint)
        }

        // time text
        val times = resources.getStringArray(R.array.times)
        textPaint.textSize = scaled(TEXT_SIZE)
        markPaint.strokeWidth = scaled(MARK_STROKE_WIDTH)
        times.forEachIndexed { index, time ->
            val y = zoomViewModel.position + height * index / (times.size - 1)
            mCanvas.drawText(time, getCentre() - textMarginRight, y + textMarginTop, textPaint)

            // mark
            mCanvas.drawLine(getCentre() - markMargin, y, getCentre() + markMargin, y, markPaint)
        }
        drawStickies()
        mCanvas.restore()
    }

    private fun drawStickies() {
        if(!::stickies.isInitialized) return

        stickies.forEach { sticky ->
            paint.color = context.getColor(sticky.tag.color)
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
        return getPosition(sticky.timeMillis)
    }

    fun getPosition(timeMillis: Long): Float {
        return zoomViewModel.position + (timeMillis - day.startTime) * height / TimeUtils.millisInDay
    }

}
