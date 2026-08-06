package com.hackfire.hftube.ui.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Hosts a WebView pointed at accounts.google.com. On successful sign-in,
 * reads CookieManager.getInstance().getCookie("https://www.youtube.com")
 * and hands the cookie string to CookieStore (encrypted, on-device only —
 * see .gitignore: cookie/session files must never be committed).
 *
 * Deliberately deferred: not implemented yet. Stubbed out so the manifest
 * entry and package structure exist without any WebView/cookie logic running.
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
