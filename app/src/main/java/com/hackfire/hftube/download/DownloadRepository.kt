package com.hackfire.hftube.download

import com.hackfire.hftube.ui.play.DownloadEntry

/**
 * Process-lifetime, in-memory list of downloads. No persistence yet — a
 * real implementation would back this with Room so "Downloaded" survives
 * app restarts; this is enough to let DownloadService and PlayFragment
 * agree on state for now.
 */
object DownloadRepository {

    private val listeners = mutableListOf<() -> Unit>()
    private val entries = linkedMapOf<String, DownloadEntry>()

    @Synchronized
    fun addOrUpdate(entry: DownloadEntry) {
        entries[entry.id] = entry
        notifyListeners()
    }

    @Synchronized
    fun updateProgress(id: String, percent: Int, speedText: String) {
        val current = entries[id] ?: return
        entries[id] = current.copy(progressPercent = percent, speedText = speedText)
        notifyListeners()
    }

    @Synchronized
    fun markFinished(id: String, durationText: String, sizeText: String) {
        val current = entries[id] ?: return
        entries[id] = current.copy(
            finished = true,
            durationText = durationText,
            sizeText = sizeText
        )
        notifyListeners()
    }

    @Synchronized
    fun downloading(): List<DownloadEntry> = entries.values.filter { !it.finished }

    @Synchronized
    fun downloaded(): List<DownloadEntry> = entries.values.filter { it.finished }

    fun addListener(listener: () -> Unit) {
        listeners += listener
    }

    fun removeListener(listener: () -> Unit) {
        listeners -= listener
    }

    private fun notifyListeners() {
        listeners.toList().forEach { it() }
    }
}
