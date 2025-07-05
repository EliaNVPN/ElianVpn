package com.elian.samoanbible.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.elian.samoanbible.SamoanBibleApplication
import com.elian.samoanbible.data.dao.BookInfo
import com.elian.samoanbible.databinding.FragmentBookListBinding
import com.google.android.material.snackbar.Snackbar

class BookListFragment : Fragment() {
    
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var bookAdapter: BookAdapter
    private val viewModel: BooksViewModel by viewModels {
        BooksViewModelFactory(SamoanBibleApplication.getRepository(requireContext()))
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
        
        setupRecyclerView()
        setupObservers()
    }
    
    private fun setupRecyclerView() {
        bookAdapter = BookAdapter { book ->
            navigateToChapters(book)
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }
    
    private fun setupObservers() {
        val bookType = arguments?.getSerializable(ARG_BOOK_TYPE) as? BookType ?: BookType.ALL_BOOKS
        
        when (bookType) {
            BookType.ALL_BOOKS -> {
                viewModel.allBooks.observe(viewLifecycleOwner) { books ->
                    updateBookList(books)
                }
            }
            BookType.OLD_TESTAMENT -> {
                viewModel.oldTestamentBooks.observe(viewLifecycleOwner) { books ->
                    updateBookList(books)
                }
            }
            BookType.NEW_TESTAMENT -> {
                viewModel.newTestamentBooks.observe(viewLifecycleOwner) { books ->
                    updateBookList(books)
                }
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        }
    }
    
    private fun updateBookList(books: List<BookInfo>?) {
        books?.let {
            if (it.isEmpty()) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                bookAdapter.submitList(it)
            }
        }
    }
    
    private fun navigateToChapters(book: BookInfo) {
        val action = BooksFragmentDirections.actionBooksToChapters(
            bookNumber = book.bookNumber,
            bookName = book.cleanBookName
        )
        findNavController().navigate(action)
    }
    
    private fun showError(error: String) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    enum class BookType {
        ALL_BOOKS,
        OLD_TESTAMENT,
        NEW_TESTAMENT
    }
    
    companion object {
        private const val ARG_BOOK_TYPE = "book_type"
        
        fun newInstance(bookType: BookType): BookListFragment {
            return BookListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BOOK_TYPE, bookType)
                }
            }
        }
    }
}