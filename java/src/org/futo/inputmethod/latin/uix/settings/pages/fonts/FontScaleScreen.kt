package org.futo.inputmethod.latin.uix.settings.pages.fonts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.futo.inputmethod.latin.uix.FontScaleState
import org.futo.inputmethod.latin.uix.settings.ScreenTitle

@Composable
fun FontScaleScreen(navController: NavHostController) {
    val context = LocalContext.current

    var textScale by remember { mutableStateOf(FontScaleState.textScale) }
    var widthScale by remember { mutableStateOf(FontScaleState.widthScale) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        ScreenTitle("Aa", showBack = true, navController)

        Column(Modifier.padding(16.dp)) {
            Text(
                "ضبط حجم وعرض النص",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "القيم تُحفَظ وتُطبَّق عند فتح الكيبورد",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            // معاينة
            Card(
                Modifier.fillMaxWidth().height(140.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Box(
                    Modifier.fillMaxSize().padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "أبجد هوّز",
                        fontSize = (30 * textScale).sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Slider الحجم
            Text("حجم النص: ${"%.2f".format(textScale)}×",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold)
            Slider(
                value = textScale,
                onValueChange = {
                    textScale = it
                    FontScaleState.textScale = it
                    savePref(context, "text_scale", it)
                },
                valueRange = 0.5f..2.0f
            )

            Spacer(Modifier.height(16.dp))

            // Slider العرض
            Text("عرض النص: ${"%.2f".format(widthScale)}×",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold)
            Slider(
                value = widthScale,
                onValueChange = {
                    widthScale = it
                    FontScaleState.widthScale = it
                    savePref(context, "width_scale", it)
                },
                valueRange = 0.5f..2.0f
            )

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    textScale = 1.0f
                    widthScale = 1.0f
                    FontScaleState.textScale = 1.0f
                    FontScaleState.widthScale = 1.0f
                    savePref(context, "text_scale", 1.0f)
                    savePref(context, "width_scale", 1.0f)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("إعادة تعيين (1.0×)")
            }
        }
    }
}

private fun savePref(context: android.content.Context, key: String, value: Float) {
    context.getSharedPreferences("font_prefs", android.content.Context.MODE_PRIVATE)
        .edit().putFloat(key, value).apply()
}
