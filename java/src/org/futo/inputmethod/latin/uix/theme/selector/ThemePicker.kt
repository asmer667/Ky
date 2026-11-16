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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
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
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZipThemePreview(
    name: ZipThemes.ThemeFileName,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit = {},
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
        
        Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(scheme.value.keyboardSurfaceDim.copy(alpha = 0.7f)))

        Row(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = scheme.value.extended.advancedThemeOptions.themeName ?: name.name,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
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
                Text("تطبيق", color = textColor, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ThemePreview(theme: ThemeOption, isSelected: Boolean = false, overrideName: String? = null, modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
    if (theme == DynamicSystemTheme) return
    val context = LocalContext.current
    val colors = remember(theme) { theme.obtainColors(context) }
    val currColors = MaterialTheme.colorScheme
    val borderWidth = if (isSelected) 4.dp else Dp.Hairline
    val borderColor = if (isSelected) currColors.inversePrimary else currColors.outline
    val textColor = colors.onBackground
    val keyboardShape = RoundedCornerShape(16.dp)

    Box(modifier = modifier.padding(6.dp).height(130.dp).border(borderWidth, borderColor, keyboardShape).combinedClickable(onClick = onClick).clip(keyboardShape)) {
        KeyboardBackground(colors, useThumbnail = true)
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = overrideName ?: stringResource(theme.name), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.TopCenter).background(colors.keyboardSurfaceDim.copy(alpha = 0.6f)).fillMaxWidth().padding(4.dp), color = textColor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun SearchDialog(allThemes: List<ZipThemes.ThemeFileName>, onDismiss: () -> Unit, onSelect: (ZipThemes.ThemeFileName) -> Unit) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<ZipThemes.ThemeFileName>>(emptyList()) }
    
    LaunchedEffect(query) {
        if (query.isBlank()) { results = emptyList(); return@LaunchedEffect }
        withContext(Dispatchers.Default) {
            val lowerQuery = query.lowercase().trim()
            results = allThemes.filter { it.name.lowercase().contains(lowerQuery) }.take(100)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🔍 بحث") },
        text = {
            Column {
                OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("اسم الثيم") }, singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                if (query.isNotBlank() && results.isEmpty()) { Text("لا توجد نتائج") }
                else { LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) { items(results.size) { idx -> Surface(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceVariant, onClick = { onSelect(results[idx]); onDismiss() }) { Text(results[idx].name, modifier = Modifier.padding(12.dp)) } } } }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}

@Composable
fun ColorFilterRow(selectedColor: String?, onSelect: (String?) -> Unit) {
    val ThemeColors = listOf("الكل" to null, "أحمر" to "red", "أزرق" to "blue", "أخضر" to "green", "بنفسجي" to "purple", "وردي" to "pink", "برتقالي" to "orange")
    LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(ThemeColors.size) { idx -> 
            val (name, key) = ThemeColors[idx]
            FilterChip(selected = selectedColor == key, onClick = { onSelect(key) }, label = { Text(name) }) 
        }
    }
}

enum class SortOption(val label: String) { ALPHABETICAL("أبجدي"), NEWEST("الأحدث") }

@Composable
fun SortDialog(currentSort: SortOption, onSelect: (SortOption) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("🔀 ترتيب") }, text = {
        Column { SortOption.values().forEach { option -> Row(modifier = Modifier.fillMaxWidth().clickable { onSelect(option); onDismiss() }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = currentSort == option, onClick = { onSelect(option); onDismiss() }); Spacer(modifier = Modifier.width(8.dp)); Text(option.label) } } }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } })
}

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
    val availableThemeOptions = remember { ThemeOptionKeys.mapNotNull { key -> getThemeOption(context, key)?.let { Pair(key, it) } }.filter { it.second.available(context) } }
    val customThemes = remember(ZipThemes.updateCount.intValue) { ZipThemes.listCustom(context) }
    val allAssetThemes = remember { ZipThemes.listAssets(context) }
    val lifecycle = LocalLifecycleOwner.current
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    var showSearchDialog by remember { mutableStateOf(false) }
    var contextMenuTheme by remember { mutableStateOf<ZipThemes.ThemeFileName?>(null) }
    var favoritesVersion by remember { mutableIntStateOf(0) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var sortBy by remember { mutableStateOf(SortOption.ALPHABETICAL) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    var showSettingsMenu by remember { mutableStateOf(false) } 
    var activeTab by remember { mutableIntStateOf(0) } 

    val favorites = remember(favoritesVersion) { ZipThemes.getFavorites(context) }
    val filteredAssetThemes = remember(selectedColor, sortBy, showFavoritesOnly, favoritesVersion) {
        var filtered = if (selectedColor == null) allAssetThemes else allAssetThemes.filter { themeMatchesColor(it.name, selectedColor) }
        if (showFavoritesOnly) filtered = filtered.filter { it.name in favorites }
        filtered.sortedBy { it.name }
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            OutlinedTextField(
                value = "", onValueChange = { showSearchDialog = true },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("البحث في الثيمات...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(24.dp), readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.Center) {
                FilterChip(selected = activeTab == 0, onClick = { activeTab = 0 }, label = { Text("مدمج") }, modifier = Modifier.padding(end = 8.dp))
                FilterChip(selected = activeTab == 1, onClick = { activeTab = 1 }, label = { Text("خاصتي") }, modifier = Modifier.padding(start = 8.dp))
            }

            ColorFilterRow(selectedColor = selectedColor, onSelect = { selectedColor = it })

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                LazyVerticalGrid(state = gridState, modifier = Modifier.fillMaxWidth(), columns = GridCells.Adaptive(minSize = 160.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
                    if (activeTab == 0) {
                        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("الثيمات المدمجة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                TextButton(onClick = { showSortDialog = true }) { Text("ترتيب") }
                            }
                        }
                        items(filteredAssetThemes) { name ->
                            ZipThemePreview(name = name, isSelected = currentTheme == name.toSetting(), onLongClick = { contextMenuTheme = name }, isFavorite = name.name in favorites, onFavoriteClick = { ZipThemes.toggleFavorite(context, name.name); favoritesVersion++ }) { lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, name.toSetting()) } }
                        }
                    } else {
                        item(span = { GridItemSpan(maxCurrentLineSpan) }) { Text("الثيمات الخاصة بك", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                        if (filteredCustomThemes.isEmpty()) { item(span = { GridItemSpan(maxCurrentLineSpan) }) { Text("لا توجد ثيمات مخصصة", modifier = Modifier.padding(16.dp)) } }
                        else { items(filteredCustomThemes.size) { val name = filteredCustomThemes[it]; ZipThemePreview(name = name, isSelected = currentTheme == name.toSetting(), onLongClick = { contextMenuTheme = name }, isFavorite = name.name in favorites, onFavoriteClick = { ZipThemes.toggleFavorite(context, name.name); favoritesVersion++ }) { lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, name.toSetting()) } } } }
                    }
                }
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomEnd).imePadding().padding(16.dp), horizontalAlignment = Alignment.End) {
            FloatingActionButton(onClick = onToggleKeyboard, containerColor = MaterialTheme.colorScheme.secondaryContainer) { Icon(if (showKeyboard) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, null) }
            Spacer(modifier = Modifier.height(12.dp))
            Box {
                FloatingActionButton(onClick = { showSettingsMenu = !showSettingsMenu }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Settings, null) }
                DropdownMenu(expanded = showSettingsMenu, onDismissRequest = { showSettingsMenu = false }) {
                    DropdownMenuItem(text = { Text("🔍 بحث") }, onClick = { showSettingsMenu = false; showSearchDialog = true })
                    DropdownMenuItem(text = { Text("➕ إضافة ثيم") }, onClick = { showSettingsMenu = false; onCustomTheme() })
                    DropdownMenuItem(text = { Text("🎲 نرد") }, onClick = { showSettingsMenu = false; val random = ZipThemes.getRandomTheme(context); if (random != null) lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, random.toSetting()) } })
                    DropdownMenuItem(text = { Text("⭐ المفضلة") }, onClick = { showSettingsMenu = false; showFavoritesOnly = !showFavoritesOnly })
                    DropdownMenuItem(text = { Text("📁 استيراد") }, onClick = { showSettingsMenu = false; folderPicker.launch(null) })
                }
            }
        }
    }

    if (showSearchDialog) SearchDialog(customThemes + allAssetThemes, { showSearchDialog = false }, { theme -> lifecycle.lifecycleScope.launch { context.setSetting(THEME_KEY, theme.toSetting()) } })
    if (showSortDialog) SortDialog(sortBy, { sortBy = it }, { showSortDialog = false })
    contextMenuTheme?.let { theme -> AlertDialog(onDismissRequest = { contextMenuTheme = null }, title = { Text(theme.name) }, text = { Column { TextButton(onClick = { ZipThemes.toggleFavorite(context, theme.name); favoritesVersion++; contextMenuTheme = null }) { Text(if (theme.name in favorites) "إزالة" else "إضافة") } } }, confirmButton = { TextButton(onClick = { contextMenuTheme = null }) { Text("إغلاق") } }) }
}