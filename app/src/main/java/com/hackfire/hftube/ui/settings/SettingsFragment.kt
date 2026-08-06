package com.hackfire.hftube.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hackfire.hftube.R
import com.hackfire.hftube.databinding.FragmentSettingsBinding

/**
 * Plain grouped list under "General" and "Info" headers. Rows are inert for
 * now (no destination screens exist yet) except Account, which shows the
 * signed-in state once sign-in is implemented — currently always "Not
 * signed in" since that feature is deliberately deferred.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SettingsAdapter { row ->
            // TODO: navigate to each row's real destination once those
            // screens exist (download settings, notifications, theme, etc).
        }
        binding.settingsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.settingsRecycler.adapter = adapter

        adapter.submitList(
            listOf(
                SettingsListItem.Header(R.string.settings_section_general),
                SettingsListItem.Row("download", R.drawable.ic_settings_download, R.string.settings_download),
                SettingsListItem.Row("notifications", R.drawable.ic_settings_bell, R.string.settings_notifications),
                SettingsListItem.Row("theme", R.drawable.ic_settings_theme, R.string.settings_theme),
                SettingsListItem.Header(R.string.settings_section_info),
                SettingsListItem.Row("account", R.drawable.ic_settings_account, R.string.settings_account, "Not signed in"),
                SettingsListItem.Row("language", R.drawable.ic_settings_language, R.string.settings_language, "English (US)"),
                SettingsListItem.Row("feedback", R.drawable.ic_settings_feedback, R.string.settings_feedback),
                SettingsListItem.Row("share", R.drawable.ic_settings_share, R.string.settings_share),
                SettingsListItem.Row("about", R.drawable.ic_settings_about, R.string.settings_about)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
