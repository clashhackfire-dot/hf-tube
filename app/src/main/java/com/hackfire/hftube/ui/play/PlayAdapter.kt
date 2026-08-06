package com.hackfire.hftube.ui.play

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hackfire.hftube.R

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ROW = 1

/**
 * Renders both "Downloading" and "Downloaded" sections from one flattened
 * list. Each row is the same layout in one of two states: in-progress
 * (progress bar + speed/percent, trailing chevron) or finished (duration +
 * size, trailing overflow menu). The chevron-tap callback is how a list row
 * pushes the detail screen; the row itself never shows the pause button —
 * that only appears once you're inside the detail screen.
 */
class PlayAdapter(
    private val onRowClicked: (DownloadEntry) -> Unit,
    private val onOverflowClicked: (DownloadEntry) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<PlayListItem> = emptyList()

    fun submitList(newItems: List<PlayListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = when (items[position]) {
        is PlayListItem.Header -> VIEW_TYPE_HEADER
        is PlayListItem.Row -> VIEW_TYPE_ROW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(inflater.inflate(R.layout.item_play_header, parent, false))
        } else {
            RowViewHolder(inflater.inflate(R.layout.item_play_row, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is PlayListItem.Header -> (holder as HeaderViewHolder).bind(item.label)
            is PlayListItem.Row -> (holder as RowViewHolder).bind(item.entry, onRowClicked, onOverflowClicked)
        }
    }

    private class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val label = itemView as android.widget.TextView
        fun bind(text: String) {
            label.text = text
        }
    }

    private class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<android.widget.TextView>(R.id.row_title)
        private val progressGroup = itemView.findViewById<View>(R.id.progress_group)
        private val progressBar = itemView.findViewById<android.widget.ProgressBar>(R.id.row_progress)
        private val speed = itemView.findViewById<android.widget.TextView>(R.id.row_speed)
        private val percent = itemView.findViewById<android.widget.TextView>(R.id.row_percent)
        private val meta = itemView.findViewById<android.widget.TextView>(R.id.row_meta)
        private val chevron = itemView.findViewById<View>(R.id.row_chevron)
        private val overflow = itemView.findViewById<View>(R.id.row_overflow)

        fun bind(
            entry: DownloadEntry,
            onRowClicked: (DownloadEntry) -> Unit,
            onOverflowClicked: (DownloadEntry) -> Unit
        ) {
            title.text = entry.title

            if (entry.finished) {
                progressGroup.visibility = View.GONE
                meta.visibility = View.VISIBLE
                meta.text = "${entry.durationText}  •  ${entry.sizeText}"
                chevron.visibility = View.GONE
                overflow.visibility = View.VISIBLE
                itemView.setOnClickListener(null)
                overflow.setOnClickListener { onOverflowClicked(entry) }
            } else {
                progressGroup.visibility = View.VISIBLE
                meta.visibility = View.GONE
                progressBar.progress = entry.progressPercent
                speed.text = entry.speedText
                percent.text = "${entry.progressPercent}%"
                chevron.visibility = View.VISIBLE
                overflow.visibility = View.GONE
                itemView.setOnClickListener { onRowClicked(entry) }
            }
        }
    }
}
