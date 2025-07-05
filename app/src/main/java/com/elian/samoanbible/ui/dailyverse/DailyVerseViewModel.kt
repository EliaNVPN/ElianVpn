package com.elian.samoanbible.ui.dailyverse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elian.samoanbible.data.repository.BibleRepository
import com.elian.samoanbible.data.repository.BilingualVerse
import kotlinx.coroutines.launch

class DailyVerseViewModel(private val repository: BibleRepository) : ViewModel() {
    
    private val _dailyVerse = MutableLiveData<BilingualVerse?>()
    val dailyVerse: LiveData<BilingualVerse?> = _dailyVerse
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        loadDailyVerse()
    }
    
    private fun loadDailyVerse() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val verse = repository.getDailyVerse()
                _dailyVerse.value = verse
                
            } catch (e: Exception) {
                _error.value = "Error loading daily verse: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshDailyVerse() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val verse = repository.getRandomVerse()
                _dailyVerse.value = verse
                
            } catch (e: Exception) {
                _error.value = "Error refreshing verse: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

class DailyVerseViewModelFactory(private val repository: BibleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyVerseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyVerseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}