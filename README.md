# HF-Tube

An open-source, ad-free, telemetry-free YouTube media downloader for Android, built on [yt-dlp](https://github.com/yt-dlp/yt-dlp).

## Status

- Bottom-nav shell (Search / Play / Settings) ✅
- Search tab home UI (top-tab row, wordmark, pill search bar) ✅
- Play tab (Downloading/Downloaded sections, 3-state row) ✅
- Settings tab (grouped list) ✅
- Format picker screen (Audio/Video sections, selectable rows, Download pill) ✅
- yt-dlp/Chaquopy extraction + download engine, foreground DownloadService ✅
- Sign-in / cookie capture — **deliberately deferred**, not implemented. Requests currently go out unauthenticated, so some videos may hit YouTube's bot-check.
- Injected download icon in the WebView YouTube tab — not implemented yet (the WebView itself doesn't exist without sign-in).

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
  - `ui/search/` — Search tab, home top-tabs, (deferred) sign-in
  - `ui/play/` — Downloads list/detail
  - `ui/settings/` — Settings tab
  - `ui/formatpicker/` — Audio/Video format picker screen
  - `download/` — YtDlpBridge (Chaquopy), DownloadService, DownloadRepository
- `app/src/main/python/hf_extractor.py` — yt-dlp wrapper called from Kotlin
- `app/src/main/res/` — layouts, drawables, design-token colors (`values/colors.xml`)
