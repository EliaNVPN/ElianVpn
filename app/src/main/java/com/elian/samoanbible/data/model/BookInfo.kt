package com.elian.samoanbible.data.model

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