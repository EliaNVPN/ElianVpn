package com.elian.samoanbible.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.elian.samoanbible.data.entity.Verse

@Dao
interface VerseDao {
    
    // Get all books
    @Query("SELECT DISTINCT bookNumber, bookName FROM verses ORDER BY bookNumber ASC")
    fun getAllBooks(): LiveData<List<BookInfo>>
    
    // Get Old Testament books (books 1-39)
    @Query("SELECT DISTINCT bookNumber, bookName FROM verses WHERE bookNumber <= 39 ORDER BY bookNumber ASC")
    fun getOldTestamentBooks(): LiveData<List<BookInfo>>
    
    // Get New Testament books (books 40-66)
    @Query("SELECT DISTINCT bookNumber, bookName FROM verses WHERE bookNumber >= 40 ORDER BY bookNumber ASC")
    fun getNewTestamentBooks(): LiveData<List<BookInfo>>
    
    // Get chapters for a specific book
    @Query("SELECT DISTINCT chapter FROM verses WHERE bookNumber = :bookNumber ORDER BY chapter ASC")
    fun getChaptersForBook(bookNumber: Int): LiveData<List<Int>>
    
    // Get verses for a specific chapter
    @Query("SELECT * FROM verses WHERE bookNumber = :bookNumber AND chapter = :chapter ORDER BY verse ASC")
    fun getVersesForChapter(bookNumber: Int, chapter: Int): LiveData<List<Verse>>
    
    // Get a specific verse
    @Query("SELECT * FROM verses WHERE bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse")
    fun getVerse(bookNumber: Int, chapter: Int, verse: Int): LiveData<Verse?>
    
    // Get random verse for daily verse
    @Query("SELECT * FROM verses ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomVerse(): Verse?
    
    // Get multiple random verses for daily notifications
    @Query("SELECT * FROM verses ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomVerses(count: Int): List<Verse>
    
    // Search verses by text
    @Query("SELECT * FROM verses WHERE text LIKE '%' || :searchQuery || '%' ORDER BY bookNumber, chapter, verse")
    fun searchVerses(searchQuery: String): LiveData<List<Verse>>
    
    // Get favourite verses
    @Query("SELECT * FROM verses WHERE favourite != '' ORDER BY bookNumber, chapter, verse")
    fun getFavouriteVerses(): LiveData<List<Verse>>
    
    // Get highlighted verses
    @Query("SELECT * FROM verses WHERE highlight != '' ORDER BY bookNumber, chapter, verse")
    fun getHighlightedVerses(): LiveData<List<Verse>>
    
    // Get verse count for a book
    @Query("SELECT COUNT(*) FROM verses WHERE bookNumber = :bookNumber")
    suspend fun getVerseCountForBook(bookNumber: Int): Int
    
    // Get chapter count for a book
    @Query("SELECT COUNT(DISTINCT chapter) FROM verses WHERE bookNumber = :bookNumber")
    suspend fun getChapterCountForBook(bookNumber: Int): Int
    
    // Get verse count for a chapter
    @Query("SELECT COUNT(*) FROM verses WHERE bookNumber = :bookNumber AND chapter = :chapter")
    suspend fun getVerseCountForChapter(bookNumber: Int, chapter: Int): Int
    
    // Get book info by book number
    @Query("SELECT DISTINCT bookNumber, bookName FROM verses WHERE bookNumber = :bookNumber")
    suspend fun getBookInfo(bookNumber: Int): BookInfo?
    
    // Get verses for a specific book
    @Query("SELECT * FROM verses WHERE bookNumber = :bookNumber ORDER BY chapter, verse")
    fun getVersesForBook(bookNumber: Int): LiveData<List<Verse>>
    
    // Get total verse count
    @Query("SELECT COUNT(*) FROM verses")
    suspend fun getTotalVerseCount(): Int
    
    // Get total book count
    @Query("SELECT COUNT(DISTINCT bookNumber) FROM verses")
    suspend fun getTotalBookCount(): Int
    
    // Get verses for specific day (for daily verse feature)
    @Query("SELECT * FROM verses WHERE (julianday('now') - julianday('2024-01-01')) % (SELECT COUNT(*) FROM verses) = (id % (SELECT COUNT(*) FROM verses)) ORDER BY RANDOM() LIMIT 1")
    suspend fun getDailyVerse(): Verse?
    
    // Get verses by reference (for sharing)
    @Query("SELECT * FROM verses WHERE bookNumber = :bookNumber AND chapter = :chapter AND verse = :verseNumber")
    suspend fun getVerseByReference(bookNumber: Int, chapter: Int, verseNumber: Int): Verse?
}

// Data class for book information
data class BookInfo(
    val bookNumber: Int,
    val bookName: String
) {
    val cleanBookName: String
        get() = bookName.replace(",", "").trim()
        
    val isOldTestament: Boolean
        get() = bookNumber <= 39
        
    val isNewTestament: Boolean
        get() = bookNumber >= 40
}