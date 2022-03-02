package org.smoke.sticky.tracker.sticky

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.smoke.sticky.tracker.databinding.StickyListElementBinding
import org.smoke.sticky.tracker.model.Sticky

class StickyListAdapter(private val stickies: List<Sticky>): RecyclerView.Adapter<StickyListAdapter.ViewHolder>() {

    class ViewHolder(val binding: StickyListElementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StickyListElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.sticky = stickies[position]
    }

    override fun getItemCount(): Int {
        return stickies.size
    }

}