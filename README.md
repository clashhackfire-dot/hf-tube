# HF-Tube

An open-source, ad-free, telemetry-free YouTube media downloader for Android, built on [yt-dlp](https://github.com/yt-dlp/yt-dlp).

## Status

Early scaffold. Bottom-nav shell (Search / Play / Settings) is in place, plus the Search tab's home UI (top-tab row, wordmark, pill search bar). Sign-in/cookie capture, the WebView YouTube tab, and the yt-dlp/Chaquopy download engine are **not yet implemented** — coming in later passes.

## Disclaimer

HF-Tube is a client-side extraction tool. It does not host, mirror, or distribute any media itself — it only automates requests a user could make manually in a browser. HF-Tube does not run any servers and collects no data.

You are solely responsible for ensuring your use of this app complies with YouTube's Terms of Service, applicable copyright law, and any other laws in your jurisdiction. The developers assume no liability for how the software is used.

## License

GPLv3 — see `LICENSE` (to be added). Depends on yt-dlp, also GPLv3-family licensed.

## Building

No Android Studio required. From Termux or any environment with a JDK + Android SDK command-line tools:

```
./gradlew assembleDebug
```

## Project layout

- `app/src/main/java/com/hackfire/hftube/` — Kotlin sources
  - `ui/search/` — Search tab, home top-tabs
  - `ui/play/` — Downloads list/detail
  - `ui/settings/` — Settings tab
- `app/src/main/res/` — layouts, drawables, design-token colors (`values/colors.xml`)
