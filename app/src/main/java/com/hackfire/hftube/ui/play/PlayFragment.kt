package com.hackfire.hftube.ui.play

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hackfire.hftube.databinding.FragmentPlayBinding

/**
 * Single scroll, two sections: "Downloading (N)" then "Downloaded". No
 * download engine is wired up yet, so this starts empty and shows the
 * empty-state message rather than faking progress. Once yt-dlp/Chaquopy
 * is hooked up, replace the empty lists below with the real data source
 * (a ViewModel backed by the download service).
 */
class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlayAdapter(
            onRowClicked = { entry ->
                val intent = Intent(requireContext(), DownloadDetailActivity::class.java)
                intent.putExtra(DownloadDetailActivity.EXTRA_DOWNLOAD_ID, entry.id)
                startActivity(intent)
            },
            onOverflowClicked = { /* TODO: show delete/share/open-file menu */ }
        )
        binding.playRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.playRecycler.adapter = adapter

        render(downloading = emptyList(), downloaded = emptyList())
    }

    private fun render(downloading: List<DownloadEntry>, downloaded: List<DownloadEntry>) {
        val items = buildPlayListItems(downloading, downloaded)
        adapter.submitList(items)
        binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        binding.playRecycler.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
