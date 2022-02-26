package org.smoke.sticky.tracker.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import org.smoke.sticky.tracker.R
import org.smoke.sticky.tracker.StickyApplication
import org.smoke.sticky.tracker.databinding.StickyActivityBinding

class StickyActivity : AppCompatActivity() {

    private lateinit var binding: StickyActivityBinding
    private val trackerViewModel: TrackerViewModel by viewModels{
        TrackerViewModelFactory((application as StickyApplication).database.stickyDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StickyActivityBinding.inflate(layoutInflater).also {
            it.viewModel = trackerViewModel
            it.lifecycleOwner = this
        }
        setContentView(binding.root)
        binding.topBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    trackerViewModel.addSticky()
                    true
                }
                R.id.addMultiple -> {
                    addStickyDialog()
                    true
                }
                else -> false
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