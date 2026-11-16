package org.futo.inputmethod.latin.uix.settings.pages.themes

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.THEME_KEY
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.useDataStore
import org.futo.inputmethod.latin.uix.theme.selector.ThemePicker
import org.futo.inputmethod.latin.uix.settings.Route

@Preview
@Composable
fun ThemeScreen(navController: NavHostController = rememberNavController()) {
    val (theme, _) = useDataStore(THEME_KEY.key, THEME_KEY.default)
    val enableKeyboardPreview = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    var showKeyboard by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {},
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Start
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            ScreenTitle(stringResource(R.string.theme_settings_title), showBack = true, navController)
            ThemePicker(
                onDeleteCustomTheme = { navController.navigate(Route.DeleteTheme(it)) },
                onCustomTheme = { navController.navigate("customThemeDialog") },
                showKeyboard = showKeyboard,
                onToggleKeyboard = { showKeyboard = !showKeyboard }
            )
        }
    }
}