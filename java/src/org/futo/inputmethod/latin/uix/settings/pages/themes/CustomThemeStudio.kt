package org.futo.inputmethod.latin.uix.settings.pages.themes

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.futo.inputmethod.latin.uix.THEME_KEY
import org.futo.inputmethod.latin.uix.setSettingBlocking
import org.futo.inputmethod.latin.uix.theme.ZipThemes
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

data class AppTheme(
    val id: String = UUID.randomUUID().toString().take(6),
    val name: String = "ثيمي",
    val bgColor: Color = Color(0xFFFDE8C4),
    val keyColor: Color = Color(0xFFE8F0D8),
    val borderColor: Color = Color(0xFF8BA87E),
    val textColor: Color = Color(0xFF1A1A1A),
    val borderWidth: Float = 1f,
    val opacity: Float = 1f,
    val textSize: Float = 16f,
    val customKeyColors: Map<String, Color> = emptyMap(),
    val customKeyImages: Map<String, String> = emptyMap()
)

object ThemeManager {
    fun applyTheme(context: Context, theme: AppTheme) {
        try {
            val customThemeDir = File(context.filesDir, "themes/custom")
            customThemeDir.mkdirs()
            val zipFile = File(customThemeDir, "studio_${theme.id}.zip")

            FileOutputStream(zipFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    val toml = StringBuilder()
                    toml.append("name = \"${theme.name}\"\nid = \"${theme.id}\"\n[colors]\n")
                    toml.append("keyboard_surface = \"${String.format("#%08X", theme.bgColor.toArgb())}\"\n")
                    toml.append("keyboard_container = \"${String.format("#%08X", theme.keyColor.toArgb())}\"\n")
                    toml.append("on_keyboard_container = \"${String.format("#%08X", theme.textColor.toArgb())}\"\n")
                    toml.append("outline = \"${String.format("#%08X", theme.borderColor.toArgb())}\"\n")

                    zos.putNextEntry(ZipEntry("theme.txt"))
                    zos.write(toml.toString().toByteArray())
                    zos.closeEntry()

                    theme.customKeyImages.forEach { (key, uriStr) ->
                        val imgName = "key_$key.png"
                        context.contentResolver.openInputStream(Uri.parse(uriStr))?.use { isImg ->
                            zos.putNextEntry(ZipEntry(imgName))
                            isImg.copyTo(zos)
                            zos.closeEntry()
                        }
                    }
                }
            }
            
            // حفظ الإعدادات بشكل متزامن لتجنب مشاكل الـ Coroutines
            context.setSettingBlocking(THEME_KEY.key, "custom_${theme.name}")
            ZipThemes.updateCount.intValue++

            Toast.makeText(context, "تم التطبيق على الكيبورد!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "خطأ: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

fun generateColors(): List<Color> {
    val colors = mutableListOf<Color>()
    for (i in 0 until 50) colors.add(Color.hsv((i * 7.2f) % 360f, 0.6f, 1f))
    for (i in 0 until 50) colors.add(Color.hsv((i * 7.2f) % 360f, 0.9f, 0.5f))
    return colors
}
val LibraryColors = generateColors()

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
                    IconButton(onClick = { ThemeManager.applyTheme(context, currentTheme) }) { 
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
            Box(modifier = Modifier.fillMaxWidth().weight(0.45f).background(Color(0xFFFDE8C4)).padding(4.dp)) {
                RealLivePreview(currentTheme)
            }
            Divider(thickness = 2.dp, color = Color.Gray)
            Box(modifier = Modifier.fillMaxWidth().weight(0.55f)) {
                EditorArea(currentTheme, onThemeUpdate = { currentTheme = it })
            }
        }
    }
}

@Composable
fun RealLivePreview(theme: AppTheme) {
    var selectedKey by remember { mutableStateOf<String?>(null) }
    
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠").forEach { key -> 
                KeyItem(key, theme, selectedKey == key, { selectedKey = if(selectedKey == key) null else key }, Modifier.weight(1f)) 
            }
        }
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج").forEach { key -> 
                KeyItem(key, theme, selectedKey == key, { selectedKey = if(selectedKey == key) null else key }, Modifier.weight(1f)) 
            }
        }
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط").forEach { key -> 
                KeyItem(key, theme, selectedKey == key, { selectedKey = if(selectedKey == key) null else key }, Modifier.weight(1f)) 
            }
        }
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ", "د").forEach { key -> 
                KeyItem(key, theme, selectedKey == key, { selectedKey = if(selectedKey == key) null else key }, Modifier.weight(1f)) 
            }
        }
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            KeyItem("١٢٣", theme, false, { }, Modifier.weight(1f))
            KeyItem("😊", theme, false, { }, Modifier.weight(1f))
            KeyItem("مسافة", theme, false, { }, Modifier.weight(4f))
            KeyItem("↵", theme, false, { }, Modifier.weight(1f))
        }
    }
}

