package org.futo.inputmethod.latin.uix.settings.pages.fonts

import android.graphics.Typeface
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.futo.inputmethod.latin.uix.settings.Route
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.Tip

/**
 * شاشة الخطوط — 3 تبويبات:
 *  1. عربي (من assets/fonts/Arabic/)
 *  2. إنجليزي (من assets/fonts/English/)
 *  3. استيراد من الجهاز
 */
@Composable
fun FontScreen(navController: NavHostController) {
    val context = LocalContext.current
    var tab by remember { mutableStateOf(0) }
    val tabs = listOf("🇸🇦 عربي", "🇬🇧 إنجليزي", "📂 استيراد")

    var currentArabicName by remember { mutableStateOf(FontManager.currentFontName(context, "Arabic")) }
    var currentEnglishName by remember { mutableStateOf(FontManager.currentFontName(context, "English")) }

    Column(Modifier.fillMaxSize()) {
        ScreenTitle("🔤 الخطوط", showBack = true, navController)

        TabRow(selectedTabIndex = tab) {
            tabs.forEachIndexed { i, label ->
                Tab(
                    selected = tab == i,
                    onClick = { tab = i },
                    text = { Text(label, fontSize = 12.sp) }
                )
            }
        }

        when (tab) {
            0 -> FontList(
                language = "Arabic",
                currentName = currentArabicName,
                onSelect = { name ->
                    val target = FontManager.currentArabicFile(context)
                    if (FontManager.copyBundledToInternal(context, "Arabic", name, target)) {
                        currentArabicName = name
                        applyFont(context, target, "Arabic")
                    }
                }
            )
            1 -> FontList(
                language = "English",
                currentName = currentEnglishName,
                onSelect = { name ->
                    val target = FontManager.currentEnglishFile(context)
                    if (FontManager.copyBundledToInternal(context, "English", name, target)) {
                        currentEnglishName = name
                        applyFont(context, target, "English")
                    }
                }
            )
            else -> ImportPanel(
                onImport = { uri, language ->
                    val target = if (language == "Arabic")
                        FontManager.currentArabicFile(context)
                    else FontManager.currentEnglishFile(context)
                    if (FontManager.importFromUri(context, uri, target)) {
                        if (language == "Arabic") currentArabicName = target.nameWithoutExtension
                        else currentEnglishName = target.nameWithoutExtension
                        applyFont(context, target, language)
                    }
                }
            )
        }
    }
}

@Composable
private fun FontList(language: String, currentName: String?, onSelect: (String) -> Unit) {
    val context = LocalContext.current
    val fonts = remember(language) { FontManager.listBundledFonts(context, language) }

    if (fonts.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد خطوط في assets/fonts/$language/")
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(fonts) { fileName ->
            FontCard(
                language = language,
                fileName = fileName,
                isSelected = (currentName == fileName.removeSuffix(".ttf")),
                onClick = { onSelect(fileName) }
            )
        }
    }
}

@Composable
private fun FontCard(
    language: String,
    fileName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val typeface = remember(fileName) {
        FontManager.loadBundledTypeface(context, language, fileName)
    }
    val displayName = remember(fileName) {
        fileName.removeSuffix(".ttf").removeSuffix(".otf")
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                             else MaterialTheme.colorScheme.surfaceContainer
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                "أبجد هوّز Abc",
                fontSize = 20.sp,
                fontFamily = typeface?.let { FontFamily(it) },
                maxLines = 1
            )
            if (isSelected) {
                Text(
                    "✓ مُختار",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ImportPanel(onImport: (Uri, String) -> Unit) {
    var selectedLanguage by remember { mutableStateOf("Arabic") }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) onImport(uri, selectedLanguage)
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Tip("اختر اللغة التي تريد استيراد خط لها، ثم اختر ملف .ttf")

        Text("اللغة:", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedLanguage == "Arabic",
                onClick = { selectedLanguage = "Arabic" },
                label = { Text("🇸🇦 عربي") }
            )
            FilterChip(
                selected = selectedLanguage == "English",
                onClick = { selectedLanguage = "English" },
                label = { Text("🇬🇧 إنجليزي") }
            )
        }

        Button(
            onClick = {
                picker.launch(arrayOf(
                    "font/ttf", "font/otf",
                    "application/x-font-ttf",
                    "application/x-font-otf",
                    "*/*"
                ))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📂 اختيار ملف خط من الجهاز")
        }

        Text(
            "الخط سيُنسخ إلى التطبيق ويبقى حتى لو حذفت الملف الأصلي.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * يطبّق الخط فورًا عبر overrideKeyboardTypeface.
 * (يستخدم Action API عبر Interface.)
 */
/**
 * يحفظ مسار الخط في SharedPreferences.
 * LatinIME يقرأه عند بدء كل مرة.
 */
private fun applyFont(
    context: android.content.Context,
    file: java.io.File,
    language: String,
) {
    val prefs = context.getSharedPreferences("font_prefs", android.content.Context.MODE_PRIVATE)
    val key = if (language == "Arabic") "font_path_arabic" else "font_path_english"
    prefs.edit().putString(key, file.absolutePath).apply()
}
