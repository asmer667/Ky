package org.futo.inputmethod.latin.uix.settings.pages.themes

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.cos
import kotlin.math.sin

// ==========================================
// 1. البيانات (Data Model)
// ==========================================
data class AppTheme(
    val id: String = UUID.randomUUID().toString().take(6),
    val name: String = "ثيمي الجديد",
    val bgColor: Color = Color(0xFFFDE8C4),
    val keyColor: Color = Color(0xFFE8F0D8),
    val borderColor: Color = Color(0xFF8BA87E),
    val textColor: Color = Color(0xFF1A1A1A),
    val borderWidth: Float = 1f,
    val opacity: Float = 1f,
    val textSize: Float = 16f,
    val keyShape: Shape = RoundedCornerShape(12.dp),
    val keyShapeName: String = "مربع",
    // التخصيص الفردي
    val customKeyColors: Map<String, Color> = emptyMap(),
    val customKeyBorderColors: Map<String, Color> = emptyMap(),
    val customKeyShapes: Map<String, Shape> = emptyMap(),
    val customKeyImages: Map<String, String> = emptyMap()
)

// ==========================================
// 2. محرك الحفظ والتصدير
// ==========================================
object ThemeManager {
    fun saveToDatabase(context: Context, theme: AppTheme) {
        val file = File(context.filesDir, "studio_themes.txt")
        file.appendText("${theme.id}|${theme.name}|${theme.bgColor.toArgb()}|${theme.keyColor.toArgb()}\n")
        Toast.makeText(context, "تم الحفظ في المعرض!", Toast.LENGTH_SHORT).show()
    }

