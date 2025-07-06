package com.elian.samoanbible.ui.verses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elian.samoanbible.data.repository.BilingualVerse
import com.elian.samoanbible.databinding.ItemVerseBinding

class VerseAdapter(
    private val onVerseClick: (BilingualVerse) -> Unit
) : ListAdapter<BilingualVerse, VerseAdapter.VerseViewHolder>(VerseDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseViewHolder {
        val binding = ItemVerseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VerseViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: VerseViewHolder, position: Int) {
        val verse = getItem(position)
        holder.bind(verse)
    }
    
    inner class VerseViewHolder(
        private val binding: ItemVerseBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(bilingualVerse: BilingualVerse) {
            binding.textVerseNumber.text = bilingualVerse.verse.toString()
            binding.textSamoanVerse.text = bilingualVerse.samoanText
            
            // Show English text if available
            if (bilingualVerse.hasEnglishTranslation) {
                binding.textEnglishVerse.text = bilingualVerse.englishText
            } else {
                binding.textEnglishVerse.text = ""
            }
            
            binding.root.setOnClickListener {
                onVerseClick(bilingualVerse)
            }
        }
    }
}

class VerseDiffCallback : DiffUtil.ItemCallback<BilingualVerse>() {
    override fun areItemsTheSame(oldItem: BilingualVerse, newItem: BilingualVerse): Boolean {
        return oldItem.bookNumber == newItem.bookNumber &&
                oldItem.chapter == newItem.chapter &&
                oldItem.verse == newItem.verse
    }
    
    override fun areContentsTheSame(oldItem: BilingualVerse, newItem: BilingualVerse): Boolean {
        return oldItem == newItem
    }
}