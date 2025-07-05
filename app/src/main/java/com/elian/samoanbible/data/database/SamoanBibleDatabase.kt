package com.elian.samoanbible.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.elian.samoanbible.data.dao.VerseDao
import com.elian.samoanbible.data.entity.Verse
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Database(
    entities = [Verse::class],
    version = 1,
    exportSchema = false
)
abstract class SamoanBibleDatabase : RoomDatabase() {
    
    abstract fun verseDao(): VerseDao
    
    companion object {
        @Volatile
        private var INSTANCE: SamoanBibleDatabase? = null
        
        private const val DATABASE_NAME = "samoan_bible.db"
        
        fun getDatabase(context: Context): SamoanBibleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SamoanBibleDatabase::class.java,
                    DATABASE_NAME
                )
                    .createFromAsset("databases/samoan_bible.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Copy database from assets to internal storage
         */
        fun copyDatabaseFromAssets(context: Context) {
            val dbPath = context.getDatabasePath(DATABASE_NAME)
            if (!dbPath.exists()) {
                try {
                    // Create the directories if they don't exist
                    dbPath.parentFile?.mkdirs()
                    
                    // Copy the database from assets
                    context.assets.open("databases/$DATABASE_NAME").use { input ->
                        FileOutputStream(dbPath).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}

// Extension class for English Bible database
@Database(
    entities = [Verse::class],
    version = 1,
    exportSchema = false
)
abstract class EnglishBibleDatabase : RoomDatabase() {
    
    abstract fun verseDao(): VerseDao
    
    companion object {
        @Volatile
        private var INSTANCE: EnglishBibleDatabase? = null
        
        private const val DATABASE_NAME = "english_bible.db"
        
        fun getDatabase(context: Context): EnglishBibleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EnglishBibleDatabase::class.java,
                    DATABASE_NAME
                )
                    .createFromAsset("databases/english_bible.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Copy database from assets to internal storage
         */
        fun copyDatabaseFromAssets(context: Context) {
            val dbPath = context.getDatabasePath(DATABASE_NAME)
            if (!dbPath.exists()) {
                try {
                    // Create the directories if they don't exist
                    dbPath.parentFile?.mkdirs()
                    
                    // Copy the database from assets
                    context.assets.open("databases/$DATABASE_NAME").use { input ->
                        FileOutputStream(dbPath).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}