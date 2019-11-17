package com.syjgin.onlyshoot.utils

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

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
}