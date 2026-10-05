package org.futo.inputmethod.latin.uix.actions

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.isDirectBootUnlocked
import org.futo.inputmethod.latin.uix.settings.SettingsActivity

val ThemeAction = Action(
    icon = R.drawable.themes,
    name = R.string.action_theme_switcher_title,
    simplePressImpl = { manager, _ ->
        val context = manager.getContext()
        if (context.isDirectBootUnlocked && !manager.isDeviceLocked()) {
            SettingsActivity.openToNavDest(context, "themes")
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.action_clipboard_manager_error_device_locked_title),
                Toast.LENGTH_SHORT
            ).show()
        }
    },
    canShowKeyboard = false,
    windowImpl = null,
)
