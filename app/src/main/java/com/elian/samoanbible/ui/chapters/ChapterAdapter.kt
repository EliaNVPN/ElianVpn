package com.elian.samoanbible.ui.chapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elian.samoanbible.databinding.ItemChapterBinding

class ChapterAdapter(
    private val onChapterClick: (Int) -> Unit
) : ListAdapter<Int, ChapterAdapter.ChapterViewHolder>(ChapterDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = ItemChapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChapterViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = getItem(position)
        holder.bind(chapter)
    }
    
    inner class ChapterViewHolder(
        private val binding: ItemChapterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(chapter: Int) {
            binding.textChapterNumber.text = chapter.toString()
            
            binding.root.setOnClickListener {
                onChapterClick(chapter)
            }
        }
    }
}

class ChapterDiffCallback : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }
    
    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }
}