package com.hackfire.hftube.ui.formatpicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hackfire.hftube.R
import com.hackfire.hftube.download.RemoteFormat

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ROW = 1

/**
 * Audio/Video grouped list with single-select rows (selector circle fills +
 * checkmark when selected). Selecting a row is how the bottom Download pill
 * gets enabled — see FormatPickerActivity.
 */
class FormatAdapter(
    private val onFormatSelected: (RemoteFormat) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<FormatListItem> = emptyList()
    private var selectedFormatId: String? = null

    fun submitList(newItems: List<FormatListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = when (items[position]) {
        is FormatListItem.Header -> VIEW_TYPE_HEADER
        is FormatListItem.Row -> VIEW_TYPE_ROW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(inflater.inflate(R.layout.item_format_section_header, parent, false))
        } else {
            RowViewHolder(inflater.inflate(R.layout.item_format_row, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is FormatListItem.Header -> (holder as HeaderViewHolder).bind(item.label)
            is FormatListItem.Row -> (holder as RowViewHolder).bind(
                item, item.format.formatId == selectedFormatId
            ) {
                val previousSelected = selectedFormatId
                selectedFormatId = item.format.formatId
                notifyItemChanged(items.indexOfFirst {
                    it is FormatListItem.Row && it.format.formatId == previousSelected
                })
                notifyItemChanged(position)
                onFormatSelected(item.format)
            }
        }
    }

    private class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val label = itemView as TextView
        fun bind(text: String) {
            label.text = text
        }
    }

    private class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon = itemView.findViewById<ImageView>(R.id.format_icon)
        private val title = itemView.findViewById<TextView>(R.id.format_title)
        private val desc = itemView.findViewById<TextView>(R.id.format_desc)
        private val size = itemView.findViewById<TextView>(R.id.format_size)
        private val selector = itemView.findViewById<ImageView>(R.id.format_selector)

        fun bind(item: FormatListItem.Row, isSelected: Boolean, onClick: () -> Unit) {
            icon.setImageResource(
                if (item.format.isAudioOnly) R.drawable.ic_format_audio else R.drawable.ic_format_video
            )
            title.text = item.title
            desc.text = item.description
            size.text = formatBytes(item.format.sizeBytes)
            selector.setImageResource(
                if (isSelected) R.drawable.ic_selector_circle_checked else R.drawable.ic_selector_circle_unchecked
            )
            itemView.setOnClickListener { onClick() }
        }
    }
}