    fun applyTheme(context: Context, theme: AppTheme) {
        try {
            val zipFile = File(context.filesDir, "theme_${theme.id}.zip")
            FileOutputStream(zipFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    val toml = StringBuilder()
                    toml.append("name = \"${theme.name}\"\nid = \"${theme.id}\"\n[colors]\nkeyboard_surface = \"${String.format("#%08X", theme.bgColor.toArgb())}\"\n")
                    // إضافة قواعد التخصيص الفردي
                    theme.customKeyColors.forEach { (k, c) ->
                        toml.append("\n[[matchrules.color]]\nselector = \"label $k\"\ncolor = \"${String.format("#%08X", c.toArgb())}\"\n")
                    }
                    zos.putNextEntry(ZipEntry("theme.txt"))
                    zos.write(toml.toString().toByteArray())
                    zos.closeEntry()
                }
            }
            Toast.makeText(context, "تم التطبيق وحفظ الـ ZIP!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "خطأ: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

// ==========================================
// 3. توليد 400 لون و 500 شكل
// ==========================================
fun genSolidColors(): List<Color> {
    val list = mutableListOf<Color>()
    for (i in 0 until 100) list.add(Color.hsv((i * 3.6f) % 360f, 0.5f, 1.0f)) // 100 فاتح
    for (i in 0 until 100) list.add(Color.hsv((i * 3.6f) % 360f, 0.9f, 0.5f)) // 100 داكن
    return list
}
fun genGradientColors(): List<List<Color>> {
    val list = mutableListOf<List<Color>>()
    for (i in 0 until 100) list.add(listOf(Color.hsv((i * 3.6f) % 360f, 0.4f, 1f), Color.hsv(((i * 3.6f)+40) % 360f, 0.6f, 1f))) // 100 فاتح
    for (i in 0 until 100) list.add(listOf(Color.hsv((i * 3.6f) % 360f, 0.8f, 0.6f), Color.hsv(((i * 3.6f)+40) % 360f, 1.0f, 0.3f))) // 100 داكن
    return list
}
class StarShape(val points: Int, val ratio: Float = 0.4f) : Shape {
    override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline {
        val path = Path(); val cx = size.width / 2; val cy = size.height / 2; val outer = size.width / 2; val inner = outer * ratio
        for (i in 0 until points * 2) { val r = if (i % 2 == 0) outer else inner; val angle = Math.PI / points * i - Math.PI / 2
            if (i == 0) path.moveTo((cx + r * cos(angle)).toFloat(), (cy + r * sin(angle)).toFloat()) else path.lineTo((cx + r * cos(angle)).toFloat(), (cy + r * sin(angle)).toFloat()) }
        path.close(); return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}
fun genShapes(): List<Pair<String, Shape>> {
    val shapes = mutableListOf<Pair<String, Shape>>()
    for (i in 0..50 step 1) shapes.add("مربع $i" to RoundedCornerShape(i.dp))
    for (i in 0..50 step 1) shapes.add("مقطوع $i" to CutCornerShape(i.dp))
    for (i in 3..30) shapes.add("نجمة $i" to StarShape(i, 0.4f))
    for (i in 3..30) shapes.add("مضلع $i" to StarShape(i, 1.0f))
    // إضافة أشكال عشوائية حتى نصل لـ 500
    while (shapes.size < 500) { shapes.add("دائري ${shapes.size}" to RoundedCornerShape((shapes.size % 50).dp)) }
    return shapes
}
val SolidColors = genSolidColors()
val GradientColors = genGradientColors()
val AllShapes = genShapes()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeStudioScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var currentTheme by remember { mutableStateOf(AppTheme()) }
    var selectedTab by remember { mutableIntStateOf(1) }
    val tabs = listOf("الخلفية", "الكتابة", "الوظيفية", "المسافة", "العلوية")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استوديو التصميم", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = Color.White) } },
                actions = {
                    IconButton(onClick = { ThemeManager.saveToDatabase(context, currentTheme); ThemeManager.applyTheme(context, currentTheme) }) { 
                        Icon(Icons.Default.Check, "حفظ وتطبيق", tint = Color(0xFF69F0AE)) 
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7E57C2))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFF3E5F5)) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(Icons.Default.Settings, contentDescription = title, tint = if (selectedTab == index) Color(0xFF7E57C2) else Color.Gray) },
                        label = { Text(title, fontSize = 10.sp, color = if (selectedTab == index) Color(0xFF7E57C2) else Color.Gray) }
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // المعاينة الحقيقية (ثابتة في الأعلى)
            Box(modifier = Modifier.fillMaxWidth().weight(0.4f).background(currentTheme.bgColor).padding(8.dp)) {
                RealLivePreview(currentTheme)
            }
            Divider(thickness = 2.dp, color = Color.Gray)
            // المساحة الذكية
            Box(modifier = Modifier.fillMaxWidth().weight(0.6f)) {
                EditorArea(currentTheme, onThemeUpdate = { currentTheme = it })
            }
        }
    }
}

@Composable
fun EditorArea(theme: AppTheme, onThemeUpdate: (AppTheme) -> Unit) {
    var selectedKey by remember { mutableStateOf<String?>(null) }
    var innerView by remember { mutableIntStateOf(0) }
    
    when (innerView) {
        0 -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = if (selectedKey != null) "أنت تعدل الزر: [ $selectedKey ]" else "اضغط على أي زر في المعاينة لتحديده (للتعديل الفردي)",
                fontWeight = FontWeight.Bold, fontSize = 16.sp,
                color = if (selectedKey != null) Color.Red else Color(0xFF7E57C2)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { innerView = 1 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الألوان 🎨") }
                Button(onClick = { innerView = 2 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الأشكال/الصور 🔷") }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { innerView = 3 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الشفافية 🌫️") }
                Button(onClick = { innerView = 4 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("النص والحدود 🔤") }
            }
        }
        1 -> ColorEditor(theme, selectedKey, onThemeUpdate) { innerView = 0 }
        2 -> ShapeEditor(theme, selectedKey, onThemeUpdate) { innerView = 0 }
        3 -> OpacityEditor(theme, onThemeUpdate) { innerView = 0 }
        4 -> TextBorderEditor(theme, onThemeUpdate) { innerView = 0 }
    }
}

