package org.smoke.sticky.tracker

import android.graphics.drawable.Drawable
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
    private lateinit var navigationIcon: Drawable
    private val stickyViewModel: StickyViewModel by viewModels {
        StickyViewModelFactory((application as StickyApplication).database.stickyDao())
    }
    private val dayViewModel: DayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StickyActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigationIcon = binding.topBar.navigationIcon!!
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
            onBackPressed()
        }
        observeLabel()
        observeTopBar()
    }

    private fun observeLabel() {
        dayViewModel.currentDay.observe(this) { day ->
            assignLabel(day, 0f)
            day?.let {
                stickyViewModel.recentCount(day).observe(this) {
                    assignLabel(day, it ?: 0f)
                }
            }
        }
    }

    private fun assignLabel(day: Day?, count: Float) {
        if (day == null) {
            binding.topBar.title = getString(R.string.week_label)
        } else {
            val label = if (day.today) getString(R.string.today) else day.label
            binding.topBar.title = getString(R.string.sticky_count, label, count)
        }
    }

    private fun observeTopBar() {
        dayViewModel.currentDay.observe(this) { day ->
            binding.topBar.navigationIcon = if (day == null) null else navigationIcon
            binding.topBar.menu?.setGroupVisible(R.id.addStickyGroup, day?.today ?: false)
        }
    }

    private fun addStickyDialog() {
        val input = EditText(this).apply {
            hint = getString(R.string.add_stickies_dialog_placeholder)
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.add_stickies_dialog_title)
            .setMessage(R.string.add_stickies_dialog_message)
            .setView(input)
            .setPositiveButton(R.string.add) { _, _ ->
                input.text.toString().toFloatOrNull()?.let { stickyViewModel.addSticky(it) }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    override fun onBackPressed() {
        if (dayViewModel.currentDay.value == null) {
            finish()
        } else {
            val action = StickyListFragmentDirections.actionStickyListFragmentToDayListFragment()
            findNavController(binding.navHostFragment.id).navigate(action)
            dayViewModel.setCurrentDay(null)
        }
    }

}