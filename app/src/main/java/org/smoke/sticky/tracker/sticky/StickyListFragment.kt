package org.smoke.sticky.tracker.sticky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import org.smoke.sticky.tracker.R
import org.smoke.sticky.tracker.StickyApplication
import org.smoke.sticky.tracker.databinding.LayoutFragmentBinding
import org.smoke.sticky.tracker.model.Sticky
import org.smoke.sticky.tracker.ui.dialogs.StickyDialogFragment
import org.smoke.sticky.tracker.ui.timeline.TimelineZoomView
import org.smoke.sticky.tracker.utils.TimeUtils

class StickyListFragment: Fragment(), StickyOptionsListener {

    private lateinit var binding: LayoutFragmentBinding
    private val stickyViewModel: StickyViewModel by activityViewModels {
        StickyViewModelFactory((context?.applicationContext as StickyApplication).database.stickyDao())
    }
    private val args: StickyListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutFragmentBinding.inflate(inflater, container, false)

        val day = args.day ?: TimeUtils.getToday()
        stickyViewModel.updateDay(day)

        val timeline = TimelineZoomView(requireContext(), day, this)
        binding.constraintLayout.addView(timeline)

        stickyViewModel.stickies.observe(viewLifecycleOwner) {
            timeline.stickies.postValue(it)
        }

        return binding.root
    }

    override fun onDelete(sticky: Sticky) {
        stickyViewModel.delete(sticky)
    }

    override fun onEdit(sticky: Sticky) {
        activity?.supportFragmentManager?.let {
            StickyDialogFragment(R.string.edit_stickies, R.string.edit, sticky.tag, sticky.amount, sticky.timeMillis) { amount, tag, timeMillis ->
                sticky.amount = amount
                sticky.tag = tag
                sticky.timeMillis = timeMillis
                stickyViewModel.edit(sticky)
            }.show(it, "editSticky")
        }
    }

}