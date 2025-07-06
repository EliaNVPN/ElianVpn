package com.elian.samoanbible.ui.versetext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elian.samoanbible.data.repository.BibleRepository
import com.elian.samoanbible.data.repository.BilingualVerse

class VerseTextViewModel(private val repository: BibleRepository) : ViewModel() {
    
    private val _verses = MutableLiveData<List<BilingualVerse>>()
    val verses: LiveData<List<BilingualVerse>> = _verses
    
    private val _currentVerse = MutableLiveData<BilingualVerse?>()
    val currentVerse: LiveData<BilingualVerse?> = _currentVerse
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadVerses(bookNumber: Int, chapterNumber: Int) {
        _isLoading.value = true
        _error.value = null
        
        repository.getVersesForChapter(bookNumber, chapterNumber).observeForever { verseList ->
            _verses.value = verseList
            _isLoading.value = false
        }
    }
    
    fun setCurrentVerse(verse: BilingualVerse) {
        _currentVerse.value = verse
    }
    
    fun getCurrentVersePosition(): Int {
        val currentVerse = _currentVerse.value
        val verses = _verses.value
        
        return if (currentVerse != null && verses != null) {
            verses.indexOfFirst { it.verse == currentVerse.verse }
        } else {
            0
        }
    }
    
    fun getVerseAt(position: Int): BilingualVerse? {
        val verses = _verses.value
        return if (verses != null && position in verses.indices) {
            verses[position]
        } else {
            null
        }
    }
}

class VerseTextViewModelFactory(
    private val repository: BibleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerseTextViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VerseTextViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}