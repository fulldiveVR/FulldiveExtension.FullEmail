package com.bugsnag.android.appextension

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import eu.faircode.email.BuildConfig
import eu.faircode.email.R

class EmailHelper {
    companion object {
        private const val MIME_TYPE_PLAIN_TEXT = "text/plain"
        private const val SUPPORT_EMAIL = "support@fulldive.com"

        @JvmStatic
        fun sendEmailToSupport(context: Context) {
            PopupManager().showContactSupportDialog(context) {
                sendEmail(
                        context,
                        "",
                        "${context.getString(R.string.app_name)}-${BuildConfig.VERSION_NAME}",
                        SUPPORT_EMAIL
                )
            }
        }

        @JvmStatic
        fun sendEmailToSupport(context: Context, message: String) {
            PopupManager().showContactSupportDialog(context) {
                sendEmail(
                        context,
                        message,
                        "${context.getString(R.string.app_name)}-${BuildConfig.VERSION_NAME}",
                        SUPPORT_EMAIL
                )
            }
        }

        private fun sendEmail(
                context: Context,
                message: String,
                subject: String,
                email: String
        ) {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                    .apply {
                        putExtra(Intent.EXTRA_TEXT, message)
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                        type = MIME_TYPE_PLAIN_TEXT
                    }
            var success = false
            try {
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(
                            Intent.createChooser(
                                    intent, context.getString(R.string.flat_fraud_intent_chooser)
                            )
                    )
                    success = true
                }
            } catch (error: ActivityNotFoundException) {
            }
            if (!success) {
                sendIntent(context, message, subject, email)
            }
        }

        private fun sendIntent(
                context: Context,
                message: String,
                subject: String,
                email: String
        ): Boolean {
            val intent = Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", email, null)).apply {
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                type = MIME_TYPE_PLAIN_TEXT
            }
            var success = false
            try {
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(
                            Intent.createChooser(
                                    intent,
                                    context.getString(R.string.flat_fraud_intent_chooser)
                            )
                    )
                    success = true
                }
            } catch (error: ActivityNotFoundException) {
            }
            return success
        }
    }
}
