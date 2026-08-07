package com.hackfire.hftube.ui.formatpicker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hackfire.hftube.databinding.ActivityFormatPickerBinding
import com.hackfire.hftube.download.DownloadService
import com.hackfire.hftube.download.RemoteFormat
import com.hackfire.hftube.download.YtDlpBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Sheet/screen shown after a search/paste resolves. Loads formats off the
 * main thread (yt-dlp extraction is blocking network + subprocess work),
 * groups them into Audio/Video, and enables the Download pill once a
 * format is selected. Tapping Download hands off to DownloadService and
 * finishes this screen — actual progress shows up in the Play tab.
 */
class FormatPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormatPickerBinding
    private var resolvedTitle: String = ""
    private var resolvedUrl: String = ""
    private var selectedFormat: RemoteFormat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormatPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val query = intent.getStringExtra(EXTRA_QUERY).orEmpty()
        resolvedUrl = query
        binding.pickerTitle.text = query

        val adapter = FormatAdapter { format ->
            selectedFormat = format
            binding.downloadButton.isEnabled = true
            binding.downloadButton.alpha = 1f
        }
        binding.formatRecycler.layoutManager = LinearLayoutManager(this)
        binding.formatRecycler.adapter = adapter

        binding.downloadButton.setOnClickListener {
            val format = selectedFormat ?: return@setOnClickListener
            startService(
                Intent(this, DownloadService::class.java).apply {
                    putExtra(DownloadService.EXTRA_URL, resolvedUrl)
                    putExtra(DownloadService.EXTRA_FORMAT_ID, format.formatId)
                    putExtra(DownloadService.EXTRA_TITLE, resolvedTitle.ifEmpty { resolvedUrl })
                    putExtra(DownloadService.EXTRA_EXT, format.ext)
                }
            )
            finish()
        }

        loadFormats(query, adapter)
    }

    private fun loadFormats(query: String, adapter: FormatAdapter) {
        binding.loadingSpinner.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val media = withContext(Dispatchers.IO) {
                    YtDlpBridge.init(applicationContext)
                    val cookiesPath = com.hackfire.hftube.auth.CookieStore
                        .takeIf { it.hasSession(applicationContext) }
                        ?.cookieFile(applicationContext)?.absolutePath
                    YtDlpBridge.listFormats(query, cookiesPath)
                }
                resolvedTitle = media.title
                resolvedUrl = media.webpageUrl ?: query
                binding.pickerTitle.text = media.title
                adapter.submitList(buildFormatListItems(media.formats))
            } catch (e: Exception) {
                // TODO: show a proper error state (bad URL, no network,
                // extraction failure) instead of just leaving the list empty.
            } finally {
                binding.loadingSpinner.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_QUERY = "extra_query"
    }
}
