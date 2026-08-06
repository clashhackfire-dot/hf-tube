package com.hackfire.hftube.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Plain grouped list: gray section headers ("General", "Info"), rows with
 * icon + label + optional right-aligned value + chevron, no dividers —
 * spacing only. Simple RecyclerView with a header item type + row item type.
 */
class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // TODO: inflate fragment_settings.xml
        return View(requireContext())
    }
}
