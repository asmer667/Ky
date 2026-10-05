import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
package org.futo.inputmethod.latin.uix.theme.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.KeyBordersSetting
import org.futo.inputmethod.latin.uix.KeyboardBackground
import org.futo.inputmethod.latin.uix.KeyboardColorScheme
import org.futo.inputmethod.latin.uix.THEME_KEY
import org.futo.inputmethod.latin.uix.actions.BugInfo
import org.futo.inputmethod.latin.uix.actions.BugViewerState
import org.futo.inputmethod.latin.uix.setSetting
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.SettingToggleDataStore
import org.futo.inputmethod.latin.uix.settings.useDataStore
import org.futo.inputmethod.latin.uix.theme.ZipThemes
import org.futo.inputmethod.latin.uix.theme.ThemeOption
import org.futo.inputmethod.latin.uix.theme.ThemeOptionKeys
import org.futo.inputmethod.latin.uix.theme.Typography
import org.futo.inputmethod.latin.uix.theme.UixThemeWrapper
import org.futo.inputmethod.latin.uix.theme.defaultThemeOption
import org.futo.inputmethod.latin.uix.theme.getThemeOption
import org.futo.inputmethod.latin.uix.theme.presets.AMOLEDDarkPurple
import org.futo.inputmethod.latin.uix.theme.presets.ClassicMaterialDark
import org.futo.inputmethod.latin.uix.theme.presets.DefaultLightScheme
import org.futo.inputmethod.latin.uix.theme.presets.DynamicDarkTheme
import org.futo.inputmethod.latin.uix.theme.presets.DynamicLightTheme
import org.futo.inputmethod.latin.uix.theme.presets.DynamicSystemTheme
import org.futo.inputmethod.latin.uix.theme.presets.VoiceInputTheme
import org.futo.inputmethod.updates.openURI
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.RadioButton
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.OutlinedButton
import java.util.Calendar
import androidx.compose.material3.Switch
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import org.futo.inputmethod.latin.uix.SettingsKey
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.isCtrlPressed
import android.content.Context
import kotlinx.coroutines.runBlocking
import org.futo.inputmethod.latin.uix.getSetting
import kotlinx.coroutines.Job

