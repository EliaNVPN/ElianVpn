package com.elian.samoanbible.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.elian.samoanbible.MainActivity
import com.elian.samoanbible.R
import com.elian.samoanbible.data.database.EnglishBibleDatabase
import com.elian.samoanbible.data.database.SamoanBibleDatabase
import com.elian.samoanbible.data.repository.BibleRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailyVerseWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    private val notificationManager = NotificationManagerCompat.from(context)
    
    override suspend fun doWork(): Result {
        return try {
            val verseType = inputData.getString(KEY_VERSE_TYPE) ?: TYPE_MORNING
            
            // Get the verse from database
            val repository = BibleRepository(
                SamoanBibleDatabase.getDatabase(context).verseDao(),
                EnglishBibleDatabase.getDatabase(context).verseDao()
            )
            
            val verse = repository.getRandomVerse()
            
            if (verse != null) {
                createNotificationChannel()
                sendVerseNotification(verse, verseType)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = CHANNEL_ID
            val channelName = context.getString(R.string.notification_channel_name)
            val channelDescription = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun sendVerseNotification(verse: com.elian.samoanbible.data.repository.BilingualVerse, verseType: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = when (verseType) {
            TYPE_MORNING -> context.getString(R.string.morning_verse_title)
            TYPE_AFTERNOON -> context.getString(R.string.afternoon_verse_title)
            TYPE_EVENING -> context.getString(R.string.evening_verse_title)
            else -> context.getString(R.string.daily_verse)
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(verse.reference)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(verse.getNotificationText()))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        val notificationId = when (verseType) {
            TYPE_MORNING -> MORNING_NOTIFICATION_ID
            TYPE_AFTERNOON -> AFTERNOON_NOTIFICATION_ID
            TYPE_EVENING -> EVENING_NOTIFICATION_ID
            else -> MORNING_NOTIFICATION_ID
        }
        
        notificationManager.notify(notificationId, notification)
    }
    
    companion object {
        const val CHANNEL_ID = "daily_verse_channel"
        const val KEY_VERSE_TYPE = "verse_type"
        const val TYPE_MORNING = "morning"
        const val TYPE_AFTERNOON = "afternoon"
        const val TYPE_EVENING = "evening"
        
        private const val MORNING_NOTIFICATION_ID = 1001
        private const val AFTERNOON_NOTIFICATION_ID = 1002
        private const val EVENING_NOTIFICATION_ID = 1003
        
        fun scheduleDailyNotifications(context: Context) {
            val workManager = WorkManager.getInstance(context)
            
            // Cancel existing work
            workManager.cancelAllWorkByTag("daily_verse")
            
            // Schedule morning notification (8:00 AM)
            scheduleNotification(context, TYPE_MORNING, 8, 0)
            
            // Schedule afternoon notification (12:00 PM)
            scheduleNotification(context, TYPE_AFTERNOON, 12, 0)
            
            // Schedule evening notification (8:00 PM)
            scheduleNotification(context, TYPE_EVENING, 20, 0)
        }
        
        private fun scheduleNotification(context: Context, type: String, hour: Int, minute: Int) {
            val inputData = Data.Builder()
                .putString(KEY_VERSE_TYPE, type)
                .build()
            
            val currentTime = Calendar.getInstance()
            val targetTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            // If the time has passed today, schedule for tomorrow
            if (targetTime.before(currentTime)) {
                targetTime.add(Calendar.DAY_OF_YEAR, 1)
            }
            
            val delay = targetTime.timeInMillis - currentTime.timeInMillis
            
            val workRequest = OneTimeWorkRequestBuilder<DailyVerseWorker>()
                .setInputData(inputData)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("daily_verse")
                .addTag("daily_verse_$type")
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
            
            WorkManager.getInstance(context).enqueue(workRequest)
            
            // Schedule the next occurrence (tomorrow)
            val nextDayRequest = PeriodicWorkRequestBuilder<DailyVerseWorker>(24, TimeUnit.HOURS)
                .setInputData(inputData)
                .setInitialDelay(delay + TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS)
                .addTag("daily_verse")
                .addTag("daily_verse_$type")
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
            
            WorkManager.getInstance(context).enqueue(nextDayRequest)
        }
        
        fun cancelDailyNotifications(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("daily_verse")
        }
    }
}