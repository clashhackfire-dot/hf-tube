package com.hackfire.hftube.ui.play

/**
 * A single download, in one of two states. Populated by the download
 * engine (yt-dlp/Chaquopy) once that's wired up — for now PlayFragment
 * just seeds an empty list, so the tab renders its empty state honestly
 * instead of showing fake progress.
 */
data class DownloadEntry(
    val id: String,
    val title: String,
    val finished: Boolean,
    val progressPercent: Int = 0,
    val speedText: String = "",
    val durationText: String = "",
    val sizeText: String = ""
)

/** Row/header items fed to the RecyclerView, flattened from two sections. */
sealed class PlayListItem {
    data class Header(val label: String) : PlayListItem()
    data class Row(val entry: DownloadEntry) : PlayListItem()
}

fun buildPlayListItems(
    downloading: List<DownloadEntry>,
    downloaded: List<DownloadEntry>
): List<PlayListItem> {
    val items = mutableListOf<PlayListItem>()
    if (downloading.isNotEmpty()) {
        items += PlayListItem.Header("Downloading (${downloading.size})")
        items += downloading.map { PlayListItem.Row(it) }
    }
    if (downloaded.isNotEmpty()) {
        items += PlayListItem.Header("Downloaded")
        items += downloaded.map { PlayListItem.Row(it) }
    }
    return items
}
