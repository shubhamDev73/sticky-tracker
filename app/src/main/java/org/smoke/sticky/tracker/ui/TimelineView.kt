package org.smoke.sticky.tracker.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.utils.TimeUtils

class TimelineView(
    context: Context,
    private val stickies: List<Sticky>,
    private val day: Day,
) : View(context) {

    private val linePaint: Paint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 10F
    }
    private val circlePaint: Paint = Paint().apply {
        color = Color.RED
    }
    private val circleRadius = 20F

    override fun onDraw(canvas: Canvas) {
        val width = resources.displayMetrics.widthPixels.toFloat()
        val height = resources.displayMetrics.heightPixels.toFloat()

        canvas.drawLine(width / 2, 0F, width / 2, height, linePaint)
        stickies.forEach { sticky ->
            val y = (sticky.timeMillis - day.startTime) * height / TimeUtils.millisInDay
            canvas.drawCircle(width / 2F, y, circleRadius, circlePaint)
        }
    }

}
