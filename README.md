# HF-Tube

An open-source, ad-free, telemetry-free YouTube media downloader for Android, built on [yt-dlp](https://github.com/yt-dlp/yt-dlp).

## Status

- Bottom-nav shell (Search / Play / Settings) ✅
- Search tab home UI (top-tab row, wordmark, pill search bar) ✅
- Play tab (Downloading/Downloaded sections, 3-state row) ✅
- Settings tab (grouped list, live sign-in state) ✅
- Format picker screen (Audio/Video sections, selectable rows, Download pill) ✅
- yt-dlp/Chaquopy extraction + download engine, foreground DownloadService ✅
- Sign-in: WebView Google login, captures the resulting session cookies on-device, feeds them to yt-dlp so requests look like a normal signed-in browser ✅
- YouTube tab: WebView on the signed-in session with an injected per-card download icon ✅

**Known limitations:**
- Google's OAuth policy blocks sign-in inside embedded WebViews ("This browser or app may not be secure"). The desktop-Chrome user-agent override in `LoginActivity` works around this today, but Google can tighten detection at any time — there's no fully reliable client-side fix for this, so sign-in may need re-tuning if it stops working.
- The injected download-icon script (`youtube_inject.js`) targets YouTube's mobile-web DOM with a best-effort heuristic. YouTube's markup changes without notice, so this selector may need updates over time.
- No persistence (Room) yet — the Play tab's "Downloaded" section resets when the app restarts.
- Pause/resume, delete-all, and the format-picker's error states are still stubs.

## Sign-in & cookies

On first launch (and any time from Settings → Account), HF-Tube opens a WebView pointed at Google sign-in. Once it redirects back to youtube.com, the session cookies are captured and written to a Netscape-format cookie file in **app-private internal storage only** (`filesDir/cookies/`) — never external storage, never committed to source control. This file is what gets passed to yt-dlp's `--cookies` flag for both format listing and downloading.

## Disclaimer

HF-Tube is a client-side extraction tool. It does not host, mirror, or distribute any media itself — it only automates requests a user could make manually in a browser. HF-Tube does not run any servers and collects no data.

You are solely responsible for ensuring your use of this app complies with YouTube's Terms of Service, applicable copyright law, and any other laws in your jurisdiction. The developers assume no liability for how the software is used.

## License

GPLv3 — see `LICENSE` (to be added). Depends on yt-dlp, also GPLv3-family licensed.

## Building

No Android Studio required. From Termux or any environment with a JDK + Android SDK/NDK command-line tools:

```
./gradlew assembleDebug
```

Chaquopy needs the Android NDK on top of the SDK — see the `android-actions/setup-android` step in `.github/workflows/build.yml` for what CI installs.

## Project layout

- `app/src/main/java/com/hackfire/hftube/`
  - `ui/search/` — Search tab, home top-tabs, WebView sign-in, YouTube tab + JS bridge
  - `ui/play/` — Downloads list/detail
  - `ui/settings/` — Settings tab
  - `ui/formatpicker/` — Audio/Video format picker screen
  - `auth/` — CookieStore (Netscape cookie-file capture/persistence)
  - `download/` — YtDlpBridge (Chaquopy), DownloadService, DownloadRepository
- `app/src/main/python/hf_extractor.py` — yt-dlp wrapper called from Kotlin
- `app/src/main/assets/youtube_inject.js` — download-icon injection script for the YouTube tab
- `app/src/main/res/` — layouts, drawables, design-token colors (`values/colors.xml`)
