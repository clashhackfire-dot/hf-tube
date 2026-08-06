package com.hackfire.hftube.ui.play

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hackfire.hftube.databinding.ActivityDownloadDetailBinding

/**
 * Detail screen for one in-progress download: back arrow + title + delete-
 * all trash icon in the header, "Pause All" toggle above a single row that
 * — unlike the list version — ends in a circular pause button instead of a
 * chevron. No download engine is wired up yet, so this currently just shows
 * whatever ID it was launched with; real progress data comes once
 * yt-dlp/Chaquopy is hooked up.
 */
class DownloadDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val downloadId = intent.getStringExtra(EXTRA_DOWNLOAD_ID).orEmpty()
        binding.detailTitle.text = downloadId

        binding.backButton.setOnClickListener { finish() }
        binding.deleteAllButton.setOnClickListener { /* TODO: confirm + cancel all downloads */ }
        binding.pauseAllLabel.setOnClickListener { /* TODO: toggle pause-all */ }

        binding.detailRow.rowChevron.visibility = android.view.View.GONE
        binding.detailRow.rowOverflow.visibility = android.view.View.GONE
        binding.detailRow.rowPause.visibility = android.view.View.VISIBLE
        binding.detailRow.rowPause.setOnClickListener { /* TODO: pause/resume this download */ }
    }

    companion object {
        const val EXTRA_DOWNLOAD_ID = "extra_download_id"
    }
}
