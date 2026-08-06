package com.hackfire.hftube.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hackfire.hftube.R

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ROW = 1

/** Plain grouped list — gray section headers, icon+label+value+chevron rows, no dividers. */
class SettingsAdapter(
    private val onRowClicked: (SettingsListItem.Row) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<SettingsListItem> = emptyList()

    fun submitList(newItems: List<SettingsListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = when (items[position]) {
        is SettingsListItem.Header -> VIEW_TYPE_HEADER
        is SettingsListItem.Row -> VIEW_TYPE_ROW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(inflater.inflate(R.layout.item_settings_header, parent, false))
        } else {
            RowViewHolder(inflater.inflate(R.layout.item_settings_row, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SettingsListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is SettingsListItem.Row -> (holder as RowViewHolder).bind(item, onRowClicked)
        }
    }

    private class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val label = itemView as TextView
        fun bind(item: SettingsListItem.Header) {
            label.setText(item.labelRes)
        }
    }

    private class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon = itemView.findViewById<ImageView>(R.id.row_icon)
        private val label = itemView.findViewById<TextView>(R.id.row_label)
        private val value = itemView.findViewById<TextView>(R.id.row_value)

        fun bind(item: SettingsListItem.Row, onRowClicked: (SettingsListItem.Row) -> Unit) {
            icon.setImageResource(item.iconRes)
            label.setText(item.labelRes)
            value.text = item.value.orEmpty()
            value.visibility = if (item.value.isNullOrEmpty()) View.GONE else View.VISIBLE
            itemView.setOnClickListener { onRowClicked(item) }
        }
    }
}
