package org.smoke.sticky.tracker

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import org.smoke.sticky.tracker.databinding.StickyActivityBinding
import org.smoke.sticky.tracker.day.DayViewModel
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.sticky.StickyListFragmentDirections
import org.smoke.sticky.tracker.sticky.StickyViewModel
import org.smoke.sticky.tracker.sticky.StickyViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: StickyActivityBinding
    private val stickyViewModel: StickyViewModel by viewModels {
        StickyViewModelFactory((application as StickyApplication).database.stickyDao())
    }
    private val dayViewModel: DayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StickyActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    stickyViewModel.addSticky()
                    true
                }
                R.id.addMultiple -> {
                    addStickyDialog()
                    true
                }
                else -> false
            }
        }
        binding.topBar.setNavigationOnClickListener {
            val action = StickyListFragmentDirections.actionStickyListFragmentToDayListFragment()
            findNavController(binding.navHostFragment.id).navigate(action)
        }
        observeLabel()
        observeTopBar()
    }

    private fun observeLabel() {
        dayViewModel.currentDay.observe(this) { day ->
            assignLabel(day, 0f)
            stickyViewModel.recentCount(day).observe(this) {
                assignLabel(day, it ?: 0f)
            }
        }
    }

    private fun assignLabel(day: Day, count: Float) {
        val label = if (day.today) getString(R.string.today) else day.label
        binding.topBar.title = getString(R.string.cig_count, label, count)
    }

    private fun observeTopBar() {
        dayViewModel.currentDay.observe(this) { day ->
            binding.topBar.menu?.setGroupVisible(R.id.addStickyGroup, day.today)
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
                input.text.toString().toFloatOrNull()?.let { stickyViewModel.addSticky(it) }
            }
            .create()
            .show()
    }

}