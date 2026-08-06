package com.hackfire.hftube.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.hackfire.hftube.R
import com.hackfire.hftube.databinding.FragmentSearchHomeBinding
import com.hackfire.hftube.ui.formatpicker.FormatPickerActivity

/**
 * Home screen: top-tab row (Search / YouTube / Music / More / Sub), centered
 * wordmark, and the pill search bar (leading download-arrow icon, trailing
 * circular accent search button). A pasted link or typed query is handed off
 * to the extraction backend — see submitQuery().
 */
class SearchHomeFragment : Fragment() {

    private var _binding: FragmentSearchHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTopTabs()
        setUpSearchBar()
    }

    private fun setUpTopTabs() {
        val labels = listOf(
            R.string.home_tab_search,
            R.string.home_tab_youtube,
            R.string.home_tab_music,
            R.string.home_tab_more,
            R.string.home_tab_sub
        )
        labels.forEach { resId ->
            binding.topTabs.addTab(binding.topTabs.newTab().setText(resId))
        }
        binding.topTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // TODO: swap the body content per tab (Search stays this pill
                // screen; YouTube/Music/More/Sub get their own child views —
                // YouTube's is the WebView from a later build step).
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setUpSearchBar() {
        binding.searchButton.setOnClickListener { submitQuery() }
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery()
                true
            } else {
                false
            }
        }
    }

    private fun submitQuery() {
        val query = binding.searchInput.text?.toString()?.trim().orEmpty()
        if (query.isEmpty()) return
        val intent = Intent(requireContext(), FormatPickerActivity::class.java)
        intent.putExtra(FormatPickerActivity.EXTRA_QUERY, query)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
