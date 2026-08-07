package com.hackfire.hftube.ui.search

import android.content.Context
import android.content.Intent
import android.webkit.JavascriptInterface
import com.hackfire.hftube.ui.formatpicker.FormatPickerActivity

/**
 * Bridged into the YouTube WebView as `window.HFTube` by
 * youtube_inject.js. Runs on the WebView's JS thread — hop back to a
 * normal Android context via startActivity, which is thread-safe.
 */
class YouTubeJsInterface(private val context: Context) {

    @JavascriptInterface
    fun onDownloadClicked(videoUrl: String) {
        val intent = Intent(context, FormatPickerActivity::class.java)
        intent.putExtra(FormatPickerActivity.EXTRA_QUERY, videoUrl)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
