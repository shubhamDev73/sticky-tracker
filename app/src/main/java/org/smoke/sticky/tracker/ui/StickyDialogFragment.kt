package org.smoke.sticky.tracker.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import org.smoke.sticky.tracker.R
import org.smoke.sticky.tracker.databinding.StickyDialogBinding
import org.smoke.sticky.tracker.model.Tag

class StickyDialogFragment(private val onPositiveClick: (Float, Tag) -> Unit): DialogFragment() {

    private lateinit var binding: StickyDialogBinding
    private lateinit var tag: Tag

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = StickyDialogBinding.inflate(layoutInflater)
        activity?.let {
            val spinner = binding.spinner
            val adapter = ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, Tag.values().map { getString(it.label) })
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object: AdapterView.OnItemClickListener,
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    tag = Tag[position]
                }
                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            return AlertDialog.Builder(it)
                .setTitle(R.string.add_stickies)
                .setView(binding.root)
                .setPositiveButton(R.string.add) { _, _ ->
                    binding.stickyAmount.text.toString().toFloatOrNull()?.let { onPositiveClick(it, tag) }
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
        }
        return super.onCreateDialog(savedInstanceState)
    }
}