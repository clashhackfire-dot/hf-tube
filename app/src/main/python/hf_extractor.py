"""
HF-Tube's extraction/download backend. Thin wrapper around yt-dlp — all the
actual heavy lifting (URL resolution, format selection, muxing) is yt-dlp's;
this module just shapes its output for the Kotlin side and reports progress.

Called from Kotlin via Chaquopy (see YtDlpBridge.kt). Every function here
runs on a background thread on the Kotlin side — never call these from the
main thread.
"""

import json
import yt_dlp


def list_formats(url: str, cookies_path: str | None = None) -> str:
    """
    Resolve a URL (or search query) and return a JSON string describing the
    available formats, grouped the way the format-picker screen expects:
    audio-only entries and video entries, each with a stable format id,
    a short human label, and an approximate size in bytes (yt-dlp reports
    filesize or filesize_approx — many formats only have the latter until
    download time).
    """
    opts = {
        "quiet": True,
        "no_warnings": True,
        "skip_download": True,
        "noplaylist": True,
    }
    if cookies_path:
        # Deliberately unused today — sign-in/cookie capture is deferred.
        # Once it exists, pass the on-device cookie file path here so
        # requests look like a normal signed-in browser session.
        opts["cookiefile"] = cookies_path

    with yt_dlp.YoutubeDL(opts) as ydl:
        info = ydl.extract_info(url, download=False)

    if "entries" in info:
        # A search query resolves to a result list — use the first hit.
        info = info["entries"][0]

    formats = []
    for f in info.get("formats", []):
        size = f.get("filesize") or f.get("filesize_approx") or 0
        is_audio_only = f.get("vcodec") == "none"
        is_video = f.get("acodec") is not None or f.get("vcodec") != "none"
        formats.append({
            "format_id": f.get("format_id"),
            "ext": f.get("ext"),
            "height": f.get("height"),
            "abr": f.get("abr"),
            "size_bytes": size,
            "is_audio_only": is_audio_only,
            "has_video": f.get("vcodec") not in (None, "none"),
        })

    result = {
        "title": info.get("title"),
        "duration": info.get("duration"),
        "thumbnail": info.get("thumbnail"),
        "webpage_url": info.get("webpage_url"),
        "formats": formats,
    }
    return json.dumps(result)


def download(
    url: str,
    format_id: str,
    output_path: str,
    cookies_path: str | None = None,
    progress_callback=None,
) -> str:
    """
    Download a single resolved format to output_path. progress_callback, if
    given, is a Kotlin/Java object exposing onProgress(percent: int,
    speed_text: str) — called from yt-dlp's own progress hook, so it fires
    on whatever thread yt-dlp is running on (the caller is expected to have
    already put this whole download() call on a background thread).
    """
    def hook(d):
        if progress_callback is None:
            return
        if d["status"] == "downloading":
            total = d.get("total_bytes") or d.get("total_bytes_estimate") or 0
            downloaded = d.get("downloaded_bytes", 0)
            percent = int(downloaded * 100 / total) if total else 0
            speed = d.get("speed")
            speed_text = f"{speed / 1024:.0f} KB/s" if speed else ""
            progress_callback.onProgress(percent, speed_text)
        elif d["status"] == "finished":
            progress_callback.onProgress(100, "")

    opts = {
        "format": format_id,
        "outtmpl": output_path,
        "quiet": True,
        "no_warnings": True,
        "noplaylist": True,
        "progress_hooks": [hook],
    }
    if cookies_path:
        opts["cookiefile"] = cookies_path

    with yt_dlp.YoutubeDL(opts) as ydl:
        ydl.download([url])

    return output_path
