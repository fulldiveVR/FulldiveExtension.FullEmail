package com.bugsnag.android.appextension

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import eu.faircode.email.R

object ContactSupportDialogBuilder {

    fun show(context: Context, onPositiveClicked: () -> Unit) {
        val dialog = AlertDialog
            .Builder(context)
            .setTitle(R.string.support_title)
            .setMessage(R.string.support_description)
            .setPositiveButton(R.string.support_submit) { _, _ ->
                onPositiveClicked.invoke()
            }
            .setNegativeButton(R.string.maybe_later) { _, _ -> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(context, R.color.textColorAccent))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(ContextCompat.getColor(context, R.color.textColorSecondary))
        }
        dialog.show()
    }
}