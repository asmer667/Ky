package org.futo.inputmethod.latin.uix.settings.pages.themes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DefaultBgColor = Color(0xFFFDE8C4)
val DefaultKeyColor = Color(0xFFE8F0D8)
val DefaultKeyTextColor = Color(0xFF1A1A1A)
val DefaultBorderColor = Color(0xFF8BA87E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeStudioScreen(onBack: () -> Unit) {
    var keyboardBgColor by remember { mutableStateOf(DefaultBgColor) }
    var bgOpacity by remember { mutableStateOf(1f) }
    var keyColor by remember { mutableStateOf(DefaultKeyColor) }
    var keyTextColor by remember { mutableStateOf(DefaultKeyTextColor) }
    var borderColor by remember { mutableStateOf(DefaultBorderColor) }
    var textSize by remember { mutableStateOf(16f) }

    var selectedPage by remember { mutableIntStateOf(0) }

    // استخدمنا أيقونات أساسية فقط لتجنب أخطاء البناء
    val editorPages = listOf(
        EditorPage("الخلفية", Icons.Default.Settings),
        EditorPage("الكتابة", Icons.Default.Edit),
        EditorPage("الوظيفية", Icons.Default.Build),
        EditorPage("أدوات", Icons.Default.Create),
        EditorPage("المسافة", Icons.Default.Menu),
        EditorPage("الأرقام", Icons.Default.List),
        EditorPage("الخطوط", Icons.Default.Info),
        EditorPage("العلوية", Icons.Default.MoreVert)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استوديو التصميم", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Check, "حفظ") }
                    IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Share, "تصدير") }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(vertical = 4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    editorPages.forEachIndexed { index, _ ->
                        val color = if (selectedPage == index) MaterialTheme.colorScheme.primary else Color.LightGray
                        Box(modifier = Modifier.padding(horizontal = 2.dp).size(6.dp).clip(RoundedCornerShape(50)).background(color))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(editorPages) { index, page ->
                        val isSelected = selectedPage == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { selectedPage = index }
                                .padding(8.dp)
                                .width(64.dp)
                        ) {
                            Icon(page.icon, contentDescription = page.title, tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(page.title, fontSize = 10.sp, textAlign = TextAlign.Center, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(0.45f)
                    .background(keyboardBgColor.copy(alpha = bgOpacity))
                    .padding(8.dp)
            ) {
                KeyboardPreview(keyColor, keyTextColor, borderColor, textSize)
            }

            Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Box(modifier = Modifier.fillMaxWidth().weight(0.55f).background(MaterialTheme.colorScheme.surface)) {
                when (selectedPage) {
                    0 -> BackgroundEditor(keyboardBgColor, { keyboardBgColor = it }, bgOpacity, { bgOpacity = it })
                    1 -> ColorAndShapeEditor("أزرار الكتابة", keyColor, { keyColor = it }, borderColor)
                    2 -> ColorAndShapeEditor("الأزرار الوظيفية", keyColor, { keyColor = it }, borderColor)
                    3 -> ColorAndShapeEditor("أدوات التحرير", keyColor, { keyColor = it }, borderColor)
                    4 -> ColorAndShapeEditor("زر المسافة", keyColor, { keyColor = it }, borderColor)
                    5 -> ColorAndShapeEditor("الأرقام", keyColor, { keyColor = it }, borderColor)
                    6 -> FontEditor(textSize, { textSize = it }, keyTextColor, { keyTextColor = it })
                    7 -> TopToolbarEditor(keyTextColor, { keyTextColor = it })
                }
            }
        }
    }
}

data class EditorPage(val title: String, val icon: ImageVector)

@Composable
fun KeyboardPreview(keyColor: Color, textColor: Color, borderColor: Color, textSize: Float) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().height(30.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MoreVert, null, tint = textColor, modifier = Modifier.size(16.dp))
            Icon(Icons.Default.Search, null, tint = textColor, modifier = Modifier.size(16.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = textColor, modifier = Modifier.size(16.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = textColor, modifier = Modifier.size(16.dp))
            Icon(Icons.Default.Edit, null, tint = textColor, modifier = Modifier.size(16.dp))
            Icon(Icons.Default.Share, null, tint = textColor, modifier = Modifier.size(16.dp))
            Icon(Icons.Default.Face, null, tint = textColor, modifier = Modifier.size(16.dp))
            Icon(Icons.Default.Settings, null, tint = textColor, modifier = Modifier.size(16.dp))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 1..9) { PreviewKey(i.toString(), keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize) }
            PreviewKey("0", keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PreviewKey("تحديد", keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize)
            PreviewKey("مسح", keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize)
            PreviewKey("لصق", keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize)
            PreviewKey("نسخ", keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize)
        }
        val row1 = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج")
        val row2 = listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط")
        val row3 = listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ", "د")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            row1.forEach { PreviewKey(it, keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            row2.forEach { PreviewKey(it, keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            row3.forEach { PreviewKey(it, keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize) }
            PreviewKey("⌫", keyColor, textColor, borderColor, modifier = Modifier.weight(1.5f), textSize, icon = Icons.AutoMirrored.Filled.ArrowBack)
        }
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PreviewKey("123", keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize)
            PreviewKey("😊", keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize)
            PreviewKey("العربية", keyColor, textColor, borderColor, modifier = Modifier.weight(4f), textSize)
            PreviewKey("،", keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize)
            PreviewKey("↵", keyColor, textColor, borderColor, modifier = Modifier.weight(1f), textSize, icon = Icons.Default.Check)
        }
    }
}

