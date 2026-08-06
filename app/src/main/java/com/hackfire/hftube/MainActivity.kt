package com.hackfire.hftube

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hackfire.hftube.databinding.ActivityMainBinding
import com.hackfire.hftube.ui.play.PlayFragment
import com.hackfire.hftube.ui.search.SearchHomeFragment
import com.hackfire.hftube.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Fragments are created once and swapped with show/hide so scroll
    // position and WebView state survive tab switches.
    private val searchFragment = SearchHomeFragment()
    private val playFragment = PlayFragment()
    private val settingsFragment = SettingsFragment()
    private var activeFragment: Fragment = searchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, settingsFragment, TAG_SETTINGS).hide(settingsFragment)
                .add(R.id.fragment_container, playFragment, TAG_PLAY).hide(playFragment)
                .add(R.id.fragment_container, searchFragment, TAG_SEARCH)
                .commit()
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val target = when (item.itemId) {
                R.id.nav_search -> searchFragment
                R.id.nav_play -> playFragment
                R.id.nav_settings -> settingsFragment
                else -> return@setOnItemSelectedListener false
            }
            switchTo(target)
            true
        }
    }

    private fun switchTo(fragment: Fragment) {
        if (fragment === activeFragment) return
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit()
        activeFragment = fragment
    }

    companion object {
        private const val TAG_SEARCH = "search"
        private const val TAG_PLAY = "play"
        private const val TAG_SETTINGS = "settings"
    }
}
