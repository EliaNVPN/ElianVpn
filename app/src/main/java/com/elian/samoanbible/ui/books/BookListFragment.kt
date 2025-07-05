package com.elian.samoanbible.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.elian.samoanbible.SamoanBibleApplication
import com.elian.samoanbible.data.model.BookInfo
import com.elian.samoanbible.databinding.FragmentBookListBinding

class BookListFragment : Fragment() {
    
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var booksViewModel: BooksViewModel
    private lateinit var bookAdapter: BookAdapter
    
    private var bookType: BookType = BookType.ALL
    
    enum class BookType {
        ALL, OLD_TESTAMENT, NEW_TESTAMENT
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get book type from arguments
        bookType = BookType.values()[arguments?.getInt(ARG_BOOK_TYPE, 0) ?: 0]
        
        setupViewModel()
        setupRecyclerView()
        observeBooks()
    }
    
    private fun setupViewModel() {
        val repository = (requireActivity().application as SamoanBibleApplication).repository
        booksViewModel = ViewModelProvider(
            this,
            BooksViewModelFactory(repository)
        )[BooksViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        bookAdapter = BookAdapter { book ->
            onBookSelected(book)
        }
        
        binding.recyclerViewBooks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }
    
    private fun observeBooks() {
        val booksLiveData = when (bookType) {
            BookType.ALL -> booksViewModel.allBooks
            BookType.OLD_TESTAMENT -> booksViewModel.oldTestamentBooks
            BookType.NEW_TESTAMENT -> booksViewModel.newTestamentBooks
        }
        
        booksLiveData.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books)
            
            // Show/hide empty state
            if (books.isEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerViewBooks.visibility = View.GONE
            } else {
                binding.textViewEmpty.visibility = View.GONE
                binding.recyclerViewBooks.visibility = View.VISIBLE
            }
        }
        
        // Observe loading state
        booksViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerViewBooks.visibility = View.GONE
                binding.textViewEmpty.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
        
        // Observe errors
        booksViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                // Show error message
                binding.textViewEmpty.text = "Error: $error"
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerViewBooks.visibility = View.GONE
            }
        }
    }
    
    private fun onBookSelected(book: BookInfo) {
        // TODO: Navigate to chapters fragment
        // For now, just show a toast or log
        println("Selected book: ${book.bookName} (${book.bookNumber})")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_BOOK_TYPE = "book_type"
        
        fun newInstance(bookType: BookType): BookListFragment {
            val fragment = BookListFragment()
            val args = Bundle()
            args.putInt(ARG_BOOK_TYPE, bookType.ordinal)
            fragment.arguments = args
            return fragment
        }
    }
}