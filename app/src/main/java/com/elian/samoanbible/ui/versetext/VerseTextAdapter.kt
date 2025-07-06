package com.elian.samoanbible.ui.versetext

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elian.samoanbible.data.repository.BilingualVerse
import com.elian.samoanbible.databinding.ItemVerseTextBinding

class VerseTextAdapter : ListAdapter<BilingualVerse, VerseTextAdapter.VerseTextViewHolder>(VerseTextDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseTextViewHolder {
        val binding = ItemVerseTextBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VerseTextViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: VerseTextViewHolder, position: Int) {
        val verse = getItem(position)
        holder.bind(verse)
    }
    
    fun getVerseAt(position: Int): BilingualVerse? {
        return if (position in 0 until itemCount) {
            getItem(position)
        } else {
            null
        }
    }
    
    inner class VerseTextViewHolder(
        private val binding: ItemVerseTextBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(bilingualVerse: BilingualVerse) {
            // Set verse number
            binding.textVerseNumber.text = bilingualVerse.verse.toString()
            
            // Set Samoan text
            binding.textSamoanVerse.text = bilingualVerse.samoanText
            
            // Set English text if available
            if (bilingualVerse.hasEnglishTranslation) {
                binding.textEnglishVerse.text = bilingualVerse.englishText
                binding.textEnglishVerse.visibility = android.view.View.VISIBLE
            } else {
                binding.textEnglishVerse.visibility = android.view.View.GONE
            }
            
            // Set reference
            binding.textReference.text = bilingualVerse.reference
        }
    }
}

class VerseTextDiffCallback : DiffUtil.ItemCallback<BilingualVerse>() {
    override fun areItemsTheSame(oldItem: BilingualVerse, newItem: BilingualVerse): Boolean {
        return oldItem.bookNumber == newItem.bookNumber &&
                oldItem.chapter == newItem.chapter &&
                oldItem.verse == newItem.verse
    }
    
    override fun areContentsTheSame(oldItem: BilingualVerse, newItem: BilingualVerse): Boolean {
        return oldItem == newItem
    }
}