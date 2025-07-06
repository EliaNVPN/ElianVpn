package com.elian.samoanbible.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.elian.samoanbible.R
import com.elian.samoanbible.databinding.FragmentBooksBinding
import com.google.android.material.tabs.TabLayoutMediator

class BooksFragment : Fragment() {
    
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewPager()
    }
    
    private fun setupViewPager() {
        val adapter = BooksViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.all_books)
                1 -> getString(R.string.old_testament)
                2 -> getString(R.string.new_testament)
                else -> ""
            }
        }.attach()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private class BooksViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BookListFragment.newInstance(BookListFragment.BookType.ALL)
                1 -> BookListFragment.newInstance(BookListFragment.BookType.OLD_TESTAMENT)
                2 -> BookListFragment.newInstance(BookListFragment.BookType.NEW_TESTAMENT)
                else -> BookListFragment.newInstance(BookListFragment.BookType.ALL)
            }
        }
    }
}