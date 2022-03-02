package org.smoke.sticky.tracker.sticky

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.smoke.sticky.tracker.StickyApplication
import org.smoke.sticky.tracker.utils.TimeUtils
import org.smoke.sticky.tracker.databinding.StickyListFragmentBinding

class StickyListFragment: Fragment() {

    private lateinit var binding: StickyListFragmentBinding
    private val stickyViewModel: StickyViewModel by activityViewModels {
        StickyViewModelFactory((context?.applicationContext as StickyApplication).database.stickyDao())
    }
    private val args: StickyListFragmentArgs by navArgs()

    private val circleRadius = 20F
    private val lineColor = Color.BLUE
    private val circleColor = Color.RED
    private val lineWidth = 10F

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = StickyListFragmentBinding.inflate(inflater, container, false)

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels.toFloat()
        val bitmap = Bitmap.createBitmap(width, height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        binding.lineImageView.setImageBitmap(bitmap)

        canvas.drawLine(width / 2F, 0F, width / 2F, height, Paint().apply {
            color = lineColor
            strokeWidth = lineWidth
        })

        val day = args.day ?: TimeUtils.getToday()
        lifecycleScope.launch {
            stickyViewModel.recentStickies(day).collect {
                val circlePaint = Paint()
                circlePaint.color = circleColor
                it.forEach { sticky ->
                    val y = (sticky.timeMillis - day.startTime) * height / TimeUtils.millisInDay
                    canvas.drawCircle(width / 2F, y, circleRadius, circlePaint)
                }
            }
        }
        return binding.root
    }

}