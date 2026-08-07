package com.hackfire.hftube.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hackfire.hftube.R
import com.hackfire.hftube.auth.CookieStore
import com.hackfire.hftube.databinding.FragmentSettingsBinding
import com.hackfire.hftube.ui.search.LoginActivity

/**
 * Plain grouped list under "General" and "Info" headers. Account reflects
 * real sign-in state via CookieStore — tapping it while signed out launches
 * LoginActivity, tapping it while signed in clears the session.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SettingsAdapter

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refreshList() }

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

        adapter = SettingsAdapter { row ->
            when (row.id) {
                "account" -> {
                    if (CookieStore.hasSession(requireContext())) {
                        CookieStore.clearSession(requireContext())
                        refreshList()
                    } else {
                        loginLauncher.launch(Intent(requireContext(), LoginActivity::class.java))
                    }
                }
                // TODO: navigate to each remaining row's real destination
                // once those screens exist (download settings, notifications,
                // theme, language, feedback, share, about).
            }
        }
        binding.settingsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.settingsRecycler.adapter = adapter

        refreshList()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val signedIn = CookieStore.hasSession(requireContext())
        adapter.submitList(
            listOf(
                SettingsListItem.Header(R.string.settings_section_general),
                SettingsListItem.Row("download", R.drawable.ic_settings_download, R.string.settings_download),
                SettingsListItem.Row("notifications", R.drawable.ic_settings_bell, R.string.settings_notifications),
                SettingsListItem.Row("theme", R.drawable.ic_settings_theme, R.string.settings_theme),
                SettingsListItem.Header(R.string.settings_section_info),
                SettingsListItem.Row(
                    "account", R.drawable.ic_settings_account, R.string.settings_account,
                    if (signedIn) "Signed in" else "Not signed in"
                ),
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
