package org.smoke.sticky.tracker.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.smoke.sticky.tracker.databinding.LayoutListBinding

class DayListFragment: Fragment() {

    private lateinit var binding: LayoutListBinding
    private val dayViewModel: DayViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutListBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = DayListAdapter(dayViewModel.getWeek()) { day ->
            if (day.valid) {
                val action = DayListFragmentDirections.actionDayListFragmentToStickyListFragment(day = day)
                findNavController().navigate(action)
                dayViewModel.setCurrentDay(day)
            }
        }
        return binding.root
    }

}
