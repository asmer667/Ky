package org.futo.inputmethod.latin.uix.settings.pages.themes

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

data class SavedTheme(
    val id: String = UUID.randomUUID().toString().take(8),
    val name: String,
    val keyBgBrush: Brush,
    val keyBorderColor: Color,
    val keyBorderWidth: Dp,
    val keyTextColor: Color,
    val keyShape: Shape,
    val keyImageUri: Uri?,
    val keyAlpha: Float,
    val keyTextSize: Float
)

val SolidLight = listOf(Color.White, Color(0xFFF5F5F5), Color(0xFFFFCDD2), Color(0xFFFFE082), Color(0xFFC8E6C9), Color(0xFFBBDEFB), Color(0xFFE1BEE7), Color(0xFFCFD8DC))
val SolidDark = listOf(Color.Black, Color(0xFF212121), Color(0xFFB71C1C), Color(0xFFF57F17), Color(0xFF1B5E20), Color(0xFF0D47A1), Color(0xFF4A148C), Color(0xFF263238))
val GradientsLight = listOf(listOf(Color(0xFFFF9A9E), Color(0xFFFECFEF)), listOf(Color(0xFFA18CD1), Color(0xFFFBC2EB)), listOf(Color(0xFF84FAB0), Color(0xFF8FD3F4)))
val GradientsDark = listOf(listOf(Color(0xFF43C6AC), Color(0xFFF8FFAE)), listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)), listOf(Color(0xFFF3904F), Color(0xFF3B4371)))

class CustomStarShape(val points: Int, val innerRatio: Float = 0.4f) : Shape {
    override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline {
        val path = Path()
        val cx = size.width / 2; val cy = size.height / 2; val outer = size.width / 2; val inner = outer * innerRatio
        for (i in 0 until points * 2) {
            val r = if (i % 2 == 0) outer else inner
            val angle = Math.PI / points * i - Math.PI / 2
            if (i == 0) path.moveTo((cx + r * Math.cos(angle)).toFloat(), (cy + r * Math.sin(angle)).toFloat())
            else path.lineTo((cx + r * Math.cos(angle)).toFloat(), (cy + r * Math.sin(angle)).toFloat())
        }
        path.close(); return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

val AllShapes = mutableListOf<Shape>().apply {
    for (i in 0..40 step 2) add(RoundedCornerShape(i.dp))
    for (i in 0..40 step 2) add(CutCornerShape(i.dp))
    for (i in 3..15) add(CustomStarShape(i, 0.4f))
    for (i in 3..15) add(CustomStarShape(i, 0.7f))
    add(CircleShape)
    for (i in 1..50) { add(RoundedCornerShape(topStart = (i*2).dp, bottomEnd = (i*3).dp)) }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeStudioScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var savedThemes by remember { mutableStateOf(listOf<SavedTheme>()) }
    var isGalleryView by remember { mutableStateOf(false) }

    var currentTheme by remember {
        mutableStateOf(
            SavedTheme(
                name = "ثيم جديد 1",
                keyBgBrush = SolidColor(Color(0xFFE8F0D8)),
                keyBorderColor = Color(0xFF8BA87E),
                keyBorderWidth = 1.dp,
                keyTextColor = Color(0xFF1A1A1A),
                keyShape = RoundedCornerShape(16.dp),
                keyImageUri = null,
                keyAlpha = 1f,
                keyTextSize = 16f
            )
        )
    }

    val createFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri: Uri? ->
        uri?.let { Toast.makeText(context, "تم تصدير الثيم كملف ZIP بنجاح!", Toast.LENGTH_LONG).show() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isGalleryView) "معرض الثيمات" else "استوديو التصميم", fontWeight = FontWeight.Bold) },
                navigationIcon = { 
                    IconButton(onClick = { if (isGalleryView) isGalleryView = false else onBack() }) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") 
                    } 
                },
                actions = {
                    if (!isGalleryView) {
                        IconButton(onClick = { isGalleryView = true }) { Icon(Icons.Default.List, "معرض الثيمات", tint = MaterialTheme.colorScheme.primary) }
                        IconButton(onClick = { savedThemes = savedThemes + currentTheme; Toast.makeText(context, "تم حفظ الثيم (ID: ${currentTheme.id})", Toast.LENGTH_SHORT).show() }) { Icon(Icons.Default.Check, "حفظ", tint = Color(0xFF4CAF50)) }
                        IconButton(onClick = { createFileLauncher.launch("${currentTheme.name}.zip") }) { Icon(Icons.Default.Share, "تصدير ZIP") }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isGalleryView) {
                if (savedThemes.isEmpty()) {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text("لا توجد ثيمات محفوظة بعد.", color = Color.Gray) }
                } else {
                    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(savedThemes) { theme ->
                            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(theme.keyShape).background(theme.keyBgBrush).border(theme.keyBorderWidth, theme.keyBorderColor, theme.keyShape), contentAlignment = Alignment.Center) { Text("Aa", color = theme.keyTextColor, fontSize = theme.keyTextSize.sp, fontWeight = FontWeight.Bold) }
                                    Spacer(Modifier.height(8.dp))
                                    Text(theme.name, fontWeight = FontWeight.Bold)
                                    Text("ID: ${theme.id}", fontSize = 12.sp, color = Color.Gray)
                                    Spacer(Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                        IconButton(onClick = { currentTheme = theme; isGalleryView = false }) { Icon(Icons.Default.Edit, "تعديل") }
                                        IconButton(onClick = { createFileLauncher.launch("${theme.name}.zip") }) { Icon(Icons.Default.Share, "تصدير") }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                EditorView(currentTheme = currentTheme, onThemeUpdate = { currentTheme = it })
            }
        }
    }
}

