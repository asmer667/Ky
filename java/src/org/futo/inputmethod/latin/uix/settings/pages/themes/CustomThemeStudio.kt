package org.futo.inputmethod.latin.uix.settings.pages.themes

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.futo.inputmethod.latin.Settings
import org.futo.inputmethod.latin.uix.THEME_KEY
import org.futo.inputmethod.latin.uix.theme.ZipThemes
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.cos
import kotlin.math.sin

// ==========================================
// 1. البيانات (تدعم الصور والأشكال لكل زر)
// ==========================================
data class AppTheme(
    val id: String = UUID.randomUUID().toString().take(8),
    val name: String = "ثيمي الجديد",
    val bgColor: Color = Color(0xFFFDE8C4),
    val keyColor: Color = Color(0xFFE8F0D8),
    val textColor: Color = Color(0xFF1A1A1A),
    val borderWidth: Float = 1f,
    val opacity: Float = 1f,
    val textSize: Float = 16f,
    val keyShape: Shape = RoundedCornerShape(12.dp),
    // خرائط لتخصيص الأزرار الفردية
    val customKeyColors: Map<String, Color> = emptyMap(),
    val customKeyShapes: Map<String, Shape> = emptyMap(),
    val customKeyImages: Map<String, String> = emptyMap() // مسار الصورة للزر المحدد
)

// ==========================================
// 2. محرك الحفظ والتطبيق الحقيقي
// ==========================================
object ThemeManager {
    fun saveToDatabase(context: Context, theme: AppTheme) {
        val file = File(context.filesDir, "studio_themes.txt")
        // حفظ مبسط للبيانات
        file.appendText("${theme.id}|${theme.name}|${theme.bgColor.toArgb()}|${theme.keyColor.toArgb()}\n")
        Toast.makeText(context, "تم الحفظ في المعرض!", Toast.LENGTH_SHORT).show()
    }

    fun applyTheme(context: Context, theme: AppTheme) {
        try {
            val customThemeDir = ZipThemes.custom(theme.id)
            val zipFile = File(customThemeDir, "studio_${theme.id}.zip")
            zipFile.parentFile?.mkdirs()

            FileOutputStream(zipFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    val toml = StringBuilder()
                    toml.append("""
                        name = "${theme.name}"
                        id = "${theme.id}"
                        [options]
                        scale_text = ${theme.textSize / 16f}
                        [colors]
                        keyboard_surface = "${String.format("#%08X", theme.bgColor.toArgb())}"
                        keyboard_container = "${String.format("#%08X", theme.keyColor.toArgb())}"
                        on_keyboard_container = "${String.format("#%08X", theme.textColor.toArgb())}"
                    """.trimIndent())

                    // إضافة الأزرار المخصصة (صور أو أشكال)
                    theme.customKeyImages.forEach { (key, uriStr) ->
                        val imgName = "key_$key.png"
                        toml.append("\n[[matchrules.border]]\nselector = \"normal label $key\"\nasset = \"$imgName\"\n")
                        // نسخ الصورة إلى الـ ZIP
                        context.contentResolver.openInputStream(Uri.parse(uriStr))?.use { isImg ->
                            zos.putNextEntry(ZipEntry(imgName))
                            isImg.copyTo(zos)
                            zos.closeEntry()
                        }
                    }

                    zos.putNextEntry(ZipEntry("theme.txt"))
                    zos.write(toml.toString().toByteArray())
                    zos.closeEntry()
                }
            }
            Settings.getInstance(context).getSharedPreferences().edit()
                .putString(THEME_KEY, ZipThemes.custom(theme.id).name).apply()
            Toast.makeText(context, "تم التطبيق بنجاح!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "فشل التطبيق: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

// أشكال وألوان
class StarShape(val points: Int, val ratio: Float = 0.4f) : Shape {
    override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline {
        val path = Path(); val cx = size.width / 2; val cy = size.height / 2; val outer = size.width / 2; val inner = outer * ratio
        for (i in 0 until points * 2) { val r = if (i % 2 == 0) outer else inner; val angle = Math.PI / points * i - Math.PI / 2
            if (i == 0) path.moveTo((cx + r * cos(angle)).toFloat(), (cy + r * sin(angle)).toFloat()) else path.lineTo((cx + r * cos(angle)).toFloat(), (cy + r * sin(angle)).toFloat()) }
        path.close(); return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}
val ShapeLibrary = listOf("مربع" to (0..20 step 2).map { RoundedCornerShape(it.dp) }, "نجوم" to (3..15).map { StarShape(it) })
val SolidColors = List(150) { Color.hsv((it*2.4f)%360f, 0.6f, 1f) }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeStudioScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var currentTheme by remember { mutableStateOf(AppTheme()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استوديو التصميم", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع", tint = Color.White) } },
                actions = {
                    IconButton(onClick = { ThemeManager.saveToDatabase(context, currentTheme); ThemeManager.applyTheme(context, currentTheme) }) { 
                        Icon(Icons.Default.Check, "تطبيق", tint = Color(0xFF69F0AE)) 
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7E57C2))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            StudioEditor(currentTheme, onThemeUpdate = { currentTheme = it })
        }
    }
}

