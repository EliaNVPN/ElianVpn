package com.elian.samoanbible

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import androidx.work.WorkManager
import com.elian.samoanbible.data.database.EnglishBibleDatabase
import com.elian.samoanbible.data.database.SamoanBibleDatabase
import com.elian.samoanbible.data.repository.BibleRepository
import com.elian.samoanbible.notifications.DailyVerseReceiver
import com.elian.samoanbible.notifications.DailyVerseWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SamoanBibleApplication : Application() {
    
    // Database instances
    private val samoanDatabase by lazy { SamoanBibleDatabase.getDatabase(this) }
    private val englishDatabase by lazy { EnglishBibleDatabase.getDatabase(this) }
    
    // Repository instance
    val repository by lazy { 
        BibleRepository(
            samoanDatabase.verseDao(),
            englishDatabase.verseDao()
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize WorkManager
        WorkManager.initialize(this, Configuration.Builder().build())
        
        // Copy databases from assets on first launch
        CoroutineScope(Dispatchers.IO).launch {
            copyDatabasesFromAssets()
        }
        
        // Set up daily notifications if enabled
        setupDailyNotifications()
    }
    
    private fun copyDatabasesFromAssets() {
        try {
            // Copy Samoan Bible database
            copyDatabaseFromAssets("SMO.db", "databases/samoan_bible.db")
            
            // Copy English Bible database
            copyDatabaseFromAssets("KJVA.SQLite3", "databases/english_bible.db")
            
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    private fun copyDatabaseFromAssets(assetFileName: String, databasePath: String) {
        val dbFile = File(filesDir, databasePath)
        
        if (!dbFile.exists()) {
            try {
                // Create the directories if they don't exist
                dbFile.parentFile?.mkdirs()
                
                // Copy the database from assets
                assets.open(assetFileName).use { input ->
                    FileOutputStream(dbFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    
    private fun setupDailyNotifications() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val notificationsEnabled = preferences.getBoolean(
            DailyVerseReceiver.PREF_NOTIFICATIONS_ENABLED, 
            true
        )
        
        if (notificationsEnabled) {
            DailyVerseWorker.scheduleDailyNotifications(this)
        }
    }
    
    companion object {
        fun getRepository(context: Context): BibleRepository {
            return (context.applicationContext as SamoanBibleApplication).repository
        }
    }
}