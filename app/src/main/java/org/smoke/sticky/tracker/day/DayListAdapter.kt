package org.smoke.sticky.tracker.day

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import org.smoke.sticky.tracker.sticky.StickyViewModel
import org.smoke.sticky.tracker.databinding.DayListElementBinding
import org.smoke.sticky.tracker.model.Day

class DayListAdapter(
    private val days: List<Day>,
    private val stickyViewModel: StickyViewModel,
    private val onClickListener: (Day) -> Unit
): RecyclerView.Adapter<DayListAdapter.ViewHolder>() {

    class ViewHolder(val binding: DayListElementBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DayListElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.lifecycleOwner = parent.findViewTreeLifecycleOwner()
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = days[position]
        holder.binding.day = day
        holder.binding.lifecycleOwner?.let { owner ->
            stickyViewModel.tags.observe(owner) {
                stickyViewModel.recentCount(day).observe(owner) { count ->
                    holder.binding.count = count
                }
            }
        }
        holder.binding.button.setOnClickListener{ onClickListener(day) }
    }

    override fun getItemCount(): Int {
        return days.size
    }

}