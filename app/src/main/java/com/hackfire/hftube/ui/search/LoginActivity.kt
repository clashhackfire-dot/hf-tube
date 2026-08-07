package com.hackfire.hftube.ui.search

import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.hackfire.hftube.auth.CookieStore
import com.hackfire.hftube.databinding.ActivityLoginBinding

/**
 * WebView Google sign-in, capturing the resulting YouTube session cookies
 * so yt-dlp requests look like a normal signed-in browser instead of
 * getting bot-checked, and so the YouTube tab can show a real feed.
 *
 * IMPORTANT CAVEAT: Google's OAuth policy blocks sign-in inside embedded
 * WebViews and shows "This browser or app may not be secure" for exactly
 * this kind of flow. Setting a desktop-Chrome user agent (below) gets
 * around it in some cases, but Google can and does tighten this detection,
 * so this may stop working without warning and there's no fully reliable
 * client-side fix. If sign-in gets blocked, that's Google's anti-automation
 * policy working as intended, not a bug in this code.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBackButton.setOnClickListener { finish() }

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(binding.loginWebview, true)

        val webView: WebView = binding.loginWebview
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        // Desktop UA — see class doc: this is a workaround for Google's
        // embedded-WebView block, not a guarantee it keeps working.
        webView.settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.loginProgress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.loginProgress.visibility = View.GONE
                if (url != null && isSignedInRedirect(url)) {
                    cookieManager.flush()
                    CookieStore.captureFromWebView(applicationContext)
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }

        webView.loadUrl(
            "https://accounts.google.com/ServiceLogin?continue=" +
                "https://www.youtube.com/"
        )
    }

    /**
     * Heuristic for "sign-in succeeded": Google redirects back to
     * youtube.com once the login flow completes. Fragile by nature — any
     * change to Google's redirect chain could require updating this.
     */
    private fun isSignedInRedirect(url: String): Boolean {
        return url.startsWith("https://www.youtube.com") && !url.contains("ServiceLogin")
    }

    override fun onDestroy() {
        binding.loginWebview.destroy()
        super.onDestroy()
    }
}
