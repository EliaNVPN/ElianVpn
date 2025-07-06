package com.elian.samoanbible.ui.verses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.elian.samoanbible.SamoanBibleApplication
import com.elian.samoanbible.databinding.FragmentVersesBinding

class VersesFragment : Fragment() {
    
    private var _binding: FragmentVersesBinding? = null
    private val binding get() = _binding!!
    
    private val args: VersesFragmentArgs by navArgs()
    private lateinit var versesViewModel: VersesViewModel
    private lateinit var verseAdapter: VerseAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVersesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        
        // Set chapter title
        binding.textChapterTitle.text = "${args.bookName} ${args.chapterNumber}"
        
        // Load verses for the selected chapter
        versesViewModel.loadVerses(args.bookNumber, args.chapterNumber)
    }
    
    private fun setupViewModel() {
        val application = requireActivity().application as SamoanBibleApplication
        val repository = application.bibleRepository
        
        versesViewModel = ViewModelProvider(
            this,
            VersesViewModelFactory(repository)
        )[VersesViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        verseAdapter = VerseAdapter { bilingualVerse ->
            // Navigate to verse text fragment
            val action = VersesFragmentDirections.actionVersesToVerseText(
                bookNumber = args.bookNumber,
                bookName = args.bookName,
                chapterNumber = args.chapterNumber,
                verseNumber = bilingualVerse.verse
            )
            findNavController().navigate(action)
        }
        
        binding.recyclerViewVerses.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = verseAdapter
        }
    }
    
    private fun setupObservers() {
        versesViewModel.verses.observe(viewLifecycleOwner) { verses ->
            verseAdapter.submitList(verses)
        }
        
        versesViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        versesViewModel.error.observe(viewLifecycleOwner) { error ->
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