package com.elian.samoanbible.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class DailyVerseReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule notifications after device reboot
                if (areNotificationsEnabled(context)) {
                    DailyVerseWorker.scheduleDailyNotifications(context)
                }
            }
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                // Reschedule notifications after app update
                if (areNotificationsEnabled(context)) {
                    DailyVerseWorker.scheduleDailyNotifications(context)
                }
            }
        }
    }
    
    private fun areNotificationsEnabled(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(PREF_NOTIFICATIONS_ENABLED, true)
    }
    
    companion object {
        const val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }
}