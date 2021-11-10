package org.smoke.sticky.tracker

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import org.smoke.sticky.tracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val trackerViewModel: TrackerViewModel by viewModels()

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
            .setPositiveButton("Add") { _: DialogInterface, _: Int ->
                input.text.toString().toFloatOrNull()?.let { trackerViewModel.addSticky(it) }
            }
            .create()
            .show()
    }

}