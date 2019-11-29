package com.syjgin.onlyshoot.utils

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
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
        fun onValueSelected(value: Int)
        fun onCancel()
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
        dialog.setContentView(R.layout.dialog_count)
        dialog.findViewById<Button>(R.id.ok)
            .setOnClickListener { countDialogListener.onValueSelected(currentCount) }
        dialog.findViewById<Button>(R.id.cancel)
            .setOnClickListener { countDialogListener.onCancel() }
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