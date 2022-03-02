package org.smoke.sticky.tracker.sticky

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
import org.smoke.sticky.tracker.databinding.StickyListFragmentBinding
import org.smoke.sticky.tracker.ui.TimelineView
import org.smoke.sticky.tracker.utils.TimeUtils

class StickyListFragment: Fragment() {

    private lateinit var binding: StickyListFragmentBinding
    private val stickyViewModel: StickyViewModel by activityViewModels {
        StickyViewModelFactory((context?.applicationContext as StickyApplication).database.stickyDao())
    }
    private val args: StickyListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = StickyListFragmentBinding.inflate(inflater, container, false)

        val day = args.day ?: TimeUtils.getToday()
        lifecycleScope.launch {
            stickyViewModel.recentStickies(day).collect {
                binding.constraintLayout.addView(TimelineView(requireContext(), it, day))
            }
        }
        return binding.root
    }

}