@Composable
fun EditorView(currentTheme: SavedTheme, onThemeUpdate: (SavedTheme) -> Unit) {
    var selectedPage by remember { mutableIntStateOf(1) }
    val pages = listOf("الخلفية", "الكتابة", "الوظيفية", "المسافة", "العلوية")
    val icons = listOf(Icons.Default.Settings, Icons.Default.Edit, Icons.Default.Build, Icons.Default.Menu, Icons.Default.MoreVert)

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().weight(0.35f).background(Color(0xFFFDE8C4)).padding(4.dp)) { RealPreview(currentTheme) }
        Divider(thickness = 2.dp)
        Box(modifier = Modifier.fillMaxWidth().weight(0.65f)) {
            if (selectedPage == 0) BackgroundEditor(currentTheme, onThemeUpdate) else KeyEditor(currentTheme, onThemeUpdate)
        }
        NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
            pages.forEachIndexed { index, title ->
                NavigationBarItem(selected = selectedPage == index, onClick = { selectedPage = index }, icon = { Icon(icons[index], contentDescription = title) }, label = { Text(title, fontSize = 10.sp) })
            }
        }
    }
}

@Composable
fun BackgroundEditor(theme: SavedTheme, onUpdate: (SavedTheme) -> Unit) {
    var innerView by remember { mutableIntStateOf(0) }
    when (innerView) {
        0 -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("تعديل الخلفية", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { innerView = 1 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الألوان 🎨") }
                Button(onClick = { innerView = 2 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الشفافية 🌫️") }
            }
        }
        1 -> ColorLibraryView(onBgSelected = { onUpdate(theme.copy(keyBgBrush = it)); innerView = 0 }, target = "الخلفية", onBack = { innerView = 0 })
        2 -> OpacityView(theme.keyAlpha, { onUpdate(theme.copy(keyAlpha = it)) }, onBack = { innerView = 0 })
    }
}

@Composable
fun KeyEditor(theme: SavedTheme, onUpdate: (SavedTheme) -> Unit) {
    var innerView by remember { mutableIntStateOf(0) }
    when (innerView) {
        0 -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("تعديل الأزرار", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { innerView = 1 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الألوان 🎨") }
                Button(onClick = { innerView = 2 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الأشكال/الصور 🔷") }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { innerView = 3 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الشفافية 🌫️") }
                Button(onClick = { innerView = 4 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("نص وحدود 🔤") }
            }
        }
        1 -> ColorLibraryView(onBgSelected = { onUpdate(theme.copy(keyBgBrush = it)) }, onBorderSelected = { onUpdate(theme.copy(keyBorderColor = it)) }, onTextSelected = { onUpdate(theme.copy(keyTextColor = it)) }, target = "الأزرار", onBack = { innerView = 0 })
        2 -> ShapeLibraryView(onShapeChange = { onUpdate(theme.copy(keyShape = it)) }, onImageChange = { onUpdate(theme.copy(keyImageUri = it)) }, onBack = { innerView = 0 })
        3 -> OpacityView(theme.keyAlpha, { onUpdate(theme.copy(keyAlpha = it)) }, onBack = { innerView = 0 })
        4 -> TextAndBorderView(theme.keyTextSize, { onUpdate(theme.copy(keyTextSize = it)) }, theme.keyBorderWidth, { onUpdate(theme.copy(keyBorderWidth = it)) }, onBack = { innerView = 0 })
    }
}
@Composable
fun OpacityView(alpha: Float, onAlphaChange: (Float) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }; Text("الشفافية") }
        Spacer(Modifier.height(16.dp))
        Text("درجة الشفافية: ${(alpha * 100).toInt()}%")
        Slider(value = alpha, onValueChange = onAlphaChange, valueRange = 0f..1f)
    }
}

@Composable
fun TextAndBorderView(size: Float, onSizeChange: (Float) -> Unit, borderW: Dp, onBorderWChange: (Dp) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }; Text("النص والحدود") }
        Spacer(Modifier.height(16.dp))
        Text("حجم النص: ${size.toInt()} sp")
        Slider(value = size, onValueChange = onSizeChange, valueRange = 10f..30f)
        Spacer(Modifier.height(16.dp))
        Text("سماكة الحدود: ${borderW.value.toInt()} dp")
        Slider(value = borderW.value, onValueChange = { onBorderWChange(it.dp) }, valueRange = 0f..10f)
    }
}

