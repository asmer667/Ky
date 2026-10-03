package org.futo.inputmethod.latin.uix.settings.pages.pdict

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.futo.inputmethod.latin.R
import java.io.File

@Composable
fun ConfirmDeleteExtraDictFileDialog(
    dict: String,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    AlertDialog(
        title = {
            Text(stringResource(R.string.user_dict_settings_delete))
        },
        text = {
            Text(dict)
        },
        onDismissRequest = {
            navController.navigateUp()
        },
        confirmButton = {
            TextButton(onClick = {
                try {
                    val file = File(context.filesDir, "imported-dicts/$dict")
                    if (file.exists()) file.delete()
                } catch (_: Exception) { }
                navController.navigateUp()
            }) {
                Text(stringResource(R.string.user_dict_settings_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                navController.navigateUp()
            }) {
                Text(stringResource(R.string.action_emoji_clear_recent_emojis_cancel))
            }
        }
    )
}
