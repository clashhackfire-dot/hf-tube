package com.hackfire.hftube.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Single scroll, two sections: "Downloading (N)" then "Downloaded",
 * sharing one row component (list / detail / finished — see
 * DownloadRowViewHolder). Backed by a RecyclerView with two item view
 * types or two separate adapters + a ConcatAdapter.
 */
class PlayFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // TODO: inflate fragment_play.xml, wire RecyclerView + ConcatAdapter
        // of DownloadingAdapter + DownloadedAdapter.
        return View(requireContext())
    }
}