@Composable
fun ThemePreview(theme: ThemeOption, isSelected: Boolean = false, overrideName: String? = null, modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
    if(theme == DynamicSystemTheme) return DynamicThemePreview(isSelected, onClick)

    val context = LocalContext.current
    val colors = remember(theme) { theme.obtainColors(context) }

    return ThemePreview(
        colors = colors,
        name = overrideName ?: stringResource(theme.name),
        loading = false,
        isSelected = isSelected,
        modifier = modifier,
        onClick = onClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemePreview(colors: KeyboardColorScheme, name: String, loading: Boolean, isSelected: Boolean = false, modifier: Modifier = Modifier, onLongClick: (() -> Unit)? = null, onClick: () -> Unit = { }) {
    val currColors = MaterialTheme.colorScheme

    val borderWidth = if (isSelected) {
        4.dp
    } else {
        Dp.Hairline
    }

    val borderColor = if (isSelected) {
        currColors.inversePrimary
    } else {
        currColors.outline
    }

    val textColor = colors.onBackground

    val spacebarColor = colors.keyboardContainer
    val actionColor = colors.primary

    val keyboardShape = RoundedCornerShape(8.dp)

    val previewModifier = if(LocalInspectionMode.current) {
        modifier.width(172.dp)
    } else {
        modifier
    }

    Box(
        modifier = previewModifier
            .padding(12.dp)
            .height(128.dp)
            .border(borderWidth, borderColor, keyboardShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .clip(keyboardShape),
    ) {
        KeyboardBackground(colors, useThumbnail = true)
        Box(modifier = Modifier.fillMaxSize()) {
            // Theme name and action bar
            Text(
                text = name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(colors.keyboardSurfaceDim.copy(
                        alpha = if(colors.extended.advancedThemeOptions.thumbnailImage == null) {
                            1.0f
                        } else {
                            0.4f
                        }
                    ))
                    .fillMaxWidth()
                    .padding(4.dp),
                color = textColor,
                style = Typography.SmallMl
            )

            // Keyboard contents
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Spacebar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(18.dp)
                        .align(Alignment.BottomCenter),
                    color = spacebarColor,
                    shape = RoundedCornerShape(4.dp)
                ) { }

                // Enter key
                Surface(
                    modifier = Modifier
                        .width(24.dp)
                        .height(18.dp)
                        .align(Alignment.BottomEnd)
                        .padding(0.dp, 1.dp),
                    color = actionColor,
                    shape = RoundedCornerShape(4.dp)
                ) { }
            }
        }
    }
}


@Composable
fun ZipThemePreview(name: ZipThemes.ThemeFileName, isSelected: Boolean, modifier: Modifier, onLongClick: (() -> Unit)? = null, onClick: () -> Unit) {
    val context = LocalContext.current

    val loading = remember { mutableStateOf(true) }
    val scheme = remember { mutableStateOf<KeyboardColorScheme>(defaultThemeOption(context).obtainColors(context)) }

    LaunchedEffect(name) {
        loading.value = true
        scheme.value = defaultThemeOption(context).obtainColors(context)
        withContext(Dispatchers.Default) {
            try {
                scheme.value = ZipThemes.loadSchemeThumb(context, name)
            }catch(e: Exception) {
                BugViewerState.pushBug(BugInfo(
                    name = "Unable to load thumbnail for $name",
                    details = e.toString(),
                ))
            }
        }
        loading.value = false
    }

    ThemePreview(
        colors = scheme.value,
        name = scheme.value.extended.advancedThemeOptions.themeName ?: stringResource(R.string.theme_custom_named, name.name),
        loading = loading.value,
        isSelected = isSelected,
        modifier = modifier,
        onLongClick = onLongClick,
        onClick = onClick,
    )
}

// Special case to demonstrate the light and dark mode
@Preview
@Composable
fun DynamicThemePreview(isSelected: Boolean = false, onClick: () -> Unit = { }) {
    Box {
        ThemePreview(
            DynamicLightTheme,
            isSelected = isSelected,
            onClick = onClick,
            overrideName = stringResource(DynamicSystemTheme.name),
            modifier = Modifier.clip(GenericShape { size, _ ->
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width * 0.66f, 0f)
                    lineTo(size.width * 0.33f, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                addPath(path)
            })
        )
        ThemePreview(
            DynamicDarkTheme,
            isSelected = isSelected,
            onClick = onClick,
            overrideName = stringResource(DynamicSystemTheme.name),
            modifier = Modifier.clip(GenericShape { size, _ ->
                val path = Path().apply {
                    moveTo(size.width * 0.66f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(size.width * 0.33f, size.height)
                    close()
                }
                addPath(path)
            })
        )
    }
}

@Composable
fun AddCustomThemeButton(short: Boolean = false, onClick: () -> Unit = { }) {
    val currColors = MaterialTheme.colorScheme

    val keyboardShape = RoundedCornerShape(8.dp)

    Surface(
        modifier = Modifier
            .padding(12.dp)
            .width(172.dp)
            .height(if(short) 64.dp else 128.dp ),
        onClick = { onClick() },
        color = currColors.surfaceVariant,
        shape = keyboardShape
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(R.string.theme_settings_add_new_theme),
                modifier = Modifier
                    .size(if(short) 32.dp else 48.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun VisitThemeStoreButton(short: Boolean = false) {
    val context = LocalContext.current
    val currColors = MaterialTheme.colorScheme

    val keyboardShape = RoundedCornerShape(8.dp)

    Surface(
        modifier = Modifier
            .padding(12.dp)
            .width(172.dp)
            .height(if(short) 64.dp else 128.dp ),
        onClick = {
            context.openURI("https://keyboard.futo.tech/themes", true)
        },
        color = currColors.surfaceVariant,
        shape = keyboardShape
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                painterResource(R.drawable.compass),
                contentDescription = stringResource(R.string.theme_settings_visit_theme_store),
                modifier = Modifier
                    .size(if(short) 32.dp else 48.dp)
                    .align(Alignment.Center)
            )
        }
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
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // البحث عند تغيير query
    LaunchedEffect(query) {
        if (query.isBlank()) {
            results = emptyList()
            return@LaunchedEffect
        }
        isSearching = true
        withContext(kotlinx.coroutines.Dispatchers.Default) {
            val lowerQuery = query.lowercase().trim()
            val filtered = allThemes.filter { theme ->
                theme.name.lowercase().contains(lowerQuery)
            }.take(100)  // حد أقصى 100 نتيجة
            withContext(kotlinx.coroutines.Dispatchers.Main) {
                results = filtered
                isSearching = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "🔍 بحث في الثيمات", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("اسم الثيم") },
                    placeholder = { Text("اكتب اسم الثيم...") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else if (query.isBlank()) {
                    Text(
                        "اكتب اسم ثيم للبحث...",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                } else if (results.isEmpty()) {
                    Text(
                        "❌ لا توجد نتائج لـ: $query",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    Text(
                        "✅ ${results.size} نتيجة",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        items(results.size) { idx ->
                            val theme = results[idx]
                            androidx.compose.material3.Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                onClick = {
                                    onSelect(theme)
                                    onDismiss()
                                }
                            ) {
                                Text(
                                    theme.name,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

// ─────── MoreMenuButton ───────
@Composable
fun MoreMenuButton(
    context: Context,
    onImportFolder: () -> Unit,
    onExportThemes: () -> Unit,
    onFilterByColor: () -> Unit,
    onSort: () -> Unit,
    onFavorites: () -> Unit,
    onStats: () -> Unit,
    onSettings: () -> Unit,
    onAutoMode: () -> Unit,
    onCustomBackground: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        FloatingActionButton(
            onClick = { expanded = true },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ) {
            Icon(Icons.Default.MoreVert, "More options")
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("📁 استيراد مجلد كامل") },
                onClick = { expanded = false; onImportFolder() },
                leadingIcon = { Icon(Icons.Default.Add, null) }
            )
            DropdownMenuItem(
                text = { Text("📤 تصدير الثيمات") },
                onClick = { expanded = false; onExportThemes() },
                leadingIcon = { Icon(Icons.Default.Share, null) }
            )
            Divider()
            DropdownMenuItem(
                text = { Text("🎨 تصنيف بالألوان") },
                onClick = { expanded = false; onFilterByColor() }
            )
            DropdownMenuItem(
                text = { Text("🔀 ترتيب") },
                onClick = { expanded = false; onSort() }
            )
            DropdownMenuItem(
                text = { Text("⭐ المفضلة") },
                onClick = { expanded = false; onFavorites() }
            )
            Divider()
            DropdownMenuItem(
                text = { Text("📊 إحصائيات") },
                onClick = { expanded = false; onStats() }
            )
            DropdownMenuItem(
                text = { Text("💾 حفظ/استيراد إعدادات") },
                onClick = { expanded = false; onSettings() }
            )
            Divider()
            DropdownMenuItem(
                text = { Text("🌙 الوضع التلقائي") },
                onClick = { expanded = false; onAutoMode() }
            )
            DropdownMenuItem(
                text = { Text("🖼️ خلفية مخصصة") },
                onClick = { expanded = false; onCustomBackground() }
            )
        }
    }
}

// ─────── RandomThemeButton ───────
@Composable
fun RandomThemeButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Icon(Icons.Default.Refresh, "Random theme")
    }
}

// ─────── FavoriteButton ───────
@Composable
fun FavoriteIconButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.Star,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─────── Color Filter Row ───────
val ThemeColors = listOf(
    "الكل" to null,
    "أحمر" to "red",
    "أزرق" to "blue",
    "أخضر" to "green",
    "بنفسجي" to "purple",
    "وردي" to "pink",
    "برتقالي" to "orange",
    "أصفر" to "yellow",
    "سماوي" to "cyan",
    "رمادي" to "gray",
)

fun themeMatchesColor(themeName: String, colorKey: String?): Boolean {
    if (colorKey == null) return true
    val lower = themeName.lowercase()
    return when (colorKey) {
        "red" -> lower.contains("red") || lower.contains("crimson") || lower.contains("ruby") || lower.contains("rose")
        "blue" -> lower.contains("blue") || lower.contains("azure") || lower.contains("cobalt") || lower.contains("sapphire")
        "green" -> lower.contains("green") || lower.contains("lime") || lower.contains("emerald") || lower.contains("mint") || lower.contains("jade")
        "purple" -> lower.contains("purple") || lower.contains("violet") || lower.contains("magenta") || lower.contains("amethyst") || lower.contains("lavender")
        "pink" -> lower.contains("pink") || lower.contains("rose") || lower.contains("plasma")
        "orange" -> lower.contains("orange") || lower.contains("amber") || lower.contains("peach") || lower.contains("copper")
        "yellow" -> lower.contains("yellow") || lower.contains("gold") || lower.contains("citrine")
        "cyan" -> lower.contains("cyan") || lower.contains("teal") || lower.contains("turquoise")
        "gray" -> lower.contains("gray") || lower.contains("grey") || lower.contains("silver") || lower.contains("graphite") || lower.contains("titanium")
        else -> true
    }
}

@Composable
fun ColorFilterRow(
    selectedColor: String?,
    onSelect: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ThemeColors.size) { idx ->
            val (name, key) = ThemeColors[idx]
            FilterChip(
                selected = selectedColor == key,
                onClick = { onSelect(key) },
                label = { Text(name, style = MaterialTheme.typography.bodySmall) }
            )
        }
    }
}

// ─────── Large Preview Dialog ───────
@Composable
fun LargePreviewDialog(
    theme: ZipThemes.ThemeFileName,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    val context = LocalContext.current
    val scheme = remember { mutableStateOf<KeyboardColorScheme?>(null) }
    val loading = remember { mutableStateOf(true) }
    
    LaunchedEffect(theme) {
        withContext(Dispatchers.Default) {
            try {
                scheme.value = ZipThemes.loadSchemeThumb(context, theme)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            loading.value = false
        }
    }
    
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        androidx.compose.material3.Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (loading.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                } else if (scheme.value != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        ZipThemePreview(
                            name = theme,
                            isSelected = false,
                            modifier = Modifier.fillMaxSize(),
                            onLongClick = null
                        ) { }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = onDismiss
                    ) {
                        Text("إلغاء")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Button(
                        onClick = onApply
                    ) {
                        Text("تطبيق")
                    }
                }
            }
        }
    }
}

// ─────── Sort Options ───────
enum class SortOption(val label: String) {
    ALPHABETICAL("أبجدي"),
    NEWEST("الأحدث"),
    MOST_USED("الأكثر استخداماً"),
    COLOR("حسب اللون"),
}

fun sortThemes(
    themes: List<ZipThemes.ThemeFileName>,
    sortBy: SortOption,
    context: Context
): List<ZipThemes.ThemeFileName> {
    return when (sortBy) {
        SortOption.ALPHABETICAL -> themes.sortedBy { it.name.lowercase() }
        SortOption.NEWEST -> themes.sortedByDescending { it.name }
        SortOption.MOST_USED -> themes // TODO: Track usage
        SortOption.COLOR -> themes.sortedBy { it.name.lowercase() }
    }
}

// ─────── Sort Dialog ───────
@Composable
fun SortDialog(
    currentSort: SortOption,
    onSelect: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🔀 ترتيب الثيمات") },
        text = {
            Column {
                SortOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option); onDismiss() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSort == option,
                            onClick = { onSelect(option); onDismiss() }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option.label)
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

// ─────── Export Themes ───────
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
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun shareThemeExport(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "تصدير الثيمات"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// ─────── Delete Multiple Dialog ───────
@Composable
fun DeleteMultipleDialog(
    themes: List<ZipThemes.ThemeFileName>,
    onConfirm: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🗑️ حذف متعدد") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                Text(
                    "المحدد: ${selected.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (themes.isEmpty()) {
                    Text("لا توجد ثيمات مخصصة للحذف")
                } else {
                    androidx.compose.foundation.lazy.LazyColumn {
                        items(themes.size) { idx ->
                            val theme = themes[idx]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selected = if (theme.name in selected) {
                                            selected - theme.name
                                        } else {
                                            selected + theme.name
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = theme.name in selected,
                                    onCheckedChange = {
                                        selected = if (it) {
                                            selected + theme.name
                                        } else {
                                            selected - theme.name
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(theme.name, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = {
                    if (selected.isNotEmpty()) {
                        onConfirm(selected)
                    }
                    onDismiss()
                },
                enabled = selected.isNotEmpty()
            ) {
                Text("حذف (${selected.size})", color = Color.Red)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

// ─────── Color Editor Dialog ───────
@Composable
fun ColorEditorDialog(
    theme: ZipThemes.ThemeFileName,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hue by remember { mutableStateOf(0.5f) }
    var saturation by remember { mutableStateOf(0.7f) }
    var lightness by remember { mutableStateOf(0.5f) }
    
    // معاينة اللون
    val previewColor = remember(hue, saturation, lightness) {
        Color.hsl(hue * 360f, saturation, lightness)
    }
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎨 تعديل الألوان: ${theme.name}") },
        text = {
            Column {
                Text("Hue (درجة اللون)")
                Slider(
                    value = hue,
                    onValueChange = { hue = it },
                    valueRange = 0f..1f
                )
                
                Text("Saturation (التشبع)")
                Slider(
                    value = saturation,
                    onValueChange = { saturation = it },
                    valueRange = 0f..1f
                )
                
                Text("Lightness (الإضاءة)")
                Slider(
                    value = lightness,
                    onValueChange = { lightness = it },
                    valueRange = 0f..1f
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // معاينة اللون
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("المعاينة: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(previewColor, RoundedCornerShape(8.dp))
                    )
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = {
                    Toast.makeText(context, "تطبيق الألوان - قريباً", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

// ─────── Auto Mode Config ───────
val AutoModeEnabledKey = SettingsKey(booleanPreferencesKey("auto_mode_enabled"), false)
val AutoModeDayThemeKey = SettingsKey(stringPreferencesKey("auto_mode_day_theme"), "")
val AutoModeNightThemeKey = SettingsKey(stringPreferencesKey("auto_mode_night_theme"), "")
val AutoModeDayStartKey = SettingsKey(androidx.datastore.preferences.core.intPreferencesKey("auto_mode_day_start"), 7) // 7 صباحاً
val AutoModeNightStartKey = SettingsKey(androidx.datastore.preferences.core.intPreferencesKey("auto_mode_night_start"), 19) // 7 مساءً

fun isDayTime(context: Context): Boolean {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val dayStart = runBlocking { context.getSetting(AutoModeDayStartKey) }
    val nightStart = runBlocking { context.getSetting(AutoModeNightStartKey) }
    return hour >= dayStart && hour < nightStart
}

// ─────── Auto Mode Dialog ───────
@Composable
fun AutoModeDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(false) }
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🌙 الوضع التلقائي") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("تفعيل التبديل التلقائي")
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it }
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "عند التفعيل، يتم تبديل الثيم تلقائياً بين النهار والليل.",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "النهار: 7:00 صباحاً",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "الليل: 7:00 مساءً",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (enabled) {
                    androidx.compose.material3.Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "تم تفعيل الوضع التلقائي",
                                Toast.LENGTH_SHORT
                            ).show()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("حفظ")
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

// ─────── Share Theme ───────
fun shareTheme(context: Context, theme: ZipThemes.ThemeFileName) {
    try {
        val themeFile = if (theme.location == ZipThemes.ThemeLocation.Custom) {
            java.io.File(ZipThemes.customThemesDir(context), "${theme.name}.zip")
        } else {
            null
        }
        
        if (themeFile != null && themeFile.exists()) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                themeFile
            )
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
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "خطأ في المشاركة", Toast.LENGTH_SHORT).show()
    }
}

// ─────── Stats ───────
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
fun StatsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val stats = remember { getStats(context) }
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📊 إحصائيات") },
        text = {
            Column {
                StatRow("📦 ثيمات مخصصة", stats["custom"] ?: 0)
                StatRow("🎨 ثيمات assets", stats["assets"] ?: 0)
                StatRow("⭐ المفضلة", stats["favorites"] ?: 0)
                StatRow("🖱️ تم تطبيقها", stats["total_applied"] ?: 0)
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

@Composable
fun StatRow(label: String, value: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}

// ─────── Settings Export/Import ───────
fun exportSettings(context: Context) {
    try {
        val settingsFile = java.io.File(
            context.cacheDir,
            "futo_settings_${System.currentTimeMillis()}.zip"
        )
        java.util.zip.ZipOutputStream(java.io.FileOutputStream(settingsFile)).use { zos ->
            // حفظ نسخة من themes dir
            val themesDir = ZipThemes.customThemesDir(context)
            themesDir.listFiles()?.forEach { file ->
                zos.putNextEntry(java.util.zip.ZipEntry("themes/${file.name}"))
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
        
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            settingsFile
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "حفظ الإعدادات"))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "خطأ في حفظ الإعدادات", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ThemePicker(onDeleteCustomTheme: (String) -> Unit, onCustomTheme: () -> Unit) {
    val context = LocalContext.current
    val currentTheme = useDataStore(THEME_KEY.key, "").value.trimEnd('_')

    val isInspecting = LocalInspectionMode.current
    val availableThemeOptions = remember {
        ThemeOptionKeys.mapNotNull { key ->
            getThemeOption(context, key)?.let { Pair(key, it) }
        }.filter {
            it.second.available(context)
        }.filter {
            when (isInspecting) {
                true -> !it.second.dynamic
                else -> true
            }
        }
    }

    val originalDirection = LocalLayoutDirection.current
    val customThemes = remember(ZipThemes.updateCount.intValue) {
        ZipThemes.listCustom(context)
    }
    val allAssetThemes = remember { ZipThemes.listAssets(context) }

    val lifecycle = LocalLifecycleOwner.current
    val gridState = rememberLazyGridState()
    var showSearchDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var contextMenuTheme by remember { mutableStateOf<ZipThemes.ThemeFileName?>(null) }
    var favoritesVersion by remember { mutableStateOf(0) }
    var showDeleteMultiple by remember { mutableStateOf(false) }
    var colorEditTheme by remember { mutableStateOf<ZipThemes.ThemeFileName?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var sortBy by remember { mutableStateOf(SortOption.ALPHABETICAL) }
        
    var showSortDialog by remember { mutableStateOf(false) }
    var previewTheme by remember { mutableStateOf<ZipThemes.ThemeFileName?>(null) }
    var showStats by remember { mutableStateOf(false) }
    var showAutoMode by remember { mutableStateOf(false) }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    val assetThemes = remember(selectedColor, sortBy, showFavoritesOnly) {
        var filtered = if (selectedColor == null) allAssetThemes
        else allAssetThemes.filter { themeMatchesColor(it.name, selectedColor) }
        
        if (showFavoritesOnly) {
            val favorites = ZipThemes.getFavorites(context)
            filtered = filtered.filter { it.name in favorites }
        }
        
        sortThemes(filtered, sortBy, context)
    }
    // backgroundPicker معطّل (يسبب كراش في ActionWindow)
    // TODO: إعادة تفعيله بطريقة آمنة
    var scrollJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var isScrolling by remember { mutableStateOf(false) }
    var scrollRunnable by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    // Import folder launcher
    val scope = rememberCoroutineScope()
    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            val (success, fail) = withContext(Dispatchers.IO) {
                ZipThemes.importThemeFolder(context, uri)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "تم استيراد: $success\nفشل: $fail",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    val totalItems = 2 + customThemes.size + 2 + assetThemes.size + availableThemeOptions.size

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                LazyVerticalGrid(
                    state = gridState,
                    modifier = Modifier.fillMaxWidth(),
                    columns = GridCells.Adaptive(minSize = 172.dp),
                    horizontalArrangement = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                        Arrangement.End
                    } else {
                        Arrangement.Start
                    }
                    // 🎚️ Slider للتنقل السريع
                ) {
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                            Text(
                                "📍 ${gridState.firstVisibleItemIndex + 1} / $totalItems",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Slider(
                                value = gridState.firstVisibleItemIndex.toFloat(),
                                onValueChange = { newValue ->
                                    scrollJob?.cancel()
                                    scrollJob = scope.launch {
                                        gridState.scrollToItem(
                                            newValue.toInt().coerceIn(0, totalItems - 1)
                                        )
                                    }
                                },
                                valueRange = 0f..(totalItems - 1).toFloat().coerceAtLeast(1f),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // 🎨 فلتر الألوان
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        ColorFilterRow(
                            selectedColor = selectedColor,
                            onSelect = { selectedColor = it }
                        )
                    }
                    

                    // ⭐ Custom themes
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        ScreenTitle(
                            if (customThemes.isEmpty())
                                stringResource(R.string.theme_settings_custom_themes)
                            else
                                "⭐ " + stringResource(R.string.theme_settings_custom_themes) + " (${customThemes.size})"
                        )
                    }

                    items(customThemes.size) {
                        val name = customThemes[it]
                        ZipThemePreview(name, isSelected = currentTheme == name.toSetting(), modifier = Modifier, onLongClick = {
                            contextMenuTheme = name
                        }) {
                            lifecycle.lifecycleScope.launch {
                                context.setSetting(THEME_KEY, name.toSetting())
                            }
                        }
                    }

                    item(span = { GridItemSpan(maxCurrentLineSpan) }) { }

                    // Default themes
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        ScreenTitle(stringResource(R.string.theme_settings_default_themes))
                    }
                    items(assetThemes) { name ->
                        ZipThemePreview(name, isSelected = currentTheme == name.toSetting(), modifier = Modifier, onLongClick = {
                            previewTheme = name
                        }) {
                            lifecycle.lifecycleScope.launch {
                                context.setSetting(THEME_KEY, name.toSetting())
                            }
                        }
                    }

                    items(availableThemeOptions.size) {
                        val themeOption = availableThemeOptions[it].second
                        ThemePreview(themeOption, isSelected = themeOption.key == currentTheme) {
                            lifecycle.lifecycleScope.launch {
                                context.setSetting(THEME_KEY, themeOption.key)
                            }
                        }
                    }

                    item(span = { GridItemSpan(maxCurrentLineSpan) }) { }
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        if(ZipThemes.ThemeFileName.fromSetting(currentTheme) == null) {
                            CompositionLocalProvider(LocalLayoutDirection provides originalDirection) {
                                SettingToggleDataStore(
                                    title = stringResource(R.string.theme_settings_key_borders),
                                    setting = KeyBordersSetting
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 80.dp),
            horizontalAlignment = Alignment.End
        ) {
            RandomThemeButton(
                onClick = {
                    val random = ZipThemes.getRandomTheme(context)
                    if (random != null) {
                        lifecycle.lifecycleScope.launch {
                            context.setSetting(THEME_KEY, random.toSetting())
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MoreMenuButton(
                context = context,
                onImportFolder = { folderPicker.launch(null) },
                onExportThemes = {
                    val file = exportCustomThemes(context)
                    if (file != null) {
                        shareThemeExport(context, file)
                    } else {
                        Toast.makeText(context, "لا توجد ثيمات مخصصة للتصدير", Toast.LENGTH_SHORT).show()
                    }
                },
                onFilterByColor = {
                    Toast.makeText(context, "تصنيف بالألوان - قريباً", Toast.LENGTH_SHORT).show()
                },
                onSort = {
                    showSortDialog = true
                },
                onFavorites = {
                    showFavoritesOnly = !showFavoritesOnly
                    Toast.makeText(
                        context,
                        if (showFavoritesOnly) "عرض المفضلة فقط" else "عرض كل الثيمات",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onStats = {
                    showStats = true
                },
                onSettings = {
                    exportSettings(context)
                },
                onAutoMode = {
                    showAutoMode = true
                },
                onCustomBackground = {
                    Toast.makeText(context, "الخلفية المخصصة - قريباً", Toast.LENGTH_SHORT).show()
                }
            )
        }
        
        // ────── Floating Slider (عائم) ──────
        val sliderInteractionSource = remember { MutableInteractionSource() }
        val isSliderPressed by sliderInteractionSource.collectIsPressedAsState()
        
        LaunchedEffect(isSliderPressed) {
            if (isSliderPressed) {
                isScrolling = true
                scrollRunnable?.cancel()
            } else {
                scrollRunnable?.cancel()
                scrollRunnable = scope.launch {
                    kotlinx.coroutines.delay(1500L)
                    isScrolling = false
                }
            }
        }
        androidx.compose.animation.AnimatedVisibility(
            visible = isScrolling,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            androidx.compose.material3.Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        "📍 ${gridState.firstVisibleItemIndex + 1} / $totalItems",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Slider(
                        interactionSource = sliderInteractionSource,
                        value = gridState.firstVisibleItemIndex.toFloat(),
                        onValueChange = { newValue ->
                            scrollJob?.cancel()
                            scrollJob = scope.launch {
                                // scrollToItem فوري — يتبع الإصبع بدقة
                                gridState.scrollToItem(
                                    newValue.toInt().coerceIn(0, totalItems - 1)
                                )
                            }
                        },
                        valueRange = 0f..(totalItems - 1).toFloat().coerceAtLeast(1f),
                        colors = androidx.compose.material3.SliderDefaults.colors(
                            thumbColor = Color(0xFF4FC3F7),
                            activeTrackColor = Color(0xFF4FC3F7),
                            inactiveTrackColor = Color(0xFF4FC3F7).copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        // ────── المراقب: إظهار/إخفاء Slider عند التمرير ──────
        LaunchedEffect(gridState.isScrollInProgress) {
            if (gridState.isScrollInProgress) {
                isScrolling = true
                scrollRunnable?.cancel()
            } else {
                scrollRunnable?.cancel()
                scrollRunnable = scope.launch {
                    kotlinx.coroutines.delay(1500L)
                    isScrolling = false
                }
            }
        }
        
        // ────── الأزرار الرئيسية (أسفل اليمين) ──────
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .imePadding()
                .padding(end = 16.dp, bottom = 180.dp),
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { showSearchDialog = true },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search themes"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FloatingActionButton(
                onClick = { onCustomTheme() },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.theme_settings_add_new_theme)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FloatingActionButton(
                onClick = {
                    context.openURI("https://keyboard.futo.tech/themes", true)
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    painter = painterResource(R.drawable.compass),
                    contentDescription = stringResource(R.string.theme_settings_visit_theme_store)
                )
            }
        }

        // ────── أزرار التنقل السريع (أسفل اليسار) ──────
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .imePadding()
                .padding(start = 16.dp, bottom = 180.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SmallFloatingActionButton(
                onClick = {
                    scope.launch {
                        gridState.animateScrollToItem(0)
                    }
                },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll to top"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SmallFloatingActionButton(
                onClick = {
                    scope.launch {
                        val totalItems = 2 + customThemes.size + 2 + assetThemes.size + availableThemeOptions.size
                        gridState.animateScrollToItem(totalItems - 1)
                    }
                },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Scroll to bottom"
                )
            }
        }
    }

    // ────── Search Dialog ──────
    if (showSearchDialog) {
        SearchDialog(
            allThemes = customThemes + assetThemes,
            onDismiss = { showSearchDialog = false },
            onSelect = { theme ->
                lifecycle.lifecycleScope.launch {
                    context.setSetting(THEME_KEY, theme.toSetting())
                }
            }
        )
    }

    // ────── القائمة السياقية ──────
    if (contextMenuTheme != null) {
        val theme = contextMenuTheme!!
        val isFav = ZipThemes.isFavorite(context, theme.name)
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { contextMenuTheme = null },
            title = { Text(theme.name) },
            text = {
                Column {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            ZipThemes.toggleFavorite(context, theme.name)
                            favoritesVersion++
                            contextMenuTheme = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            if (isFav) Icons.Default.Star else Icons.Default.Star,
                            null,
                            tint = if (isFav) Color(0xFFFFD700) else Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isFav) "إزالة من المفضلة" else "إضافة للمفضلة")
                    }
                    
                    androidx.compose.material3.TextButton(
                        onClick = {
                            shareTheme(context, theme)
                            contextMenuTheme = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مشاركة")
                    }
                    
                    androidx.compose.material3.TextButton(
                        onClick = {
                            val fileName = theme.name
                            ZipThemes.delete(context, theme)
                            favoritesVersion++
                            contextMenuTheme = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حذف", color = Color.Red)
                    }
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { contextMenuTheme = null }
                ) {
                    Text("إغلاق")
                }
            }
        )
    }


    // ────── Large Preview Dialog ──────
    previewTheme?.let { theme ->
        LargePreviewDialog(
            theme = theme,
            onDismiss = { previewTheme = null },
            onApply = {
                lifecycle.lifecycleScope.launch {
                    context.setSetting(THEME_KEY, theme.toSetting())
                }
                previewTheme = null
            }
        )
    }


    // ────── Delete Multiple Dialog ──────
    if (showDeleteMultiple) {
        DeleteMultipleDialog(
            themes = customThemes,
            onConfirm = { selectedNames ->
                selectedNames.forEach { name ->
                    ZipThemes.delete(context, ZipThemes.custom(name))
                }
                ZipThemes.updateCount.intValue += 1
                Toast.makeText(context, "تم حذف ${selectedNames.size} ثيم", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showDeleteMultiple = false }
        )
    }


    // ────── Auto Mode Dialog ──────
    if (showAutoMode) {
        AutoModeDialog(
            onDismiss = { showAutoMode = false }
        )
    }


    // ────── Stats Dialog ──────
    if (showStats) {
        StatsDialog(onDismiss = { showStats = false })
    }

}



@Preview
@Composable
private fun ThemePickerPreview() {
    Column {
        UixThemeWrapper(VoiceInputTheme.obtainColors(LocalContext.current)) {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                ThemePicker({},{})
            }
        }
        UixThemeWrapper(ClassicMaterialDark.obtainColors(LocalContext.current)) {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                ThemePicker({},{})
            }
        }
        UixThemeWrapper(AMOLEDDarkPurple.obtainColors(LocalContext.current)) {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                ThemePicker({},{})
            }
        }
    }
}