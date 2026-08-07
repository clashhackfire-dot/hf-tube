package com.hackfire.hftube.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.hackfire.hftube.R
import com.hackfire.hftube.auth.CookieStore
import com.hackfire.hftube.databinding.FragmentSearchHomeBinding
import com.hackfire.hftube.ui.formatpicker.FormatPickerActivity

private const val TAB_SEARCH = 0
private const val TAB_YOUTUBE = 1

/**
 * Home screen: top-tab row (Search / YouTube / Music / More / Sub), centered
 * wordmark, and the pill search bar (leading download-arrow icon, trailing
 * circular accent search button). Search stays the pill screen; YouTube
 * swaps in a WebView carrying the signed-in session (see CookieStore) with
 * an injected per-card download icon. Music/More/Sub fall back to the
 * search body for now — no dedicated content built yet.
 */
class SearchHomeFragment : Fragment() {

    private var _binding: FragmentSearchHomeBinding? = null
    private val binding get() = _binding!!
    private var youtubeWebViewLoaded = false

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
                showTab(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun showTab(position: Int) {
        if (position == TAB_YOUTUBE) {
            binding.searchBody.visibility = View.GONE
            binding.youtubeBody.visibility = View.VISIBLE
            ensureYouTubeWebViewLoaded()
        } else {
            binding.youtubeBody.visibility = View.GONE
            binding.searchBody.visibility = View.VISIBLE
        }
    }

    private fun ensureYouTubeWebViewLoaded() {
        if (youtubeWebViewLoaded) return

        if (!CookieStore.hasSession(requireContext())) {
            binding.youtubeSignedOutNotice.visibility = View.VISIBLE
            binding.youtubeWebview.visibility = View.GONE
            return
        }

        binding.youtubeSignedOutNotice.visibility = View.GONE
        binding.youtubeWebview.visibility = View.VISIBLE

        val webView: WebView = binding.youtubeWebview
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(YouTubeJsInterface(requireContext()), "HFTube")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val script = requireContext().assets.open("youtube_inject.js")
                    .bufferedReader().use { it.readText() }
                view?.evaluateJavascript(script, null)
            }
        }

        webView.loadUrl("https://m.youtube.com/")
        youtubeWebViewLoaded = true
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
        binding.youtubeWebview.destroy()
        super.onDestroyView()
        _binding = null
    }
}
