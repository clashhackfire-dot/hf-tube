package com.hackfire.hftube.ui.formatpicker

import com.hackfire.hftube.download.RemoteFormat

sealed class FormatListItem {
    data class Header(val label: String) : FormatListItem()
    data class Row(val format: RemoteFormat, val title: String, val description: String) : FormatListItem()
}

fun buildFormatListItems(formats: List<RemoteFormat>): List<FormatListItem> {
    val audio = formats.filter { it.isAudioOnly }
    val video = formats.filter { it.hasVideo }

    val items = mutableListOf<FormatListItem>()
    if (audio.isNotEmpty()) {
        items += FormatListItem.Header("Audio")
        audio.forEach { f ->
            val abr = f.audioBitrate?.let { "${it.toInt()}K" } ?: f.ext.uppercase()
            items += FormatListItem.Row(
                format = f,
                title = "${f.ext.uppercase()} ($abr)",
                description = "Supports Bluetooth speakers, phones, car stereos, smartwatches, etc"
            )
        }
    }
    if (video.isNotEmpty()) {
        items += FormatListItem.Header("Video")
        video.sortedByDescending { it.heightPx ?: 0 }.forEach { f ->
            val label = f.heightPx?.let { "${it}p" } ?: f.ext.uppercase()
            items += FormatListItem.Row(
                format = f,
                title = "High quality ($label)",
                description = "Clear view and quick play"
            )
        }
    }
    return items
}

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "—"
    val mb = bytes / (1024.0 * 1024.0)
    return "%.1f MB".format(mb)
}
