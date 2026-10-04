package com.justra.app.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast

/**
 * Centralized, crash-safe external intent launcher for Justra.
 * Handles URL sanitization, browser launches, emergency helpline dialing,
 * and secure Sharesheet invocations.
 */
object ActionUtils {
    private const val TAG = "ActionUtils"

    fun launchSecureWebPortal(context: Context, rawUrl: String) {
        val trimmed = rawUrl.trim()
        if (trimmed.isEmpty()) {
            Toast.makeText(context, "இணைப்பைத் திறக்க முடியவில்லை: பிரவுசர் செயலியைச் சரிபார்க்கவும்", Toast.LENGTH_LONG).show()
            return
        }

        val formattedUrl = if (!trimmed.startsWith("http://", ignoreCase = true) &&
            !trimmed.startsWith("https://", ignoreCase = true)
        ) {
            "https://$trimmed"
        } else {
            trimmed
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch web portal: $formattedUrl", e)
            Toast.makeText(context, "இணைப்பைத் திறக்க முடியவில்லை: பிரவுசர் செயலியைச் சரிபார்க்கவும்", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Sanitizes external URLs by prepending https:// if missing,
     * attaches FLAG_ACTIVITY_NEW_TASK, and launches the default browser safely.
     */
    fun openWebUrl(context: Context, url: String) {
        launchSecureWebPortal(context, url)
    }

    /**
     * Cleans phone number and launches native dialer via Intent.ACTION_DIAL.
     * ACTION_DIAL does not require dangerous CALL_PHONE permission and is safe.
     */
    fun dialEmergencyHelpline(context: Context, phoneNumber: String) {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "").trim()
        if (cleanNumber.isEmpty()) {
            Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "No dialer activity found for number: $cleanNumber", e)
            Toast.makeText(context, "No telephone dialer found on this device", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch dialer for number: $cleanNumber", e)
            Toast.makeText(context, "Cannot launch dialer: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares a generated case brief / legal notice PDF via Android Sharesheet
     * with FLAG_GRANT_READ_URI_PERMISSION.
     */
    fun shareCaseBriefPdf(context: Context, fileUri: Uri) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Legal Brief PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "No application found to share PDF", e)
            Toast.makeText(context, "No app available to share PDF file", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing PDF: ${fileUri.path}", e)
            Toast.makeText(context, "Unable to share document: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares plain text legal draft or notice via Android Sharesheet.
     */
    fun shareText(context: Context, text: String, title: String = "Share Legal Notice") {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_TITLE, title)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share text", e)
            Toast.makeText(context, "Unable to share text: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
        }
    }
}
