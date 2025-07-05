package com.elian.samoanbible.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.elian.samoanbible.data.dao.VerseDao
import com.elian.samoanbible.data.entity.Verse
import com.elian.samoanbible.data.model.BookInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BibleRepository(
    private val samoanVerseDao: VerseDao,
    private val englishVerseDao: VerseDao
) {
    
    // Book operations - map Verse to BookInfo
    fun getAllBooks(): LiveData<List<BookInfo>> = 
        samoanVerseDao.getAllBooks().map { verses ->
            verses.map { verse -> BookInfo(verse.bookNumber, verse.bookName) }
        }
    
    fun getOldTestamentBooks(): LiveData<List<BookInfo>> = 
        samoanVerseDao.getOldTestamentBooks().map { verses ->
            verses.map { verse -> BookInfo(verse.bookNumber, verse.bookName) }
        }
    
    fun getNewTestamentBooks(): LiveData<List<BookInfo>> = 
        samoanVerseDao.getNewTestamentBooks().map { verses ->
            verses.map { verse -> BookInfo(verse.bookNumber, verse.bookName) }
        }
    
    // Chapter operations
    fun getChaptersForBook(bookNumber: Int): LiveData<List<Int>> = 
        samoanVerseDao.getChaptersForBook(bookNumber)
    
    // Verse operations - combining both languages
    fun getVersesForChapter(bookNumber: Int, chapter: Int): LiveData<List<BilingualVerse>> {
        val result = MediatorLiveData<List<BilingualVerse>>()
        val samoanVerses = samoanVerseDao.getVersesForChapter(bookNumber, chapter)
        val englishVerses = englishVerseDao.getVersesForChapter(bookNumber, chapter)
        
        result.addSource(samoanVerses) { samoan ->
            val english = englishVerses.value
            result.value = combineVerses(samoan, english)
        }
        
        result.addSource(englishVerses) { english ->
            val samoan = samoanVerses.value
            result.value = combineVerses(samoan, english)
        }
        
        return result
    }
    
    // Get specific verse in both languages
    fun getVerse(bookNumber: Int, chapter: Int, verse: Int): LiveData<BilingualVerse?> {
        val result = MediatorLiveData<BilingualVerse?>()
        val samoanVerse = samoanVerseDao.getVerse(bookNumber, chapter, verse)
        val englishVerse = englishVerseDao.getVerse(bookNumber, chapter, verse)
        
        result.addSource(samoanVerse) { samoan ->
            val english = englishVerse.value
            result.value = if (samoan != null) {
                BilingualVerse(samoan, english)
            } else null
        }
        
        result.addSource(englishVerse) { english ->
            val samoan = samoanVerse.value
            result.value = if (samoan != null) {
                BilingualVerse(samoan, english)
            } else null
        }
        
        return result
    }
    
    // Daily verse operations
    suspend fun getDailyVerse(): BilingualVerse? = withContext(Dispatchers.IO) {
        val samoanVerse = samoanVerseDao.getDailyVerse()
        val englishVerse = samoanVerse?.let { 
            englishVerseDao.getVerseByReference(it.bookNumber, it.chapter, it.verse)
        }
        
        samoanVerse?.let { BilingualVerse(it, englishVerse) }
    }
    
    suspend fun getRandomVerse(): BilingualVerse? = withContext(Dispatchers.IO) {
        val samoanVerse = samoanVerseDao.getRandomVerse()
        val englishVerse = samoanVerse?.let { 
            englishVerseDao.getVerseByReference(it.bookNumber, it.chapter, it.verse)
        }
        
        samoanVerse?.let { BilingualVerse(it, englishVerse) }
    }
    
    suspend fun getRandomVerses(count: Int): List<BilingualVerse> = withContext(Dispatchers.IO) {
        val samoanVerses = samoanVerseDao.getRandomVerses(count)
        samoanVerses.map { samoanVerse ->
            val englishVerse = englishVerseDao.getVerseByReference(
                samoanVerse.bookNumber, 
                samoanVerse.chapter, 
                samoanVerse.verse
            )
            BilingualVerse(samoanVerse, englishVerse)
        }
    }
    
    // Search operations
    fun searchVerses(query: String): LiveData<List<BilingualVerse>> {
        val result = MediatorLiveData<List<BilingualVerse>>()
        val samoanResults = samoanVerseDao.searchVerses(query)
        
        result.addSource(samoanResults) { samoanVerses ->
            // For search results, we'll load English verses on-demand
            result.value = samoanVerses.map { BilingualVerse(it, null) }
        }
        
        return result
    }
    
    // Statistics
    suspend fun getBookInfo(bookNumber: Int): BookInfo? = withContext(Dispatchers.IO) {
        val verse = samoanVerseDao.getBookInfo(bookNumber)
        verse?.let { BookInfo(it.bookNumber, it.bookName) }
    }
    
    suspend fun getChapterCountForBook(bookNumber: Int): Int = withContext(Dispatchers.IO) {
        samoanVerseDao.getChapterCountForBook(bookNumber)
    }
    
    suspend fun getVerseCountForChapter(bookNumber: Int, chapter: Int): Int = withContext(Dispatchers.IO) {
        samoanVerseDao.getVerseCountForChapter(bookNumber, chapter)
    }
    
    suspend fun getTotalBookCount(): Int = withContext(Dispatchers.IO) {
        samoanVerseDao.getTotalBookCount()
    }
    
    suspend fun getTotalVerseCount(): Int = withContext(Dispatchers.IO) {
        samoanVerseDao.getTotalVerseCount()
    }
    
    // Helper function to combine verses from both languages
    private fun combineVerses(samoanVerses: List<Verse>?, englishVerses: List<Verse>?): List<BilingualVerse> {
        if (samoanVerses == null) return emptyList()
        
        val englishMap = englishVerses?.associateBy { "${it.bookNumber}-${it.chapter}-${it.verse}" } ?: emptyMap()
        
        return samoanVerses.map { samoanVerse ->
            val key = "${samoanVerse.bookNumber}-${samoanVerse.chapter}-${samoanVerse.verse}"
            val englishVerse = englishMap[key]
            BilingualVerse(samoanVerse, englishVerse)
        }
    }
}

// Data class representing a verse in both languages
data class BilingualVerse(
    val samoanVerse: Verse,
    val englishVerse: Verse?
) {
    val bookNumber: Int get() = samoanVerse.bookNumber
    val bookName: String get() = samoanVerse.bookName
    val chapter: Int get() = samoanVerse.chapter
    val verse: Int get() = samoanVerse.verse
    val reference: String get() = samoanVerse.reference
    
    val samoanText: String get() = samoanVerse.cleanText
    val englishText: String get() = englishVerse?.cleanText ?: ""
    
    val hasEnglishTranslation: Boolean get() = englishVerse != null
    
    // Helper function to get formatted text for sharing
    fun getFormattedText(): String {
        val samoan = samoanText
        val english = if (hasEnglishTranslation) "\n\n$englishText" else ""
        return "$samoan$english\n\n- $reference"
    }
    
    // Helper function to get text for notifications
    fun getNotificationText(): String {
        return if (hasEnglishTranslation) {
            "$samoanText\n\n$englishText"
        } else {
            samoanText
        }
    }
}