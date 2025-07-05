package com.elian.samoanbible.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elian.samoanbible.data.dao.BookInfo
import com.elian.samoanbible.data.repository.BibleRepository
import kotlinx.coroutines.launch

class BooksViewModel(private val repository: BibleRepository) : ViewModel() {
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Books data
    val allBooks: LiveData<List<BookInfo>> = repository.getAllBooks()
    val oldTestamentBooks: LiveData<List<BookInfo>> = repository.getOldTestamentBooks()
    val newTestamentBooks: LiveData<List<BookInfo>> = repository.getNewTestamentBooks()
    
    // Get book statistics
    suspend fun getChapterCount(bookNumber: Int): Int {
        return try {
            repository.getChapterCountForBook(bookNumber)
        } catch (e: Exception) {
            0
        }
    }
    
    fun getBookStatistics(bookNumber: Int, callback: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val chapterCount = repository.getChapterCountForBook(bookNumber)
                val formattedText = when {
                    chapterCount == 1 -> "1 chapter"
                    chapterCount > 1 -> "$chapterCount chapters"
                    else -> "No chapters"
                }
                callback(formattedText)
            } catch (e: Exception) {
                callback("Unknown")
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

class BooksViewModelFactory(private val repository: BibleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BooksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}