package com.omgodse.notally.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.omgodse.notally.R
import com.omgodse.notally.databinding.DialogReminderBinding
import com.omgodse.notally.miscellaneous.AlarmReceiver
import com.omgodse.notally.room.AlarmDetails
import com.omgodse.notally.viewmodels.NotallyModel
import java.util.Calendar

class ReminderSetupDialog : DialogFragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnClickListener {

    internal lateinit var binding: DialogReminderBinding

    internal val model: NotallyModel by activityViewModels()

    private var pickedDate: Calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        binding = DialogReminderBinding.inflate(inflater)

        binding.DateInput.setOnClickListener(this)
        binding.TimeInput.setOnClickListener(this)

        if (model.reminder.value != null) {
            pickedDate.timeInMillis = model.reminder.value!!

            val dateFormat = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            val formattedDate = DateUtils.formatDateTime(this.context, pickedDate.timeInMillis, dateFormat)
            binding.DateInput.setText(formattedDate)

            val timeFormat = DateUtils.FORMAT_SHOW_TIME
            val formattedTime = DateUtils.formatDateTime(this.context, pickedDate.timeInMillis, timeFormat)
            binding.TimeInput.setText(formattedTime)
        }

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.manage_reminder)
            .setView(binding.root)
            .setNegativeButton(R.string.cancel, this)
            .setPositiveButton(R.string.save, this)

        if (model.reminder.value != null) {
            dialogBuilder.setNeutralButton(R.string.delete, this)
        }

        return dialogBuilder.create()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.DateInput -> {
                val dialog = DatePickerDialog(
                    requireContext(),
                    this,
                    pickedDate.get(Calendar.YEAR),
                    pickedDate.get(Calendar.MONTH),
                    pickedDate.get(Calendar.DAY_OF_MONTH)
                )

                val now = Calendar.getInstance()
                dialog.datePicker.minDate = now.timeInMillis
                dialog.show()
            }

            R.id.TimeInput -> {
                TimePickerDialog(
                    requireContext(),
                    this,
                    pickedDate.get(Calendar.HOUR_OF_DAY),
                    pickedDate.get(Calendar.MINUTE),
                    true
                ).show()
            }
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                // first cancel previous alarm
                if (model.reminder.value != null) {
                    AlarmReceiver.cancelAlarm(
                        this.requireContext(),
                        AlarmDetails(model.reminder.value!!, model.id)
                    )
                }

                model.reminder.value = pickedDate.timeInMillis

                // schedule new alarm
                AlarmReceiver.scheduleAlarm(
                    this.requireContext(), AlarmDetails(pickedDate.timeInMillis, model.id)
                )

                dismiss()
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                dismiss()
            }

            DialogInterface.BUTTON_NEUTRAL -> {
                model.reminder.value = null
                dismiss()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        pickedDate.set(year, month, dayOfMonth)

        val dateFormat = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
        val formattedDate = DateUtils.formatDateTime(this.context, pickedDate.timeInMillis, dateFormat)
        binding.DateInput.setText(formattedDate)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        pickedDate.set(
            pickedDate.get(Calendar.YEAR),
            pickedDate.get(Calendar.MONTH),
            pickedDate.get(Calendar.DAY_OF_MONTH),
            hourOfDay,
            minute
        )

        val timeFormat = DateUtils.FORMAT_SHOW_TIME
        val formattedTime = DateUtils.formatDateTime(this.context, pickedDate.timeInMillis, timeFormat)
        binding.TimeInput.setText(formattedTime)
    }

    companion object {
        const val TAG = "ReminderSetupDialog"
    }
}