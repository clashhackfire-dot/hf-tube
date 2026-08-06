package com.hackfire.hftube.download

import android.content.Context
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import org.json.JSONObject

/** A single available format, parsed from hf_extractor.list_formats(). */
data class RemoteFormat(
    val formatId: String,
    val ext: String,
    val heightPx: Int?,
    val audioBitrate: Double?,
    val sizeBytes: Long,
    val isAudioOnly: Boolean,
    val hasVideo: Boolean
)

data class ResolvedMedia(
    val title: String,
    val durationSeconds: Int?,
    val thumbnailUrl: String?,
    val webpageUrl: String?,
    val formats: List<RemoteFormat>
)

/** Progress callback shape expected by hf_extractor.download()'s progress_hook. */
interface DownloadProgressListener {
    fun onProgress(percent: Int, speedText: String)
}

/**
 * Thin Kotlin-side wrapper around hf_extractor.py. Every function here does
 * blocking network/subprocess work via yt-dlp — always call from a
 * background thread (a coroutine on Dispatchers.IO, or the download
 * service's worker thread), never from the UI thread.
 */
object YtDlpBridge {

    private lateinit var module: PyObject

    fun init(context: Context) {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
        module = Python.getInstance().getModule("hf_extractor")
    }

    /**
     * Resolves a URL or search query. cookiesPath is unused for now — sign-
     * in/cookie capture is deliberately deferred, so requests go out
     * unauthenticated the same way yt-dlp would from a bare command line.
     */
    fun listFormats(url: String, cookiesPath: String? = null): ResolvedMedia {
        val json = module.callAttr("list_formats", url, cookiesPath).toString()
        val obj = JSONObject(json)
        val formatsArray = obj.getJSONArray("formats")
        val formats = buildList {
            for (i in 0 until formatsArray.length()) {
                val f = formatsArray.getJSONObject(i)
                add(
                    RemoteFormat(
                        formatId = f.optString("format_id"),
                        ext = f.optString("ext"),
                        heightPx = if (f.isNull("height")) null else f.optInt("height"),
                        audioBitrate = if (f.isNull("abr")) null else f.optDouble("abr"),
                        sizeBytes = f.optLong("size_bytes"),
                        isAudioOnly = f.optBoolean("is_audio_only"),
                        hasVideo = f.optBoolean("has_video")
                    )
                )
            }
        }
        return ResolvedMedia(
            title = obj.optString("title"),
            durationSeconds = if (obj.isNull("duration")) null else obj.optInt("duration"),
            thumbnailUrl = obj.optString("thumbnail", null),
            webpageUrl = obj.optString("webpage_url", null),
            formats = formats
        )
    }

    fun download(
        url: String,
        formatId: String,
        outputPath: String,
        cookiesPath: String? = null,
        listener: DownloadProgressListener? = null
    ): String {
        return module.callAttr(
            "download", url, formatId, outputPath, cookiesPath, listener
        ).toString()
    }
}