@Composable
fun KeyItem(text: String, theme: AppTheme, isSelected: Boolean, onClick: () -> Unit, mod: Modifier) {
    val context = LocalContext.current
    val color = theme.customKeyColors[text] ?: theme.keyColor
    val imgUri = theme.customKeyImages[text]

    val imgBitmap = remember(imgUri) {
        imgUri?.let { Uri.parse(it) }?.let { uri -> context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it).asImageBitmap() } }
    }

    Box(
        modifier = mod.fillMaxHeight().alpha(theme.opacity).clip(RoundedCornerShape(8.dp))
            .background(color)
            .border(if (isSelected) 3.dp else theme.borderWidth.dp, if (isSelected) Color.Red else theme.borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imgBitmap != null) {
            Image(bitmap = imgBitmap, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)))
        } else {
            Text(text, color = theme.textColor, fontSize = theme.textSize.sp, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal)
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
                text = if (selectedKey != null) "أنت تعدل الزر: [ $selectedKey ]" else "اضغط على أي زر في المعاينة لتحديده",
                fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (selectedKey != null) Color.Red else Color(0xFF7E57C2)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { innerView = 1 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الألوان 🎨") }
                Button(onClick = { innerView = 2 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("رفع صورة 🖼️") }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { innerView = 3 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("الشفافية 🌫️") }
                Button(onClick = { innerView = 4 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("النص 🔤") }
            }
        }
        1 -> ColorEditor(theme, selectedKey, onThemeUpdate) { innerView = 0 }
        2 -> ImageEditor(theme, selectedKey, onThemeUpdate) { innerView = 0 }
        3 -> OpacityEditor(theme, onThemeUpdate) { innerView = 0 }
        4 -> TextEditor(theme, onThemeUpdate) { innerView = 0 }
    }
}

@Composable
fun ColorEditor(theme: AppTheme, targetKey: String?, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    var target by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }; Text("مكتبة الألوان", fontWeight = FontWeight.Bold) }
        
        TabRow(selectedTabIndex = target) {
            Tab(selected = target == 0, onClick = { target = 0 }, text = { Text("الخلفية") })
            Tab(selected = target == 1, onClick = { target = 1 }, text = { Text("الشكل") })
            Tab(selected = target == 2, onClick = { target = 2 }, text = { Text("الحدود") })
        }

        LazyVerticalGrid(columns = GridCells.Fixed(8), modifier = Modifier.padding(8.dp)) {
            items(LibraryColors) { c ->
                Box(Modifier.padding(4.dp).aspectRatio(1f).clip(CircleShape).background(c).clickable {
                    if (targetKey == null) {
                        if (target == 0) onUpdate(theme.copy(bgColor = c))
                        else if (target == 1) onUpdate(theme.copy(keyColor = c))
                        else onUpdate(theme.copy(borderColor = c))
                    } else {
                        if (target == 1) onUpdate(theme.copy(customKeyColors = theme.customKeyColors + (targetKey to c)))
                    }
                })
            }
        }
    }
}

@Composable
fun ImageEditor(theme: AppTheme, targetKey: String?, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> 
        uri?.let { if (targetKey != null) onUpdate(theme.copy(customKeyImages = theme.customKeyImages + (targetKey to it.toString()))) } 
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }; Text("رفع صورة", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(16.dp))
        if (targetKey == null) {
            Text("الرجاء تحديد زر من المعاينة أولاً.")
        } else {
            Text("رفع صورة للزر: $targetKey")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) { Text("اختيار صورة من الهاتف") }
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
fun TextEditor(theme: AppTheme, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
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
