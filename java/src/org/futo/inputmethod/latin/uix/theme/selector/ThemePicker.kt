package org.futo.inputmethod.latin.uix.theme.selector

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.*
import org.futo.inputmethod.latin.uix.actions.BugInfo
import org.futo.inputmethod.latin.uix.actions.BugViewerState
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.SettingToggleDataStore
import org.futo.inputmethod.latin.uix.settings.useDataStore
import org.futo.inputmethod.latin.uix.theme.*
import org.futo.inputmethod.latin.uix.theme.presets.*
import org.futo.inputmethod.updates.openURI
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

// ==========================================
// 1. بطاقة معاينة الثيم (مع زر القلب والتطبيق)
// ==========================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZipThemePreview(
    name: ZipThemes.ThemeFileName,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val loading = remember { mutableStateOf(true) }
    val scheme = remember { mutableStateOf<KeyboardColorScheme>(defaultThemeOption(context).obtainColors(context)) }

    LaunchedEffect(name) {
        loading.value = true
        scheme.value = defaultThemeOption(context).obtainColors(context)
        withContext(Dispatchers.Default) {
            try {
                scheme.value = ZipThemes.loadSchemeThumb(context, name)
            } catch (e: Exception) {
                BugViewerState.pushBug(BugInfo("Unable to load thumbnail", e.toString()))
            }
        }
        loading.value = false
    }

    val currColors = MaterialTheme.colorScheme
    val borderWidth = if (isSelected) 3.dp else Dp.Hairline
    val borderColor = if (isSelected) currColors.primary else currColors.outline.copy(alpha = 0.3f)
    val textColor = scheme.value.onBackground
    val keyboardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .padding(6.dp)
            .height(130.dp)
            .border(borderWidth, borderColor, keyboardShape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .clip(keyboardShape)
    ) {
        KeyboardBackground(scheme.value, useThumbnail = true)
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(scheme.value.keyboardSurfaceDim.copy(alpha = 0.7f))
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = scheme.value.extended.advancedThemeOptions.themeName ?: name.name,
                color = textColor,
                style = Typography.SmallMl,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            if (onFavoriteClick != null) {
                IconButton(onClick = onFavoriteClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFF4081) else Color.White
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
            color = scheme.value.keyboardSurfaceDim.copy(alpha = 0.8f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, null, tint = textColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("تطبيق", color = textColor, style = Typography.SmallMl)
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp, end = 8.dp).size(16.dp),
            color = scheme.value.primary,
            shape = RoundedCornerShape(4.dp)
        ) {}
    }
}

@Composable
fun ThemePreview(theme: ThemeOption, isSelected: Boolean = false, overrideName: String? = null, modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
    if (theme == DynamicSystemTheme) return DynamicThemePreview(isSelected, onClick)
    val context = LocalContext.current
    val colors = remember(theme) { theme.obtainColors(context) }
    ThemePreview(colors = colors, name = overrideName ?: stringResource(theme.name), loading = false, isSelected = isSelected, modifier = modifier, onClick = onClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemePreview(colors: KeyboardColorScheme, name: String, loading: Boolean, isSelected: Boolean = false, modifier: Modifier = Modifier, onLongClick: (() -> Unit)? = null, onClick: () -> Unit = { }) {
    val currColors = MaterialTheme.colorScheme
    val borderWidth = if (isSelected) 4.dp else Dp.Hairline
    val borderColor = if (isSelected) currColors.inversePrimary else currColors.outline
    val textColor = colors.onBackground
    val spacebarColor = colors.keyboardContainer
    val actionColor = colors.primary
    val keyboardShape = RoundedCornerShape(16.dp)

    Box(modifier = modifier.padding(6.dp).height(130.dp).border(borderWidth, borderColor, keyboardShape).combinedClickable(onClick = onClick, onLongClick = onLongClick).clip(keyboardShape)) {
        KeyboardBackground(colors, useThumbnail = true)
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = name, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.TopCenter).background(colors.keyboardSurfaceDim.copy(alpha = 0.6f)).fillMaxWidth().padding(4.dp), color = textColor, style = Typography.SmallMl)
            Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                Surface(modifier = Modifier.fillMaxWidth(0.5f).height(18.dp).align(Alignment.BottomCenter), color = spacebarColor, shape = RoundedCornerShape(4.dp)) { }
                Surface(modifier = Modifier.width(24.dp).height(18.dp).align(Alignment.BottomEnd), color = actionColor, shape = RoundedCornerShape(4.dp)) { }
            }
        }
    }
}