@Composable
fun RealLivePreview(theme: AppTheme) {
    var selectedKey by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    
    // ارتفاع ثابت 48dp لكل صف لمنع التمدد
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("ض", "ص", "ث", "ق", "ف").forEach { key -> 
                KeyItem(key, theme, selectedKey == key, { selectedKey = if(selectedKey == key) null else key }, Modifier.weight(1f)) 
            }
        }
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("ش", "س", "ي", "ب", "ل").forEach { key -> 
                KeyItem(key, theme, selectedKey == key, { selectedKey = if(selectedKey == key) null else key }, Modifier.weight(1f)) 
            }
        }
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("ا", "ت", "ن", "م", "ك").forEach { key -> 
                KeyItem(key, theme, selectedKey == key, { selectedKey = if(selectedKey == key) null else key }, Modifier.weight(1f)) 
            }
        }
    }
}

@Composable
fun KeyItem(text: String, theme: AppTheme, isSelected: Boolean, onClick: () -> Unit, mod: Modifier) {
    val context = LocalContext.current
    val color = theme.customKeyColors[text] ?: theme.keyColor
    val borderColor = theme.customKeyBorderColors[text] ?: theme.borderColor
    val shape = theme.customKeyShapes[text] ?: theme.keyShape
    val imgUri = theme.customKeyImages[text]

    val imgBitmap = remember(imgUri) {
        imgUri?.let { Uri.parse(it) }?.let { uri -> context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it).asImageBitmap() } }
    }

    Box(
        modifier = mod.fillMaxHeight().alpha(theme.opacity).clip(shape)
            .background(color)
            .border(if (isSelected) 3.dp else theme.borderWidth.dp, if (isSelected) Color.Red else borderColor, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imgBitmap != null) {
            Image(bitmap = imgBitmap, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(shape))
        } else {
            Text(text, color = theme.textColor, fontSize = theme.textSize.sp, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
@Composable
fun ColorEditor(theme: AppTheme, targetKey: String?, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    var mode by remember { mutableIntStateOf(0) } // 0: solid, 1: gradient, 2: custom
    var target by remember { mutableIntStateOf(0) } // 0: bg, 1: key, 2: border, 3: text

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }; Text("مكتبة الألوان", fontWeight = FontWeight.Bold) }
        
        TabRow(selectedTabIndex = target) {
            Tab(selected = target == 0, onClick = { target = 0 }, text = { Text("الخلفية") })
            Tab(selected = target == 1, onClick = { target = 1 }, text = { Text("الشكل") })
            Tab(selected = target == 2, onClick = { target = 2 }, text = { Text("الحدود") })
            Tab(selected = target == 3, onClick = { target = 3 }, text = { Text("النص") })
        }
        TabRow(selectedTabIndex = mode) {
            Tab(selected = mode == 0, onClick = { mode = 0 }, text = { Text("أحادية") })
            Tab(selected = mode == 1, onClick = { mode = 1 }, text = { Text("متدرجة") })
            Tab(selected = mode == 2, onClick = { mode = 2 }, text = { Text("مخصص") })
        }

        if (mode == 0) {
            LazyVerticalGrid(columns = GridCells.Fixed(8), modifier = Modifier.padding(8.dp)) {
                items(SolidColors) { c ->
                    Box(Modifier.padding(4.dp).aspectRatio(1f).clip(CircleShape).background(c).clickable {
                        if (targetKey == null) {
                            if (target == 0) onUpdate(theme.copy(bgColor = c)) else if (target == 1) onUpdate(theme.copy(keyColor = c)) else if (target == 2) onUpdate(theme.copy(borderColor = c)) else onUpdate(theme.copy(textColor = c))
                        } else {
                            if (target == 1) onUpdate(theme.copy(customKeyColors = theme.customKeyColors + (targetKey to c)))
                            else if (target == 2) onUpdate(theme.copy(customKeyBorderColors = theme.customKeyBorderColors + (targetKey to c)))
                        }
                    })
                }
            }
        } else if (mode == 1) {
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.padding(8.dp)) {
                items(GradientColors) { g -> 
                    Box(Modifier.padding(4.dp).aspectRatio(1.5f).clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(g)).clickable {
                         if (target == 0) onUpdate(theme.copy(bgColor = g[0]))
                    }) 
                }
            }
        } else {
            CanvasPicker { c -> if (target == 0) onUpdate(theme.copy(bgColor = c)) else if (target == 1) onUpdate(theme.copy(keyColor = c)) else if (target == 2) onUpdate(theme.copy(borderColor = c)) else onUpdate(theme.copy(textColor = c)) }
        }
    }
}

