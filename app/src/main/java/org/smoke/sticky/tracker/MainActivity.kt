package org.smoke.sticky.tracker

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import org.smoke.sticky.tracker.databinding.FloatingActionButtonBinding
import org.smoke.sticky.tracker.databinding.StickyActivityBinding
import org.smoke.sticky.tracker.day.DayViewModel
import org.smoke.sticky.tracker.model.Day
import org.smoke.sticky.tracker.model.Tag
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
                R.id.filter -> {
                    filterStickyDialog()
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
        addOptions()
    }

    private fun observeLabel() {
        dayViewModel.currentDay.observe(this) { day ->
            assignLabel(day, 0f)
            stickyViewModel.tags.removeObservers(this)
            day?.let {
                stickyViewModel.tags.observe(this) {
                    stickyViewModel.recentCount(day).observe(this) {
                        assignLabel(day, it ?: 0f)
                    }
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
            binding.addOptions.isVisible = day != null
        }
    }

    private fun addOptions() {
        Tag.values().forEach { tag ->
            val fab = FloatingActionButtonBinding.inflate(layoutInflater, binding.addOptions, false)
            fab.floatingActionButton.setImageIcon(Icon.createWithResource(applicationContext, tag.icon))
            val states = arrayOf(intArrayOf(android.R.attr.state_enabled))
            val colors = intArrayOf(getColor(tag.color))
            fab.floatingActionButton.backgroundTintList = ColorStateList(states, colors)
            binding.addOptions.addView(fab.root)
            fab.floatingActionButton.setOnClickListener {
                stickyViewModel.addSticky(tag)
            }
            fab .floatingActionButton.setOnLongClickListener {
                addStickyDialog(tag)
                true
            }
        }
    }

    private fun addStickyDialog(defaultTag: Tag) {
        val input = EditText(this).apply {
            hint = getString(R.string.add_stickies_dialog_placeholder)
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.add_stickies_dialog_title)
            .setMessage(R.string.add_stickies_dialog_message)
            .setView(input)
            .setPositiveButton(R.string.add) { _, _ ->
                input.text.toString().toFloatOrNull()?.let { stickyViewModel.addSticky(it, defaultTag) }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    private fun filterStickyDialog() {
        val selectedTags = stickyViewModel.getTags()
        val checked = Tag.values().map { selectedTags.contains(it) }

        val tagLabels = Tag.values().map { getString(it.label) }

        val listener = DialogInterface.OnMultiChoiceClickListener { _, which, isChecked ->
            stickyViewModel.updateTag(Tag[which], isChecked)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.filter_stickies_dialog_title)
            .setMultiChoiceItems(tagLabels.toTypedArray(), checked.toBooleanArray(), listener)
            .setNeutralButton(R.string.confirm) { dialog, _ ->
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