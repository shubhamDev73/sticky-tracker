package org.smoke.sticky.tracker.ui.dialogs

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import org.smoke.sticky.tracker.R
import org.smoke.sticky.tracker.databinding.StickyDialogBinding
import org.smoke.sticky.tracker.model.Tag
import org.smoke.sticky.tracker.utils.TimeUtils
import java.util.*

class StickyDialogFragment(
    private val titleResourceId: Int,
    private val positiveButtonResourceId: Int,
    private val defaultTag: Tag = Tag.CIGARETTE,
    private val defaultAmount: Float = 1.0f,
    private val defaultTime: Long = TimeUtils.getCurrentTime(),
    private val onPositiveClick: (Float, Tag, Long) -> Unit
): DialogFragment(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private lateinit var binding: StickyDialogBinding
    private val stickyDialogViewModel: StickyDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = StickyDialogBinding.inflate(layoutInflater)
        activity?.let {
            val spinner = binding.spinner
            val adapter = ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, Tag.values().map { getString(it.label) })
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object: AdapterView.OnItemClickListener,
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    stickyDialogViewModel.tag = Tag[position]
                }
                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            spinner.setSelection(defaultTag.ordinal)

            binding.stickyAmount.setText(defaultAmount.toString())

            stickyDialogViewModel.calendar = Calendar.getInstance().apply { timeInMillis = defaultTime }
            binding.stickyTime.text = TimeUtils.getDateTimeString(defaultTime)
            binding.timeEditButton.setOnClickListener { createDatePicker() }

            return AlertDialog.Builder(it)
                .setTitle(titleResourceId)
                .setView(binding.root)
                .setPositiveButton(positiveButtonResourceId) { _, _ ->
                    binding.stickyAmount.text.toString().toFloatOrNull()?.let { amount ->
                        onPositiveClick(amount, stickyDialogViewModel.tag, stickyDialogViewModel.calendar.timeInMillis)
                    }
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
        }
        return super.onCreateDialog(savedInstanceState)
    }

    private fun createDatePicker() {
        DatePickerDialog(
            requireContext(),
            this,
            stickyDialogViewModel.calendar.get(Calendar.YEAR),
            stickyDialogViewModel.calendar.get(Calendar.MONTH),
            stickyDialogViewModel.calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun createTimePicker() {
        TimePickerDialog(
            requireContext(),
            this,
            stickyDialogViewModel.calendar.get(Calendar.HOUR_OF_DAY),
            stickyDialogViewModel.calendar.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(activity)
        ).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        stickyDialogViewModel.calendar.set(Calendar.YEAR, year)
        stickyDialogViewModel.calendar.set(Calendar.MONTH, month)
        stickyDialogViewModel.calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        binding.stickyTime.text = TimeUtils.getDateTimeString(stickyDialogViewModel.calendar.timeInMillis)
        createTimePicker()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        stickyDialogViewModel.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        stickyDialogViewModel.calendar.set(Calendar.MINUTE, minute)
        binding.stickyTime.text = TimeUtils.getDateTimeString(stickyDialogViewModel.calendar.timeInMillis)
    }

}