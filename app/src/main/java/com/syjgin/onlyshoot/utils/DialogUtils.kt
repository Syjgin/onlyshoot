package com.syjgin.onlyshoot.utils

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import com.syjgin.onlyshoot.R

object DialogUtils {
    interface InputFieldDialogListener {
        fun onValueSelected(value: String)
        fun onCancel()
    }

    fun createInputDialog(
        context: Context?,
        caption: String,
        inputFieldDialogListener: InputFieldDialogListener) : AlertDialog? {
        if(context == null)
            return null
        val builder = AlertDialog.Builder(context)
        builder.setTitle(caption)
        val input = EditText(context)
        builder.setView(input)
        builder.setPositiveButton(android.R.string.ok
        ) { _, _ -> inputFieldDialogListener.onValueSelected(input.text.toString())}
        builder.setNegativeButton(android.R.string.cancel) {
                _, _ -> inputFieldDialogListener.onCancel()
        }
        return builder.create()
    }

    interface CountDialogListener {
        fun onValueSelected(value: Int, isRandom: Boolean)
        fun onCancel()
    }

    interface TwoOptionsDialogListener {
        fun onFirstOptionSelected()
        fun onSecondOptionSelected()
        fun onCancel()
    }

    fun createTwoOptionsDialog(
        context: Context?,
        listener: TwoOptionsDialogListener,
        @LayoutRes layout: Int
    ): Dialog? {
        if (context == null)
            return null
        var isFirstOptionSelected: Boolean? = null
        val dialog = Dialog(context)
        dialog.setTitle(context.getString(R.string.add))
        dialog.setContentView(layout)
        dialog.findViewById<Button>(R.id.ok)
            .setOnClickListener {
                if (isFirstOptionSelected != null) {
                    if (isFirstOptionSelected!!) {
                        listener.onFirstOptionSelected()
                    } else {
                        listener.onSecondOptionSelected()
                    }
                }
            }
        dialog.findViewById<Button>(R.id.cancel)
            .setOnClickListener { listener.onCancel() }
        dialog.findViewById<RadioButton>(R.id.first_option)
            .setOnCheckedChangeListener { _, isChecked ->
                isFirstOptionSelected = isChecked
            }
        dialog.findViewById<RadioButton>(R.id.second_option)
            .setOnCheckedChangeListener { _, isChecked ->
                isFirstOptionSelected = !isChecked
            }
        return dialog
    }

    fun createCountDialog(
        context: Context?,
        caption: String,
        countDialogListener: CountDialogListener,
        maxCount: Int
    ): Dialog? {
        if (context == null)
            return null
        val dialog = Dialog(context)
        dialog.setTitle(caption)
        var currentCount = maxCount
        var isRandom: Boolean? = null
        dialog.setContentView(R.layout.dialog_count)
        dialog.findViewById<Button>(R.id.ok)
            .setOnClickListener {
                if (isRandom != null) {
                    countDialogListener.onValueSelected(currentCount, isRandom!!)
                }
            }
        dialog.findViewById<Button>(R.id.cancel)
            .setOnClickListener { countDialogListener.onCancel() }
        dialog.findViewById<RadioButton>(R.id.full_random)
            .setOnCheckedChangeListener { _, isChecked ->
                isRandom = isChecked
            }
        dialog.findViewById<RadioButton>(R.id.wounded_first)
            .setOnCheckedChangeListener { _, isChecked ->
                isRandom = !isChecked
            }
        dialog.findViewById<TextView>(R.id.count)?.text = maxCount.toString()
        dialog.findViewById<Button>(R.id.add_count)?.setOnClickListener {
            if (currentCount + 1 <= maxCount) {
                currentCount++
                dialog.findViewById<TextView>(R.id.count).text = currentCount.toString()
            }
        }
        dialog.findViewById<Button>(R.id.remove_count)?.setOnClickListener {
            if (currentCount - 1 > 0) {
                currentCount--
                dialog.findViewById<TextView>(R.id.count).text = currentCount.toString()
            }
        }
        return dialog
    }
}