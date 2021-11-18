package org.smoke.sticky.tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.smoke.sticky.tracker.databinding.ActivityMainBinding
import org.smoke.sticky.tracker.databinding.StickyDetailsBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val trackerViewModel: TrackerViewModel by viewModels{
        TrackerViewModelFactory((application as StickyApplication).database.stickyDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            it.viewModel = trackerViewModel
            it.lifecycleOwner = this
        }
        setContentView(binding.root)
        binding.addButton.setOnLongClickListener {
            addStickyDialog()
            true
        }
        lifecycleScope.launch {
            trackerViewModel.recentStickies().collect {
                binding.stickyList.adapter = StickyAdapter(it)
            }
        }
    }

    private fun addStickyDialog() {
        val input = EditText(this).apply {
            hint = "Enter Cigs"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle("Add multiple cigs")
            .setMessage("How many cigs you smoked?")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                input.text.toString().toFloatOrNull()?.let { trackerViewModel.addSticky(it) }
            }
            .create()
            .show()
    }

}

class StickyAdapter(private val stickies: List<Sticky>): RecyclerView.Adapter<StickyAdapter.ViewHolder>() {

    class ViewHolder(val binding: StickyDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StickyDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.sticky = stickies[position]
    }

    override fun getItemCount(): Int {
        return stickies.size
    }

}