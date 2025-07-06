package com.elian.samoanbible.ui.versetext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.elian.samoanbible.SamoanBibleApplication
import com.elian.samoanbible.databinding.FragmentVerseTextBinding

class VerseTextFragment : Fragment() {
    
    private var _binding: FragmentVerseTextBinding? = null
    private val binding get() = _binding!!
    
    private val args: VerseTextFragmentArgs by navArgs()
    private lateinit var verseTextViewModel: VerseTextViewModel
    private lateinit var verseTextAdapter: VerseTextAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerseTextBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupViewPager()
        setupObservers()
        setupClickListeners()
        
        // Set chapter title
        binding.textChapterTitle.text = "${args.bookName} ${args.chapterNumber}"
        
        // Load verses for the chapter
        verseTextViewModel.loadVerses(args.bookNumber, args.chapterNumber)
    }
    
    private fun setupViewModel() {
        val application = requireActivity().application as SamoanBibleApplication
        val repository = application.bibleRepository
        
        verseTextViewModel = ViewModelProvider(
            this,
            VerseTextViewModelFactory(repository)
        )[VerseTextViewModel::class.java]
    }
    
    private fun setupViewPager() {
        verseTextAdapter = VerseTextAdapter()
        binding.viewPagerVerses.adapter = verseTextAdapter
        
        // Set up page change callback to update verse number
        binding.viewPagerVerses.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val currentVerse = verseTextAdapter.getVerseAt(position)
                currentVerse?.let { verse ->
                    binding.textVerseNumber.text = "Verse ${verse.verse}"
                    verseTextViewModel.setCurrentVerse(verse)
                }
            }
        })
    }
    
    private fun setupObservers() {
        verseTextViewModel.verses.observe(viewLifecycleOwner) { verses ->
            verseTextAdapter.submitList(verses)
            
            // Navigate to the specific verse if provided
            val targetVerseIndex = verses.indexOfFirst { it.verse == args.verseNumber }
            if (targetVerseIndex >= 0) {
                binding.viewPagerVerses.setCurrentItem(targetVerseIndex, false)
            }
        }
        
        verseTextViewModel.currentVerse.observe(viewLifecycleOwner) { currentVerse ->
            currentVerse?.let { verse ->
                binding.textVerseNumber.text = "Verse ${verse.verse}"
            }
        }
        
        verseTextViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonShare.setOnClickListener {
            shareCurrentVerse()
        }
        
        binding.buttonCopy.setOnClickListener {
            copyCurrentVerse()
        }
    }
    
    private fun shareCurrentVerse() {
        val currentVerse = verseTextViewModel.currentVerse.value
        currentVerse?.let { verse ->
            val shareText = verse.getFormattedText()
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share Verse"))
        }
    }
    
    private fun copyCurrentVerse() {
        val currentVerse = verseTextViewModel.currentVerse.value
        currentVerse?.let { verse ->
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Bible Verse", verse.getFormattedText())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Verse copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}