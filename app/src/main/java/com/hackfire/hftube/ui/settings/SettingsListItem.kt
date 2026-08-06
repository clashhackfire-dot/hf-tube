package com.hackfire.hftube.ui.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class SettingsListItem {
    data class Header(@StringRes val labelRes: Int) : SettingsListItem()
    data class Row(
        val id: String,
        @DrawableRes val iconRes: Int,
        @StringRes val labelRes: Int,
        val value: String? = null
    ) : SettingsListItem()
}
