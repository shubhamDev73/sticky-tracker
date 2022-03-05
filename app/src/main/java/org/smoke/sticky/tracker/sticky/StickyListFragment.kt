package org.smoke.sticky.tracker.sticky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import org.smoke.sticky.tracker.StickyApplication
import org.smoke.sticky.tracker.databinding.LayoutFragmentBinding
import org.smoke.sticky.tracker.ui.TimelineZoomView
import org.smoke.sticky.tracker.utils.TimeUtils

class StickyListFragment: Fragment() {

    private lateinit var binding: LayoutFragmentBinding
    private val stickyViewModel: StickyViewModel by activityViewModels {
        StickyViewModelFactory((context?.applicationContext as StickyApplication).database.stickyDao())
    }
    private val args: StickyListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutFragmentBinding.inflate(inflater, container, false)

        val day = args.day ?: TimeUtils.getToday()
        stickyViewModel.updateDay(day)

        val timeline = TimelineZoomView(requireContext(), day) {
            stickyViewModel.delete(it)
        }
        binding.constraintLayout.addView(timeline)

        stickyViewModel.stickies.observe(viewLifecycleOwner) {
            timeline.stickies.postValue(it)
        }

        return binding.root
    }

}