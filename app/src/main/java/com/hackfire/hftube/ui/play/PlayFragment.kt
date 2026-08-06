package com.hackfire.hftube.ui.play

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hackfire.hftube.databinding.FragmentPlayBinding
import com.hackfire.hftube.download.DownloadRepository

/**
 * Single scroll, two sections: "Downloading (N)" then "Downloaded", sourced
 * from DownloadRepository — the in-memory store DownloadService writes to.
 * No persistence yet, so "Downloaded" only holds what finished since the
 * app last started.
 */
class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlayAdapter
    private val mainHandler = Handler(Looper.getMainLooper())
    private val repositoryListener = { mainHandler.post { render() } }

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

        render()
    }

    override fun onStart() {
        super.onStart()
        DownloadRepository.addListener(repositoryListener)
        render()
    }

    override fun onStop() {
        super.onStop()
        DownloadRepository.removeListener(repositoryListener)
    }

    private fun render() {
        if (_binding == null) return
        val items = buildPlayListItems(DownloadRepository.downloading(), DownloadRepository.downloaded())
        adapter.submitList(items)
        binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        binding.playRecycler.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
