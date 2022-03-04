package org.smoke.sticky.tracker.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.smoke.sticky.tracker.StickyApplication
import org.smoke.sticky.tracker.databinding.DayListFragmentBinding
import org.smoke.sticky.tracker.sticky.StickyViewModel
import org.smoke.sticky.tracker.sticky.StickyViewModelFactory

class DayListFragment: Fragment() {

    private lateinit var binding: DayListFragmentBinding
    private val dayViewModel: DayViewModel by activityViewModels()
    private val stickyViewModel: StickyViewModel by activityViewModels {
        StickyViewModelFactory((context?.applicationContext as StickyApplication).database.stickyDao())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DayListFragmentBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = DayListAdapter(dayViewModel.getWeek(), stickyViewModel) { day ->
            if (day.valid) {
                val action = DayListFragmentDirections.actionDayListFragmentToStickyListFragment(day = day)
                findNavController().navigate(action)
                dayViewModel.setCurrentDay(day)
            }
        }
        return binding.root
    }

}
