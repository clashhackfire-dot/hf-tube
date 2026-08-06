package com.hackfire.hftube.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.hackfire.hftube.R
import com.hackfire.hftube.ui.play.DownloadEntry
import java.io.File
import java.util.concurrent.Executors

/**
 * Runs one download at a time via YtDlpBridge on a background executor, and
 * mirrors progress into DownloadRepository so the Play tab can render it.
 * No pause/resume/queueing yet — this is enough to prove the yt-dlp/
 * Chaquopy path end to end; a real queue is future work.
 */
class DownloadService : Service() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra(EXTRA_URL) ?: return START_NOT_STICKY
        val formatId = intent.getStringExtra(EXTRA_FORMAT_ID) ?: return START_NOT_STICKY
        val title = intent.getStringExtra(EXTRA_TITLE) ?: url
        val ext = intent.getStringExtra(EXTRA_EXT) ?: "mp4"

        val id = "$url|$formatId"
        DownloadRepository.addOrUpdate(DownloadEntry(id = id, title = title, finished = false))

        startForeground(NOTIFICATION_ID, buildNotification(title, 0))

        executor.execute {
            runDownload(id, url, formatId, title, ext)
        }

        return START_REDELIVER_INTENT
    }

    private fun runDownload(id: String, url: String, formatId: String, title: String, ext: String) {
        YtDlpBridge.init(applicationContext)

        val outputFile = File(getExternalFilesDir(null), "$title.$ext")
        val listener = object : DownloadProgressListener {
            override fun onProgress(percent: Int, speedText: String) {
                DownloadRepository.updateProgress(id, percent, speedText)
                updateNotification(title, percent)
            }
        }

        try {
            // cookiesPath deliberately null — sign-in/cookie capture is
            // deferred, so this runs the same as an anonymous yt-dlp call.
            YtDlpBridge.download(url, formatId, outputFile.absolutePath, null, listener)
            val sizeText = formatFileSize(outputFile.length())
            DownloadRepository.markFinished(id, durationText = "", sizeText = sizeText)
        } catch (e: Exception) {
            // TODO: surface failure state in the Play tab instead of
            // silently leaving the row stuck at its last progress value.
        } finally {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun buildNotification(title: String, percent: Int): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("$percent%")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setProgress(100, percent, false)
            .build()
    }

    private fun updateNotification(title: String, percent: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(title, percent))
    }

    private fun formatFileSize(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return "%.1f MB".format(mb)
    }

    companion object {
        const val EXTRA_URL = "extra_url"
        const val EXTRA_FORMAT_ID = "extra_format_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_EXT = "extra_ext"

        private const val CHANNEL_ID = "downloads"
        private const val NOTIFICATION_ID = 1001
    }
}
