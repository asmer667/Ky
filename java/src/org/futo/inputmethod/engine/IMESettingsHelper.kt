package org.futo.inputmethod.engine

import androidx.compose.runtime.Composable
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu

// Chinese and Japanese IME settings are disabled in this build
// (because mozc-lib is not available)

val SettingsByLanguage = emptyMap<String, UserSettingsMenu>()

@Composable
private fun anyVisible(): Boolean = false

val IMESettingsMenu = UserSettingsMenu(
    title = 0,
    navPath = "ime",
    registerNavPath = false,
    settings = emptyList(),
    visibilityCheck = { false }
)
