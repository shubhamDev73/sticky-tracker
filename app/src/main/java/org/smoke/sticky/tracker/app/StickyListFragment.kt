package org.smoke.sticky.tracker.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.smoke.sticky.tracker.StickyApplication
import org.smoke.sticky.tracker.databinding.LayoutListBinding

class StickyListFragment: Fragment() {

    private lateinit var binding: LayoutListBinding
    private val trackerViewModel: TrackerViewModel by activityViewModels {
        TrackerViewModelFactory((context?.applicationContext as StickyApplication).database.stickyDao())
    }
    private val args: StickyListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutListBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch {
            trackerViewModel.recentStickies(args.startTime).collect {
                binding.recyclerView.adapter = StickyListAdapter(it)
            }
        }
        return binding.root
    }

}