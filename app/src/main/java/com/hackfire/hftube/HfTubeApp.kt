package com.hackfire.hftube

import android.app.Application

/**
 * No analytics, no crash reporters, no ad SDK init here — keep it that way.
 */
class HfTubeApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