@Preview
@Composable
fun DynamicThemePreview(isSelected: Boolean = false, onClick: () -> Unit = { }) {
    Box {
        ThemePreview(DynamicLightTheme, isSelected, onClick = onClick, overrideName = stringResource(DynamicSystemTheme.name), modifier = Modifier.clip(GenericShape { size, _ -> addPath(Path().apply { moveTo(0f, 0f); lineTo(size.width * 0.66f, 0f); lineTo(size.width * 0.33f, size.height); lineTo(0f, size.height); close() }) }))
        ThemePreview(DynamicDarkTheme, isSelected, onClick = onClick, overrideName = stringResource(DynamicSystemTheme.name), modifier = Modifier.clip(GenericShape { size, _ -> addPath(Path().apply { moveTo(size.width * 0.66f, 0f); lineTo(size.width, 0f); lineTo(size.width, size.height); lineTo(size.width * 0.33f, size.height); close() }) }))
    }
}

@Composable
fun SearchDialog(
    allThemes: List<ZipThemes.ThemeFileName>,
    onDismiss: () -> Unit,
    onSelect: (ZipThemes.ThemeFileName) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<ZipThemes.ThemeFileName>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    
    LaunchedEffect(query) {
        if (query.isBlank()) { results = emptyList(); return@LaunchedEffect }
        isSearching = true
        withContext(Dispatchers.Default) {
            val lowerQuery = query.lowercase().trim()
            results = allThemes.filter { it.name.lowercase().contains(lowerQuery) }.take(100)
            withContext(Dispatchers.Main) { isSearching = false }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🔍 بحث") },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("اسم الثيم") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else if (query.isNotBlank() && results.isEmpty()) {
                    Text("لا توجد نتائج", modifier = Modifier.padding(8.dp))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                        items(results.size) { idx ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                onClick = { onSelect(results[idx]); onDismiss() }
                            ) {
                                Text(results[idx].name, modifier = Modifier.padding(12.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}

@Composable
fun ColorFilterRow(selectedColor: String?, onSelect: (String?) -> Unit) {
    val ThemeColors = listOf(
        "الكل" to null, "أحمر" to "red", "أزرق" to "blue", "أخضر" to "green", 
        "بنفسجي" to "purple", "وردي" to "pink", "برتقالي" to "orange", 
        "أصفر" to "yellow", "سماوي" to "cyan", "رمادي" to "gray"
    )
    LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(ThemeColors.size) { idx -> 
            val (name, key) = ThemeColors[idx]
            FilterChip(selected = selectedColor == key, onClick = { onSelect(key) }, label = { Text(name, style = MaterialTheme.typography.bodySmall) }) 
        }
    }
}

fun themeMatchesColor(themeName: String, colorKey: String?): Boolean {
    if (colorKey == null) return true
    val lower = themeName.lowercase()
    return when (colorKey) {
        "red" -> lower.contains("red") || lower.contains("crimson") || lower.contains("ruby") || lower.contains("rose")
        "blue" -> lower.contains("blue") || lower.contains("azure") || lower.contains("cobalt") || lower.contains("sapphire")
        "green" -> lower.contains("green") || lower.contains("lime") || lower.contains("emerald") || lower.contains("mint")
        "purple" -> lower.contains("purple") || lower.contains("violet") || lower.contains("magenta") || lower.contains("amethyst")
        "pink" -> lower.contains("pink") || lower.contains("rose") || lower.contains("plasma")
        "orange" -> lower.contains("orange") || lower.contains("amber") || lower.contains("peach")
        "yellow" -> lower.contains("yellow") || lower.contains("gold") || lower.contains("citrine")
        "cyan" -> lower.contains("cyan") || lower.contains("teal") || lower.contains("turquoise")
        "gray" -> lower.contains("gray") || lower.contains("grey") || lower.contains("silver") || lower.contains("graphite")
        else -> true
    }
}

enum class SortOption(val label: String) { ALPHABETICAL("أبجدي"), NEWEST("الأحدث"), MOST_USED("الأكثر استخداماً"), COLOR("حسب اللون") }

fun sortThemes(themes: List<ZipThemes.ThemeFileName>, sortBy: SortOption, context: Context): List<ZipThemes.ThemeFileName> {
    return when (sortBy) {
        SortOption.ALPHABETICAL -> themes.sortedBy { it.name.lowercase() }
        SortOption.NEWEST -> themes.sortedByDescending { it.name }
        SortOption.MOST_USED -> themes
        SortOption.COLOR -> themes.sortedBy { it.name.lowercase() }
    }
}

@Composable
fun SortDialog(currentSort: SortOption, onSelect: (SortOption) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🔀 ترتيب") },
        text = {
            Column {
                SortOption.values().forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSelect(option); onDismiss() }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = currentSort == option, onClick = { onSelect(option); onDismiss() })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option.label)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}

@Composable
fun LargePreviewDialog(theme: ZipThemes.ThemeFileName, onDismiss: () -> Unit, onApply: () -> Unit) {
    val context = LocalContext.current
    val scheme = remember { mutableStateOf<KeyboardColorScheme?>(null) }
    val loading = remember { mutableStateOf(true) }
    
    LaunchedEffect(theme) {
        withContext(Dispatchers.Default) {
            try {
                scheme.value = ZipThemes.loadSchemeThumb(context, theme)
            } catch (e: Exception) { e.printStackTrace() }
            loading.value = false
        }
    }
    
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.7f), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = theme.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                if (loading.value) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else if (scheme.value != null) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(12.dp))) {
                        ZipThemePreview(name = theme, isSelected = false, modifier = Modifier.fillMaxSize(), onLongClick = null) { }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("إلغاء") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onApply) { Text("تطبيق") }
                }
            }
        }
    }
}

