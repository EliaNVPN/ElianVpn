package com.elian.samoanbible.ui.dailyverse

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.elian.samoanbible.R
import com.elian.samoanbible.SamoanBibleApplication
import com.elian.samoanbible.databinding.FragmentDailyVerseBinding
import com.elian.samoanbible.data.repository.BilingualVerse
import com.google.android.material.snackbar.Snackbar

class DailyVerseFragment : Fragment() {
    
    private var _binding: FragmentDailyVerseBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DailyVerseViewModel by viewModels {
        DailyVerseViewModelFactory(SamoanBibleApplication.getRepository(requireContext()))
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyVerseBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        // Observe daily verse
        viewModel.dailyVerse.observe(viewLifecycleOwner) { verse ->
            verse?.let {
                displayVerse(it)
            }
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.dailyVerseCard.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        
        // Observe error state
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        }
    }
    
    private fun setupClickListeners() {
        // Copy button
        binding.copyButton.setOnClickListener {
            val verse = viewModel.dailyVerse.value
            verse?.let {
                copyVerseToClipboard(it)
            }
        }
        
        // Share button
        binding.shareButton.setOnClickListener {
            val verse = viewModel.dailyVerse.value
            verse?.let {
                shareVerse(it)
            }
        }
        
        // Browse books button
        binding.browseBooksButton.setOnClickListener {
            findNavController().navigate(R.id.booksFragment)
        }
        
        // Refresh on card click
        binding.dailyVerseCard.setOnClickListener {
            viewModel.refreshDailyVerse()
        }
    }
    
    private fun displayVerse(verse: BilingualVerse) {
        binding.apply {
            verseReference.text = verse.reference
            samoanVerseText.text = verse.samoanText
            
            if (verse.hasEnglishTranslation) {
                englishVerseText.text = verse.englishText
                englishVerseContainer.visibility = View.VISIBLE
            } else {
                englishVerseContainer.visibility = View.GONE
            }
        }
    }
    
    private fun copyVerseToClipboard(verse: BilingualVerse) {
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Bible Verse", verse.getFormattedText())
        clipboardManager.setPrimaryClip(clipData)
        
        Snackbar.make(
            binding.root,
            getString(R.string.verse_copied),
            Snackbar.LENGTH_SHORT
        ).show()
    }
    
    private fun shareVerse(verse: BilingualVerse) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, verse.getFormattedText())
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_verse_title))
        }
        
        val chooser = Intent.createChooser(shareIntent, getString(R.string.share_verse_title))
        startActivity(chooser)
    }
    
    private fun showError(error: String) {
        Snackbar.make(
            binding.root,
            error,
            Snackbar.LENGTH_LONG
        ).setAction("Retry") {
            viewModel.refreshDailyVerse()
        }.show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}