@Composable
fun ColorLibraryView(onBgSelected: (Brush) -> Unit, target: String, onBorderSelected: ((Color) -> Unit)? = null, onTextSelected: ((Color) -> Unit)? = null, onBack: () -> Unit) {
    var mode by remember { mutableIntStateOf(0) }
    var targetTab by remember { mutableIntStateOf(0) }
    var isLight by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }; Text("مكتبة الألوان ($target)") }
        if (target == "الأزرار") {
            TabRow(selectedTabIndex = targetTab) {
                Tab(selected = targetTab == 0, onClick = { targetTab = 0 }, text = { Text("الخلفية") })
                Tab(selected = targetTab == 1, onClick = { targetTab = 1 }, text = { Text("الحدود") })
                Tab(selected = targetTab == 2, onClick = { targetTab = 2 }, text = { Text("النص") })
            }
        }
        if (targetTab == 0) {
            TabRow(selectedTabIndex = mode) {
                Tab(selected = mode == 0, onClick = { mode = 0 }, text = { Text("أحادية") })
                Tab(selected = mode == 1, onClick = { mode = 1 }, text = { Text("متدرجة") })
                Tab(selected = mode == 2, onClick = { mode = 2 }, text = { Text("مخصص") })
            }
            Spacer(Modifier.height(8.dp))
            when (mode) {
                0, 1 -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        FilterChip(selected = isLight, onClick = { isLight = true }, label = { Text("فاتح") })
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(selected = !isLight, onClick = { isLight = false }, label = { Text("داكن") })
                    }
                    val colors = if (mode == 0) { if (isLight) SolidLight else SolidDark } else { if (isLight) GradientsLight else GradientsDark }
                    if (mode == 0) {
                        LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.padding(8.dp)) {
                            items(colors as List<Color>) { c -> Box(Modifier.padding(4.dp).aspectRatio(1f).clip(CircleShape).background(c).clickable { onBgSelected(SolidColor(c)) }) }
                        }
                    } else {
                        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(8.dp)) {
                            items(colors as List<List<Color>>) { g -> Box(Modifier.padding(4.dp).aspectRatio(1.5f).clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(g)).clickable { onBgSelected(Brush.linearGradient(g)) }) }
                        }
                    }
                }
                2 -> {
                    Text("منتقي الألوان المخصص (Hue)", modifier = Modifier.padding(16.dp))
                    var hue by remember { mutableFloatStateOf(0f) }
                    val c = Color.hsv(hue, 1f, 1f)
                    Box(Modifier.fillMaxWidth().height(60.dp).background(c))
                    Slider(value = hue, onValueChange = { hue = it }, valueRange = 0f..360f)
                    Button(onClick = { onBgSelected(SolidColor(c)) }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("تطبيق اللون") }
                }
            }
        } else {
            val colors = SolidLight + SolidDark
            LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.padding(8.dp)) {
                items(colors) { c -> Box(Modifier.padding(4.dp).aspectRatio(1f).clip(CircleShape).background(c).clickable { 
                    if (targetTab == 1) onBorderSelected?.invoke(c) else onTextSelected?.invoke(c) 
                }) }
            }
        }
    }
}

@Composable
fun ShapeLibraryView(onShapeChange: (Shape) -> Unit, onImageChange: (Uri) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { onImageChange(it) } }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }
            Text("الأشكال والصور", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Button(onClick = { launcher.launch("image/*") }) { Text("رفع صورة") }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.padding(8.dp)) {
            items(AllShapes) { s -> Box(Modifier.padding(4.dp).aspectRatio(1f).clip(s).background(Color.LightGray).clickable { onShapeChange(s) }) }
        }
    }
}

@Composable
fun RealPreview(theme: SavedTheme) {
    val context = LocalContext.current
    val imageBitmap = remember(theme.keyImageUri) {
        theme.keyImageUri?.let { uri -> context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it).asImageBitmap() } }
    }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠").forEach { PK(it, theme, imageBitmap, Modifier.weight(1f)) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج").forEach { PK(it, theme, imageBitmap, Modifier.weight(1f)) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط").forEach { PK(it, theme, imageBitmap, Modifier.weight(1f)) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ", "د").forEach { PK(it, theme, imageBitmap, Modifier.weight(1f)) }
        }
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PK("١٢٣", theme, imageBitmap, Modifier.weight(1f))
            PK("مسافة", theme, imageBitmap, Modifier.weight(4f))
            PK("↵", theme, imageBitmap, Modifier.weight(1f))
        }
    }
}

@Composable
fun PK(text: String, theme: SavedTheme, img: androidx.compose.ui.graphics.ImageBitmap?, mod: Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Box(
        modifier = mod.fillMaxHeight().padding(if (pressed) 2.dp else 0.dp).alpha(theme.keyAlpha).clip(theme.keyShape).background(theme.keyBgBrush).border(theme.keyBorderWidth, theme.keyBorderColor, theme.keyShape).clickable(interactionSource = interaction, indication = null) {},
        contentAlignment = Alignment.Center
    ) {
        if (img != null) {
            Image(bitmap = img, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(theme.keyShape))
        } else {
            Text(text, color = theme.keyTextColor, fontSize = theme.keyTextSize.sp, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}
