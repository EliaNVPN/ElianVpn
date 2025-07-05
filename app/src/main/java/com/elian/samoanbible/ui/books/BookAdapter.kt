package com.elian.samoanbible.ui.books

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elian.samoanbible.R
import com.elian.samoanbible.data.dao.BookInfo
import com.elian.samoanbible.databinding.ItemBookBinding

class BookAdapter(
    private val onBookClick: (BookInfo) -> Unit
) : ListAdapter<BookInfo, BookAdapter.BookViewHolder>(BookDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class BookViewHolder(
        private val binding: ItemBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(book: BookInfo) {
            binding.apply {
                bookName.text = book.cleanBookName
                
                // Set book icon color based on testament
                val iconColor = if (book.isOldTestament) {
                    ContextCompat.getColor(root.context, R.color.old_testament_primary)
                } else {
                    ContextCompat.getColor(root.context, R.color.new_testament_primary)
                }
                bookIcon.setColorFilter(iconColor)
                
                // Set book info (you can customize this to show chapter count, etc.)
                bookInfo.text = if (book.isOldTestament) {
                    "Old Testament"
                } else {
                    "New Testament"
                }
                
                // Set click listener
                root.setOnClickListener {
                    onBookClick(book)
                }
            }
        }
    }
    
    private class BookDiffCallback : DiffUtil.ItemCallback<BookInfo>() {
        override fun areItemsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
            return oldItem.bookNumber == newItem.bookNumber
        }
        
        override fun areContentsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
            return oldItem == newItem
        }
    }
}