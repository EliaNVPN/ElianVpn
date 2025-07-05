package com.elian.samoanbible.ui.books

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elian.samoanbible.data.model.BookInfo
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
    
    inner class BookViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onBookClick(getItem(position))
                }
            }
        }
        
        fun bind(book: BookInfo) {
            with(binding) {
                textViewBookName.text = book.cleanBookName
                textViewBookNumber.text = book.bookNumber.toString()
                
                // You can add more details here like chapter count, etc.
                textViewBookTestament.text = if (book.isOldTestament) "Old Testament" else "New Testament"
            }
        }
    }
    
    class BookDiffCallback : DiffUtil.ItemCallback<BookInfo>() {
        override fun areItemsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
            return oldItem.bookNumber == newItem.bookNumber
        }
        
        override fun areContentsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
            return oldItem == newItem
        }
    }
}