@Composable
fun StudioEditor(theme: AppTheme, onThemeUpdate: (AppTheme) -> Unit) {
    var selectedKeyToEdit by remember { mutableStateOf<String?>(null) }
    var innerView by remember { mutableIntStateOf(0) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 1. المعاينة الحية (وضع الاختيار)
        Box(modifier = Modifier.fillMaxWidth().weight(0.4f).background(theme.bgColor).padding(4.dp)) {
            LivePreview(theme, selectedKeyToEdit, onKeyClick = { key ->
                // عند النقر على زر في المعاينة، يتم تحديده للتعديل
                selectedKeyToEdit = if (selectedKeyToEdit == key) null else key
            })
        }
        Divider(thickness = 2.dp, color = Color.Gray)
        
        // 2. المساحة الذكية السفلية
        Box(modifier = Modifier.fillMaxWidth().weight(0.6f)) {
            when (innerView) {
                0 -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = if (selectedKeyToEdit != null) "أنت تعدل الزر: [ $selectedKeyToEdit ]" else "تعديل جميع الأزرار",
                        fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        color = if (selectedKeyToEdit != null) Color.Red else Color(0xFF7E57C2)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { innerView = 1 }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("تغيير الألوان 🎨") }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { innerView = 2 }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("تغيير الأشكال / رفع صورة 🔷") }
                }
                1 -> ColorEditor(theme, selectedKeyToEdit, onThemeUpdate) { innerView = 0 }
                2 -> ShapeEditor(theme, selectedKeyToEdit, onThemeUpdate) { innerView = 0 }
            }
        }
    }
}

@Composable
fun ColorEditor(theme: AppTheme, targetKey: String?, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) { 
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }
            Text(if (targetKey == null) "تلوين الكل" else "تلوين حرف: $targetKey", fontWeight = FontWeight.Bold) 
        }
        LazyVerticalGrid(columns = GridCells.Fixed(8), modifier = Modifier.padding(8.dp)) {
            items(SolidColors) { c ->
                Box(Modifier.padding(4.dp).aspectRatio(1f).clip(CircleShape).background(c).clickable {
                    if (targetKey == null) onUpdate(theme.copy(keyColor = c))
                    else {
                        val newMap = theme.customKeyColors.toMutableMap()
                        newMap[targetKey] = c
                        onUpdate(theme.copy(customKeyColors = newMap))
                    }
                })
            }
        }
    }
}

@Composable
fun ShapeEditor(theme: AppTheme, targetKey: String?, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    var selectedCat by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    // منتقي الصور
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> 
        uri?.let { 
            if (targetKey != null) {
                val newMap = theme.customKeyImages.toMutableMap()
                newMap[targetKey] = it.toString()
                onUpdate(theme.copy(customKeyImages = newMap))
            }
        } 
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }
            Text(if (targetKey == null) "أشكال الكل" else "تخصيص حرف: $targetKey", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            // زر رفع الصورة يظهر فقط عند تحديد زر معين
            if (targetKey != null) {
                Button(onClick = { launcher.launch("image/*") }) { Text("رفع صورة") }
            }
        }
        
        LazyRow(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ShapeLibrary.size) { i -> FilterChip(selected = selectedCat == i, onClick = { selectedCat = i }, label = { Text(ShapeLibrary[i].first) }) }
        }
        
        LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.padding(8.dp)) {
            items(ShapeLibrary[selectedCat].second) { shape ->
                Box(Modifier.padding(4.dp).aspectRatio(1f).clip(shape).background(Color.LightGray).clickable { 
                    if (targetKey == null) onUpdate(theme.copy(keyShape = shape))
                    else {
                        val newMap = theme.customKeyShapes.toMutableMap()
                        newMap[targetKey] = shape
                        onUpdate(theme.copy(customKeyShapes = newMap))
                    }
                })
            }
        }
    }
}

@Composable
fun LivePreview(theme: AppTheme, selectedKey: String?, onKeyClick: (String) -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ض", "ص", "ث", "ق", "ف").forEach { key -> KeyBox(key, theme, selectedKey == key, { onKeyClick(key) }, Modifier.weight(1f)) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ب", "ل", "ا", "ت", "ن").forEach { key -> KeyBox(key, theme, selectedKey == key, { onKeyClick(key) }, Modifier.weight(1f)) }
        }
    }
}

@Composable
fun KeyBox(text: String, theme: AppTheme, isSelected: Boolean, onClick: () -> Unit, mod: Modifier) {
    val context = LocalContext.current
    val customColor = theme.customKeyColors[text] ?: theme.keyColor
    val customShape = theme.customKeyShapes[text] ?: theme.keyShape
    val customImgUri = theme.customKeyImages[text]

    val imgBitmap = remember(customImgUri) {
        customImgUri?.let { Uri.parse(it) }?.let { uri -> context.contentResolver.openInputStream(uri)?.use { android.graphics.BitmapFactory.decodeStream(it).asImageBitmap() } }
    }

    Box(
        modifier = mod.fillMaxHeight().padding(2.dp).clip(customShape).background(customColor)
            .border(if (isSelected) 3.dp else 1.dp, if (isSelected) Color.Red else Color.Gray, customShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imgBitmap != null) {
            Image(bitmap = imgBitmap, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(customShape))
        } else {
            Text(text, color = theme.textColor, fontSize = 16.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
