package com.elian.samoanbible.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verses")
data class Verse(
    @PrimaryKey
    val id: Int,
    val bookNumber: Int,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val highlight: String = "",
    val title: String = "",
    val favourite: String = "",
    val date: String = "",
    val intro: String = ""
) {
    // Helper property to get clean book name without punctuation
    val cleanBookName: String
        get() = bookName.replace(",", "").trim()
    
    // Helper property to get verse reference
    val reference: String
        get() = "$cleanBookName $chapter:$verse"
    
    // Helper property to get clean text without HTML tags
    val cleanText: String
        get() = text.replace(Regex("<[^>]*>"), "").trim()
    
    // Helper property to check if verse is highlighted
    val isHighlighted: Boolean
        get() = highlight.isNotEmpty()
    
    // Helper property to check if verse is favourite
    val isFavourite: Boolean
        get() = favourite.isNotEmpty()
}