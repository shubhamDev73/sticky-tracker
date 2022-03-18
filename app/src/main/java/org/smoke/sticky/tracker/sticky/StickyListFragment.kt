package org.smoke.sticky.tracker.sticky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.smoke.sticky.tracker.R
import org.smoke.sticky.tracker.StickyApplication
import org.smoke.sticky.tracker.databinding.LayoutFragmentBinding
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.ui.dialogs.StickyDialogFragment
import org.smoke.sticky.tracker.ui.timeline.ScrollEndListener
import org.smoke.sticky.tracker.ui.timeline.TimelineZoomView

class StickyListFragment: Fragment(), StickyOptionsListener, ScrollEndListener {

    private lateinit var binding: LayoutFragmentBinding
    private val stickyViewModel: StickyViewModel by activityViewModels {
        StickyViewModelFactory((context?.applicationContext as StickyApplication).database.stickyDao())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutFragmentBinding.inflate(inflater, container, false)

        val timeline = TimelineZoomView(requireContext(), this, this)
        binding.constraintLayout.addView(timeline)

        stickyViewModel.stickies.observe(viewLifecycleOwner) {
            timeline.updateStickies(it)
        }
        stickyViewModel.day.observe(viewLifecycleOwner) {
            timeline.updateDay(it)
        }

        return binding.root
    }

    override fun onDelete(sticky: Sticky) {
        stickyViewModel.deleteSticky(sticky)
    }

    override fun onEdit(sticky: Sticky) {
        activity?.supportFragmentManager?.let {
            StickyDialogFragment(R.string.edit_stickies, R.string.edit, sticky.tag, sticky.amount, sticky.timeMillis) { amount, tag, timeMillis ->
                sticky.amount = amount
                sticky.tag = tag
                sticky.timeMillis = timeMillis
                stickyViewModel.editSticky(sticky)
            }.show(it, "editSticky")
        }
    }

    override fun onScrollNext() = stickyViewModel.nextDay()
    override fun onScrollPrevious() = stickyViewModel.previousDay()

}