@Composable
fun ShapeEditor(theme: AppTheme, targetKey: String?, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> 
        uri?.let { if (targetKey != null) onUpdate(theme.copy(customKeyImages = theme.customKeyImages + (targetKey to it.toString()))) } 
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }
            Text(if (targetKey == null) "أشكال الكل" else "تخصيص: $targetKey", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            if (targetKey != null) Button(onClick = { launcher.launch("image/*") }) { Text("رفع صورة") }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.padding(8.dp)) {
            items(AllShapes) { (name, shape) ->
                Box(Modifier.padding(4.dp).aspectRatio(1f).clip(shape).background(Color.LightGray).clickable { 
                    if (targetKey == null) onUpdate(theme.copy(keyShape = shape))
                    else onUpdate(theme.copy(customKeyShapes = theme.customKeyShapes + (targetKey to shape)))
                })
            }
        }
    }
}

@Composable
fun OpacityEditor(theme: AppTheme, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }; Text("الشفافية") }
        Spacer(Modifier.height(16.dp))
        Text("الشفافية: ${(theme.opacity * 100).toInt()}%")
        Slider(value = theme.opacity, onValueChange = { onUpdate(theme.copy(opacity = it)) }, valueRange = 0.1f..1f)
    }
}

@Composable
fun TextBorderEditor(theme: AppTheme, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }; Text("النص والحدود") }
        Spacer(Modifier.height(16.dp))
        Text("حجم النص: ${theme.textSize.toInt()} sp")
        Slider(value = theme.textSize, onValueChange = { onUpdate(theme.copy(textSize = it)) }, valueRange = 10f..30f)
        Spacer(Modifier.height(16.dp))
        Text("سماكة الحدود: ${theme.borderWidth.toInt()} dp")
        Slider(value = theme.borderWidth, onValueChange = { onUpdate(theme.copy(borderWidth = it)) }, valueRange = 0f..10f)
    }
}

@Composable
fun CanvasPicker(onColorPicked: (Color) -> Unit) {
    var hue by remember { mutableFloatStateOf(0f) }; var sat by remember { mutableFloatStateOf(1f) }; var value by remember { mutableFloatStateOf(1f) }
    val currentColor = Color.hsv(hue, sat, value)
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(50.dp).clip(CircleShape).background(currentColor).border(2.dp, Color.White, CircleShape))
        Spacer(Modifier.height(16.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(8.dp)).pointerInput(Unit) {
            detectDragGestures { change, _ -> sat = (change.position.x / size.width).coerceIn(0f, 1f); value = 1f - (change.position.y / size.height).coerceIn(0f, 1f) }
        }) {
            drawRect(Brush.horizontalGradient(listOf(Color.White, Color.hsv(hue, 1f, 1f))))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
        }
        Spacer(Modifier.height(16.dp))
        Slider(value = hue, onValueChange = { hue = it }, valueRange = 0f..360f, colors = SliderDefaults.colors(thumbColor = Color.hsv(hue, 1f, 1f)))
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onColorPicked(currentColor) }, modifier = Modifier.fillMaxWidth()) { Text("تطبيق اللون") }
    }
}
