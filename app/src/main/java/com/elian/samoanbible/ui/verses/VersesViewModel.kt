package com.elian.samoanbible.ui.verses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elian.samoanbible.data.repository.BibleRepository
import com.elian.samoanbible.data.repository.BilingualVerse
import kotlinx.coroutines.launch

class VersesViewModel(private val repository: BibleRepository) : ViewModel() {
    
    private val _verses = MutableLiveData<List<BilingualVerse>>()
    val verses: LiveData<List<BilingualVerse>> = _verses
    
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
}

class VersesViewModelFactory(
    private val repository: BibleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VersesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VersesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}