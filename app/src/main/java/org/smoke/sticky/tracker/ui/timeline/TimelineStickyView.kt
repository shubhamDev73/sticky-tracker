package org.smoke.sticky.tracker.ui.timeline

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import org.smoke.sticky.tracker.databinding.StickyDetailsElementBinding
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.sticky.StickyOptionsListener

class TimelineStickyView(
    context: Context,
    private val timelineViewModel: TimelineViewModel,
    private val stickyOptionsListener: StickyOptionsListener,
): RelativeLayout(context) {

    private val stickyBindings = mutableListOf<StickyBinding>()

    private fun createStickyDetail(sticky: Sticky) {
        val binding = StickyDetailsElementBinding.inflate(LayoutInflater.from(context), this, false)
        binding.sticky = sticky
        binding.stickyOptionsListener = stickyOptionsListener
        stickyBindings.add(StickyBinding(sticky, binding))
        addView(binding.root)
        binding.root.x = resources.displayMetrics.widthPixels / 2f - 20f
        binding.root.y = getPosition(sticky)
    }

    fun refresh() {
        stickyBindings.forEach {
            it.binding.sticky = it.sticky
            it.binding.root.y = getPosition(it.sticky)
            it.binding.options.isVisible = timelineViewModel.scale >= 3f
        }
    }

    fun refresh(stickies: List<Sticky>) {
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

    private fun getPosition(sticky: Sticky): Float {
        return timelineViewModel.getPosition(sticky) * timelineViewModel.scale - 56f
    }
}

private data class StickyBinding(val sticky: Sticky, val binding: StickyDetailsElementBinding)