@Composable
fun PreviewKey(label: String, bgColor: Color, textColor: Color, borderColor: Color, modifier: Modifier = Modifier, textSize: Float, icon: ImageVector? = null) {
    Box(modifier = modifier.fillMaxHeight().clip(RoundedCornerShape(8.dp)).background(bgColor).border(1.dp, borderColor, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
        if (icon != null) Icon(icon, contentDescription = label, tint = textColor, modifier = Modifier.size(textSize.dp))
        else Text(text = label, color = textColor, fontSize = textSize.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun BackgroundEditor(bgColor: Color, onBgColorChange: (Color) -> Unit, opacity: Float, onOpacityChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("الخلفية والشفافية", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("لون الخلفية:", fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp)).background(bgColor).border(1.dp, Color.Gray, RoundedCornerShape(8.dp)))
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("درجة الشفافية (Opacity): ${(opacity * 100).toInt()}%")
        Slider(value = opacity, onValueChange = onOpacityChange, valueRange = 0f..1f)
        
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("رفع صورة خلفية للكيبورد")
        }
    }
}

@Composable
fun ColorAndShapeEditor(title: String, currentColor: Color, onColorChange: (Color) -> Unit, borderColor: Color) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("تعديل: $title", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("لون الزر:", fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp)).background(currentColor).border(1.dp, Color.Gray, RoundedCornerShape(8.dp)))
        
        Spacer(modifier = Modifier.height(12.dp))
        Text("لون الحدود:", fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp)).background(borderColor).border(1.dp, Color.Gray, RoundedCornerShape(8.dp)))
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("تغيير شكل الزر (رفع صورة PNG)")
        }
    }
}

@Composable
fun FontEditor(textSize: Float, onTextSizeChange: (Float) -> Unit, textColor: Color, onTextColorChange: (Color) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("الخط والنص", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("حجم النص: ${textSize.toInt()} sp")
        Slider(value = textSize, onValueChange = onTextSizeChange, valueRange = 10f..30f)
        
        Spacer(modifier = Modifier.height(8.dp))
        Text("لون النص:", fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp)).background(textColor).border(1.dp, Color.Gray, RoundedCornerShape(8.dp)))
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("اختيار ملف خط (.ttf)")
        }
    }
}

@Composable
fun TopToolbarEditor(textColor: Color, onTextColorChange: (Color) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("أدوات التحرير العلوية", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("تعديل أيقونات (النسخ، اللصق، الإعدادات) الموجودة في الشريط العلوي للكيبورد.", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("لون الأيقونات:", fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp)).background(textColor).border(1.dp, Color.Gray, RoundedCornerShape(8.dp)))
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("تغيير أيقونات الشريط العلوي")
        }
    }
}
