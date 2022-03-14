package org.smoke.sticky.tracker.ui.timeline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import org.smoke.sticky.tracker.R
import org.smoke.sticky.tracker.utils.TimeUtils

class TimelineView(
    context: Context,
    private val timelineViewModel: TimelineViewModel,
) : View(context) {

    private val COLOR = Color.BLUE
    private val TEXT_COLOR = Color.GRAY
    private val CURRENT_MARK_COLOR = Color.RED
    private val LINE_STROKE_WIDTH = 7f
    private val MARK_STROKE_WIDTH = 4f
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

    private lateinit var mCanvas: Canvas

    fun refresh() {
        if(!::mCanvas.isInitialized) return
        timelineViewModel.height = height.toFloat()
        draw(mCanvas)
    }

    override fun onDraw(canvas: Canvas) {
        if(!::mCanvas.isInitialized) mCanvas = canvas

        super.onDraw(mCanvas)
        mCanvas.save()
        mCanvas.scale(timelineViewModel.scale, timelineViewModel.scale)

        val markMargin = scaled(MARK_WIDTH / 2f)
        val textMarginTop = scaled(16f)
        val textMarginRight = scaled(250f)

        // line
        paint.color = COLOR
        paint.strokeWidth = scaled(LINE_STROKE_WIDTH)
        mCanvas.drawLine(getCentre(), timelineViewModel.position, getCentre(), timelineViewModel.position + height, paint)

        // current mark
        val currentTime = TimeUtils.getCurrentTime()
        val currentMarkY = timelineViewModel.getPosition(currentTime)
        currentMarkPaint.strokeWidth = scaled(MARK_STROKE_WIDTH)
        mCanvas.drawLine(getCentre() - markMargin, currentMarkY, getCentre() + markMargin, currentMarkY, currentMarkPaint)
        mCanvas.drawText(TimeUtils.getTimeString(currentTime), getCentre() - textMarginRight, currentMarkY + textMarginTop, textPaint)

        // time text
        val times = resources.getStringArray(R.array.times)
        textPaint.textSize = scaled(TEXT_SIZE)
        markPaint.strokeWidth = scaled(MARK_STROKE_WIDTH)
        times.forEachIndexed { index, time ->
            val y = timelineViewModel.position + height * index / (times.size - 1)
            mCanvas.drawText(time, getCentre() - textMarginRight, y + textMarginTop, textPaint)

            // mark
            mCanvas.drawLine(getCentre() - markMargin, y, getCentre() + markMargin, y, markPaint)
        }
        mCanvas.restore()
    }

    private fun scaled(x: Float): Float {
        return x / timelineViewModel.scale
    }

    private fun getCentre(): Float {
        return scaled(resources.displayMetrics.widthPixels / 2f)
    }

}
