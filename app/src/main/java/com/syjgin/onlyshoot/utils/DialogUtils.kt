package com.syjgin.onlyshoot.utils

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
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

    interface LoadUnitDialogListener {
        fun onFromUnitSelected()
        fun onFromArchetypeSelected()
        fun onCancel()
    }

    fun createAddUnitDialog(
        context: Context?,
        listener: LoadUnitDialogListener
    ): Dialog? {
        if (context == null)
            return null
        var isUnitSelected: Boolean? = null
        val dialog = Dialog(context)
        dialog.setTitle(context.getString(R.string.add))
        dialog.setContentView(R.layout.dialog_add_unit)
        dialog.findViewById<Button>(R.id.ok)
            .setOnClickListener {
                if (isUnitSelected != null) {
                    if (isUnitSelected!!) {
                        listener.onFromUnitSelected()
                    } else {
                        listener.onFromArchetypeSelected()
                    }
                }
            }
        dialog.findViewById<Button>(R.id.cancel)
            .setOnClickListener { listener.onCancel() }
        dialog.findViewById<RadioButton>(R.id.create_new)
            .setOnCheckedChangeListener { _, isChecked ->
                isUnitSelected = isChecked
            }
        dialog.findViewById<RadioButton>(R.id.select_archetype)
            .setOnCheckedChangeListener { _, isChecked ->
                isUnitSelected = !isChecked
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