fun exportCustomThemes(context: Context): File? {
    try {
        val themesDir = ZipThemes.customThemesDir(context)
        val files = themesDir.listFiles()?.filter { it.extension == "zip" } ?: return null
        if (files.isEmpty()) return null
        val exportFile = File(context.cacheDir, "futo_themes_backup_${System.currentTimeMillis()}.zip")
        ZipOutputStream(FileOutputStream(exportFile)).use { zos ->
            files.forEach { file ->
                zos.putNextEntry(ZipEntry(file.name))
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
        return exportFile
    } catch (e: Exception) { e.printStackTrace(); return null }
}

fun shareThemeExport(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "تصدير الثيمات"))
    } catch (e: Exception) { e.printStackTrace() }
}

@Composable
fun DeleteMultipleDialog(themes: List<ZipThemes.ThemeFileName>, onConfirm: (Set<String>) -> Unit, onDismiss: () -> Unit) {
    var selected by remember { mutableStateOf<Set<String>>(emptySet()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🗑️ حذف متعدد") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                Text("المحدد: ${selected.size}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                if (themes.isEmpty()) { Text("لا توجد ثيمات مخصصة للحذف") } 
                else {
                    LazyColumn {
                        items(themes.size) { idx ->
                            val theme = themes[idx]
                            Row(modifier = Modifier.fillMaxWidth().clickable {
                                selected = if (theme.name in selected) selected - theme.name else selected + theme.name
                            }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = theme.name in selected, onCheckedChange = {
                                    selected = if (it) selected + theme.name else selected - theme.name
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(theme.name, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { if (selected.isNotEmpty()) onConfirm(selected); onDismiss() }, enabled = selected.isNotEmpty()) { Text("حذف (${selected.size})", color = Color.Red) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
fun ColorEditorDialog(theme: ZipThemes.ThemeFileName, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var hue by remember { mutableStateOf(0.5f) }
    var saturation by remember { mutableStateOf(0.7f) }
    var lightness by remember { mutableStateOf(0.5f) }
    val previewColor = remember(hue, saturation, lightness) { Color.hsl(hue * 360f, saturation, lightness) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎨 تعديل الألوان: ${theme.name}") },
        text = {
            Column {
                Text("Hue"); Slider(value = hue, onValueChange = { hue = it }, valueRange = 0f..1f)
                Text("Saturation"); Slider(value = saturation, onValueChange = { saturation = it }, valueRange = 0f..1f)
                Text("Lightness"); Slider(value = lightness, onValueChange = { lightness = it }, valueRange = 0f..1f)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("المعاينة: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(60.dp).background(previewColor, RoundedCornerShape(8.dp)))
                }
            }
        },
        confirmButton = { Button(onClick = { Toast.makeText(context, "قريباً", Toast.LENGTH_SHORT).show(); onDismiss() }) { Text("حفظ") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

val AutoModeEnabledKey = SettingsKey(booleanPreferencesKey("auto_mode_enabled"), false)
val AutoModeDayThemeKey = SettingsKey(stringPreferencesKey("auto_mode_day_theme"), "")
val AutoModeNightThemeKey = SettingsKey(stringPreferencesKey("auto_mode_night_theme"), "")
val AutoModeDayStartKey = SettingsKey(intPreferencesKey("auto_mode_day_start"), 7)
val AutoModeNightStartKey = SettingsKey(intPreferencesKey("auto_mode_night_start"), 19)

fun isDayTime(context: Context): Boolean {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val dayStart = runBlocking { context.getSetting(AutoModeDayStartKey) }
    val nightStart = runBlocking { context.getSetting(AutoModeNightStartKey) }
    return hour >= dayStart && hour < nightStart
}

@Composable
fun AutoModeDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🌙 الوضع التلقائي") },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("تفعيل التبديل التلقائي")
                    Switch(checked = enabled, onCheckedChange = { enabled = it })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("يتم تبديل الثيم تلقائياً بين النهار والليل.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("النهار: 7:00 صباحاً", style = MaterialTheme.typography.bodySmall)
                Text("الليل: 7:00 مساءً", style = MaterialTheme.typography.bodySmall)
                if (enabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { Toast.makeText(context, "تم تفعيل الوضع التلقائي", Toast.LENGTH_SHORT).show(); onDismiss() }, modifier = Modifier.fillMaxWidth()) { Text("حفظ") }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}

fun shareTheme(context: Context, theme: ZipThemes.ThemeFileName) {
    try {
        val themeFile = if (theme.location == ZipThemes.ThemeLocation.Custom) File(ZipThemes.customThemesDir(context), "${theme.name}.zip") else null
        if (themeFile != null && themeFile.exists()) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", themeFile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "ثيم FUTO: ${theme.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "مشاركة الثيم"))
        } else {
            Toast.makeText(context, "لا يمكن مشاركة هذا الثيم", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) { e.printStackTrace(); Toast.makeText(context, "خطأ في المشاركة", Toast.LENGTH_SHORT).show() }
}

val ThemeUsageCountKey = SettingsKey(intPreferencesKey("theme_usage_count"), 0)
val TotalThemesAppliedKey = SettingsKey(intPreferencesKey("total_themes_applied"), 0)

fun getStats(context: Context): Map<String, Int> {
    return mapOf(
        "custom" to ZipThemes.listCustom(context).size,
        "assets" to ZipThemes.listAssets(context).size,
        "favorites" to ZipThemes.getFavorites(context).size,
        "total_applied" to runBlocking { context.getSetting(TotalThemesAppliedKey) }
    )
}

@Composable
fun StatsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val stats = remember { getStats(context) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📊 إحصائيات") },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("📦 ثيمات مخصصة"); Text("${stats["custom"]}") }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("🎨 ثيمات assets"); Text("${stats["assets"]}") }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("⭐ المفضلة"); Text("${stats["favorites"]}") }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("🖱️ تم تطبيقها"); Text("${stats["total_applied"]}") }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}

fun exportSettings(context: Context) {
    try {
        val settingsFile = File(context.cacheDir, "futo_settings_${System.currentTimeMillis()}.zip")
        ZipOutputStream(FileOutputStream(settingsFile)).use { zos ->
            val themesDir = ZipThemes.customThemesDir(context)
            themesDir.listFiles()?.forEach { file ->
                zos.putNextEntry(ZipEntry("themes/${file.name}"))
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", settingsFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "حفظ الإعدادات"))
    } catch (e: Exception) { e.printStackTrace(); Toast.makeText(context, "خطأ في حفظ الإعدادات", Toast.LENGTH_SHORT).show() }
}

// ==========================================
// 3. الشاشة الرئيسية (ThemePicker) - التصميم الجديد
// ==========================================
@Composable
fun ThemePicker(
    onDeleteCustomTheme: (String) -> Unit, 
    onCustomTheme: () -> Unit,
    showKeyboard: Boolean = false,
    onToggleKeyboard: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentTheme = useDataStore(THEME_KEY.key, "").value.trimEnd('_')
    val isInspecting = LocalInspectionMode.current
    
    val availableThemeOptions = remember {
        ThemeOptionKeys.mapNotNull { key -> getThemeOption(context, key)?.let { Pair(key, it) } }
            .filter { it.second.available(context) }
            .filter { if (isInspecting) !it.second.dynamic else true }
    }

    val originalDirection = LocalLayoutDirection.current
    val customThemes = remember(ZipThemes.updateCount.intValue) { ZipThemes.listCustom(context) }
    val allAssetThemes = remember { ZipThemes.listAssets(context) }

    val lifecycle = LocalLifecycleOwner.current
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    // الحالات
    var showSearchDialog by remember { mutableStateOf(false) }
    var contextMenuTheme by remember { mutableStateOf<ZipThemes.ThemeFileName?>(null) }
    var favoritesVersion by remember { mutableIntStateOf(0) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var sortBy by remember { mutableStateOf(SortOption.ALPHABETICAL) }
    var showSortDialog by remember { mutableStateOf(false) }
    var previewTheme by remember { mutableStateOf<ZipThemes.ThemeFileName?>(null) }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    var showSettingsMenu by remember { mutableStateOf(false) } 
    var activeTab by remember { mutableIntStateOf(0) } 

    // فلترة
    val favorites = remember(favoritesVersion) { ZipThemes.getFavorites(context) }
    val filteredAssetThemes = remember(selectedColor, sortBy, showFavoritesOnly, favoritesVersion) {
        var filtered = if (selectedColor == null) allAssetThemes else allAssetThemes.filter { themeMatchesColor(it.name, selectedColor) }
        if (showFavoritesOnly) filtered = filtered.filter { it.name in favorites }
        sortThemes(filtered, sortBy, context)
    }
    val filteredCustomThemes = remember(showFavoritesOnly, favoritesVersion) {
        if (showFavoritesOnly) customThemes.filter { it.name in favorites } else customThemes
    }

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            val (success, fail) = withContext(Dispatchers.IO) { ZipThemes.importThemeFolder(context, uri) }
            withContext(Dispatchers.Main) { Toast.makeText(context, "تم استيراد: $success\nفشل: $fail", Toast.LENGTH_LONG).show() }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column {
            // --- 1. شريط البحث الرئيسي ---
            OutlinedTextField(
                value = "", 
                onValueChange = { showSearchDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("البحث في الثيمات...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(24.dp),
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            // --- 2. أزرار التبديل (مدمج / خاصتي) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    label = { Text("مدمج") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    label = { Text("خاصتي") },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // --- 3. أزرار الألوان ---
            ColorFilterRow(selectedColor = selectedColor, onSelect = { selectedColor = it })

            // --- 4. عنوان المفضلة ---
            if (showFavoritesOnly) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700))
                    Spacer(Modifier.width(8.dp))
                    Text("المفضلة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                LazyVerticalGrid(
                    state = gridState,
                    modifier = Modifier.fillMaxWidth(),
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    horizontalArrangement = if (originalDirection == LayoutDirection.Rtl) Arrangement.End else Arrangement.Start,
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // قسم الثيمات (يظهر حسب التبويب المختار)
                    if (activeTab == 0) {
                        // --- عرض الثيمات المدمجة ---
                        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.GridView, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("جميع الثيمات المدمجة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Surface(
                                    onClick = { showSortDialog = true },
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("ترتيب حسب", style = MaterialTheme.typography.bodySmall)
                                        Icon(Icons.Default.KeyboardArrowDown, null)
                                    }
                                }
                            }
                        }

                        items(filteredAssetThemes) { name ->
                            ZipThemePreview(
                                name = name,
                                isSelected = currentTheme == name.toSetting(),
                                onLongClick = { previewTheme = name },
                                isFavorite = name.name in favorites,
                                onFavoriteClick = { ZipThemes.toggleFavorite(context, name.name); favoritesVersion++ }
                            ) { lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, name.toSetting()) } }
                        }

                        items(availableThemeOptions.size) {
                            val themeOption = availableThemeOptions[it].second
                            ThemePreview(themeOption, isSelected = themeOption.key == currentTheme) {
                                lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, themeOption.key) }
                            }
                        }
                    } else {
                        // --- عرض الثيمات الخاصة بك ---
                        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        if (filteredCustomThemes.isEmpty()) "الثيمات الخاصة بك (0)" else "الثيمات الخاصة بك (${filteredCustomThemes.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (filteredCustomThemes.isEmpty()) {
                            item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp).border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Home, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text("لا توجد ثيمات مخصصة بعد", fontWeight = FontWeight.Bold)
                                        Text("قم بحفظ ثيماتك المفضلة هنا", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        } else {
                            items(filteredCustomThemes.size) {
                                val name = filteredCustomThemes[it]
                                ZipThemePreview(
                                    name = name,
                                    isSelected = currentTheme == name.toSetting(),
                                    onLongClick = { contextMenuTheme = name },
                                    isFavorite = name.name in favorites,
                                    onFavoriteClick = { ZipThemes.toggleFavorite(context, name.name); favoritesVersion++ }
                                ) { lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, name.toSetting()) } }
                            }
                        }
                    }
                }
            }
        }

        // --- زر تجربة الثيم (السهم) + زر الترس ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .imePadding()
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            // 1. زر تجربة الثيم (السهم)
            FloatingActionButton(
                onClick = onToggleKeyboard,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (showKeyboard) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Toggle Keyboard Preview"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. زر الترس (القائمة المنسدلة)
            Box {
                FloatingActionButton(
                    onClick = { showSettingsMenu = !showSettingsMenu },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Settings, "Settings")
                }

                DropdownMenu(
                    expanded = showSettingsMenu,
                    onDismissRequest = { showSettingsMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = { Text("🔍 بحث") },
                        onClick = { showSettingsMenu = false; showSearchDialog = true }
                    )
                    DropdownMenuItem(
                        text = { Text("➕ إضافة ثيم") },
                        onClick = { showSettingsMenu = false; onCustomTheme() }
                    )
                    DropdownMenuItem(
                        text = { Text("🎲 ثيم عشوائي (نرد)") },
                        onClick = {
                            showSettingsMenu = false
                            val random = ZipThemes.getRandomTheme(context)
                            if (random != null) lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, random.toSetting()) }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("⭐ عرض المفضلة") },
                        onClick = { showSettingsMenu = false; showFavoritesOnly = !showFavoritesOnly }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text("📁 استيراد مجلد") },
                        onClick = { showSettingsMenu = false; folderPicker.launch(null) }
                    )
                    DropdownMenuItem(
                        text = { Text("📤 تصدير الثيمات") },
                        onClick = { 
                            showSettingsMenu = false
                            val file = exportCustomThemes(context)
                            if (file != null) shareThemeExport(context, file) 
                        }
                    )
                }
            }
        }
    }

    // --- النوافذ المنبثقة (Dialogs) ---
    if (showSearchDialog) SearchDialog(customThemes + allAssetThemes, { showSearchDialog = false }, { theme -> lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, theme.toSetting()) } })
    if (showSortDialog) SortDialog(sortBy, { sortBy = it }, { showSortDialog = false })

    contextMenuTheme?.let { theme ->
        val isFav = theme.name in favorites
        AlertDialog(
            onDismissRequest = { contextMenuTheme = null },
            title = { Text(theme.name) },
            text = {
                Column {
                    TextButton(onClick = { ZipThemes.toggleFavorite(context, theme.name); favoritesVersion++; contextMenuTheme = null }, modifier = Modifier.fillMaxWidth()) {
                        Icon(if (isFav) Icons.Default.Star else Icons.Default.StarBorder, null); Spacer(Modifier.width(8.dp)); Text(if (isFav) "إزالة من المفضلة" else "إضافة للمفضلة")
                    }
                    TextButton(onClick = { shareTheme(context, theme); contextMenuTheme = null }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text("مشاركة") }
                    TextButton(onClick = { ZipThemes.delete(context, theme); favoritesVersion++; contextMenuTheme = null }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Delete, null, tint = Color.Red); Spacer(Modifier.width(8.dp)); Text("حذف", color = Color.Red) }
                }
            },
            confirmButton = { TextButton(onClick = { contextMenuTheme = null }) { Text("إغلاق") } }
        )
    }

    previewTheme?.let { theme ->
        LargePreviewDialog(theme, onDismiss = { previewTheme = null }, onApply = { lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, theme.toSetting()) }; previewTheme = null })
    }
}

@Preview
@Composable
private fun ThemePickerPreview() {
    Column {
        UixThemeWrapper(VoiceInputTheme.obtainColors(LocalContext.current)) { Surface(color = MaterialTheme.colorScheme.background) { ThemePicker({},{}) } }
        UixThemeWrapper(ClassicMaterialDark.obtainColors(LocalContext.current)) { Surface(color = MaterialTheme.colorScheme.background) { ThemePicker({},{}) } }
    }
}