package com.elian.samoanbible.ui.chapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.elian.samoanbible.SamoanBibleApplication
import com.elian.samoanbible.databinding.FragmentChaptersBinding

class ChaptersFragment : Fragment() {
    
    private var _binding: FragmentChaptersBinding? = null
    private val binding get() = _binding!!
    
    private val args: ChaptersFragmentArgs by navArgs()
    private lateinit var chaptersViewModel: ChaptersViewModel
    private lateinit var chapterAdapter: ChapterAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChaptersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        
        // Set book title
        binding.textBookTitle.text = args.bookName
        
        // Load chapters for the selected book
        chaptersViewModel.loadChapters(args.bookNumber)
    }
    
    private fun setupViewModel() {
        val application = requireActivity().application as SamoanBibleApplication
        val repository = application.bibleRepository
        
        chaptersViewModel = ViewModelProvider(
            this,
            ChaptersViewModelFactory(repository)
        )[ChaptersViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        chapterAdapter = ChapterAdapter { chapterNumber ->
            // Navigate to verses fragment
            val action = ChaptersFragmentDirections.actionChaptersToVerses(
                bookNumber = args.bookNumber,
                bookName = args.bookName,
                chapterNumber = chapterNumber
            )
            findNavController().navigate(action)
        }
        
        binding.recyclerViewChapters.apply {
            layoutManager = GridLayoutManager(context, 4) // 4 columns
            adapter = chapterAdapter
        }
    }
    
    private fun setupObservers() {
        chaptersViewModel.chapters.observe(viewLifecycleOwner) { chapters ->
            chapterAdapter.submitList(chapters)
        }
        
        chaptersViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        chaptersViewModel.error.observe(viewLifecycleOwner) { error ->
            // Handle error if needed
            error?.let {
                // You can show a toast or error message here
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}