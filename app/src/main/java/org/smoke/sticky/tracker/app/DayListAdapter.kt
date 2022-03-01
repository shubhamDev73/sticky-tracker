package org.smoke.sticky.tracker.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.smoke.sticky.tracker.databinding.DayListElementBinding
import org.smoke.sticky.tracker.model.Day

class DayListAdapter(private val days: List<Day>): RecyclerView.Adapter<DayListAdapter.ViewHolder>() {

    class ViewHolder(val binding: DayListElementBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DayListElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.day = days[position]
    }

    override fun getItemCount(): Int {
        return days.size
    }

}