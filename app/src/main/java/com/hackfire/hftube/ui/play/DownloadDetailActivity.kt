package com.hackfire.hftube.ui.play

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Detail screen for an in-progress download: back arrow + title + delete-all
 * trash icon in the header, "Pause All" toggle above the single-item row
 * (which itself ends in a circular pause button in this state).
 */
class DownloadDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: inflate activity_download_detail.xml
    }

    companion object {
        const val EXTRA_DOWNLOAD_ID = "extra_download_id"
    }
}
