package com.elian.samoanbible.ui.chapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elian.samoanbible.data.repository.BibleRepository
import kotlinx.coroutines.launch

class ChaptersViewModel(private val repository: BibleRepository) : ViewModel() {
    
    private val _chapters = MutableLiveData<List<Int>>()
    val chapters: LiveData<List<Int>> = _chapters
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadChapters(bookNumber: Int) {
        _isLoading.value = true
        _error.value = null
        
        repository.getChaptersForBook(bookNumber).observeForever { chapterList ->
            _chapters.value = chapterList
            _isLoading.value = false
        }
    }
    
    fun getChapterCount(bookNumber: Int) {
        viewModelScope.launch {
            try {
                val count = repository.getChapterCountForBook(bookNumber)
                _chapters.value = (1..count).toList()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class ChaptersViewModelFactory(
    private val repository: BibleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChaptersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChaptersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}