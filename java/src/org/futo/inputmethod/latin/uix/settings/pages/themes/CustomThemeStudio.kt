package org.futo.inputmethod.latin.uix.settings.pages.themes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// مكتبة ألوان ضخمة (أكثر من 100 لون)
// ==========================================
val ColorsLight = listOf(
    Color(0xFFFFFFFF), Color(0xFFF5F5F5), Color(0xFFEEEEEE), Color(0xFFE0E0E0), Color(0xFFBDBDBD),
    Color(0xFFFFEBEE), Color(0xFFFFCDD2), Color(0xFFEF9A9A), Color(0xFFE57373), Color(0xFFEF5350),
    Color(0xFFFCE4EC), Color(0xFFF8BBD0), Color(0xFFF48FB1), Color(0xFFF06292), Color(0xFFEC407A),
    Color(0xFFF3E5F5), Color(0xFFE1BEE7), Color(0xFFCE93D8), Color(0xFFBA68C8), Color(0xFFAB47BC),
    Color(0xFFEDE7F6), Color(0xFFD1C4E9), Color(0xFFB39DDB), Color(0xFF9575CD), Color(0xFF7E57C2),
    Color(0xFFE8EAF6), Color(0xFFC5CAE9), Color(0xFF9FA8DA), Color(0xFF7986CB), Color(0xFF5C6BC0),
    Color(0xFFE3F2FD), Color(0xFFBBDEFB), Color(0xFF90CAF9), Color(0xFF64B5F6), Color(0xFF42A5F5),
    Color(0xFFE1F5FE), Color(0xFFB3E5FC), Color(0xFF81D4FA), Color(0xFF4FC3F7), Color(0xFF29B6F6),
    Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA), Color(0xFF4DD0E1), Color(0xFF26C6DA),
    Color(0xFFE0F2F1), Color(0xFFB2DFDB), Color(0xFF80CBC4), Color(0xFF4DB6AC), Color(0xFF26A69A),
    Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7), Color(0xFF81C784), Color(0xFF66BB6A),
    Color(0xFFF1F8E9), Color(0xFFDCEDC8), Color(0xFFC5E1A5), Color(0xFFAED581), Color(0xFF9CCC65),
    Color(0xFFF9FBE7), Color(0xFFF0F4C3), Color(0xFFE6EE9C), Color(0xFFDCE775), Color(0xFFD4E157),
    Color(0xFFFFFDE7), Color(0xFFFFF9C4), Color(0xFFFFF59D), Color(0xFFFFF176), Color(0xFFFFEE58),
    Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFFFFE082), Color(0xFFFFD54F), Color(0xFFFFCA28),
    Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFCC80), Color(0xFFFFB74D), Color(0xFFFFA726),
    Color(0xFFFBE9E7), Color(0xFFFFCCBC), Color(0xFFFFAB91), Color(0xFFFF8A65), Color(0xFFFF7043),
    Color(0xFFEFEBE9), Color(0xFFD7CCC8), Color(0xFFBCAAA4), Color(0xFFA1887F), Color(0xFF8D6E63),
    Color(0xFFECEFF1), Color(0xFFCFD8DC), Color(0xFFB0BEC5), Color(0xFF90A4AE), Color(0xFF78909C)
)

val ColorsDark = listOf(
    Color(0xFF000000), Color(0xFF212121), Color(0xFF424242), Color(0xFF616161), Color(0xFF757575),
    Color(0xFFB71C1C), Color(0xFFC62828), Color(0xFFD32F2F), Color(0xFFE53935), Color(0xFFF44336),
    Color(0xFF880E4F), Color(0xFFAD1457), Color(0xFFC2185B), Color(0xFFD81B60), Color(0xFFE91E63),
    Color(0xFF4A148C), Color(0xFF6A1B9A), Color(0xFF7B1FA2), Color(0xFF8E24AA), Color(0xFF9C27B0),
    Color(0xFF311B92), Color(0xFF4527A0), Color(0xFF512DA8), Color(0xFF5E35B1), Color(0xFF673AB7),
    Color(0xFF1A237E), Color(0xFF283593), Color(0xFF303F9F), Color(0xFF3949AB), Color(0xFF3F51B5),
    Color(0xFF0D47A1), Color(0xFF1565C0), Color(0xFF1976D2), Color(0xFF1E88E5), Color(0xFF2196F3),
    Color(0xFF01579B), Color(0xFF0277BD), Color(0xFF0288D1), Color(0xFF039BE5), Color(0xFF03A9F4),
    Color(0xFF006064), Color(0xFF00838F), Color(0xFF0097A7), Color(0xFF00ACC1), Color(0xFF00BCD4),
    Color(0xFF004D40), Color(0xFF00695C), Color(0xFF00796B), Color(0xFF00897B), Color(0xFF009688),
    Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C), Color(0xFF43A047), Color(0xFF4CAF50),
    Color(0xFF33691E), Color(0xFF558B2F), Color(0xFF689F38), Color(0xFF7CB342), Color(0xFF8BC34A),
    Color(0xFF827717), Color(0xFF9E9D24), Color(0xFFAFB42B), Color(0xFFC0CA33), Color(0xFFCDDC39),
    Color(0xFFF57F17), Color(0xFFF9A825), Color(0xFFFBC02D), Color(0xFFFDD835), Color(0xFFFFEB3B),
    Color(0xFFE65100), Color(0xFFEF6C00), Color(0xFFF57C00), Color(0xFFFB8C00), Color(0xFFFF9800),
    Color(0xFFBF360C), Color(0xFFD84315), Color(0xFFE64A19), Color(0xFFF4511E), Color(0xFFFF5722),
    Color(0xFF3E2723), Color(0xFF4E342E), Color(0xFF5D4037), Color(0xFF6D4C41), Color(0xFF795548),
    Color(0xFF263238), Color(0xFF37474F), Color(0xFF455A64), Color(0xFF546E7A), Color(0xFF607D8B)
)

val ColorsGradients = listOf(
    listOf(Color(0xFFFF9A9E), Color(0xFFFECFEF)), listOf(Color(0xFFA18CD1), Color(0xFFFBC2EB)),
    listOf(Color(0xFF84FAB0), Color(0xFF8FD3F4)), listOf(Color(0xFFFCCB90), Color(0xFFD57EEB)),
    listOf(Color(0xFF43C6AC), Color(0xFFF8FFAE)), listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)),
    listOf(Color(0xFFee9ca7), Color(0xFFffdde1)), listOf(Color(0xFFF3904F), Color(0xFF3B4371)),
    listOf(Color(0xFF4CA1AF), Color(0xFFC4E0E5)), listOf(Color(0xFFFF758C), Color(0xFFFF7EB3)),
    listOf(Color(0xFF00b09b), Color(0xFF96c93d)), listOf(Color(0xFFff9966), Color(0xFFff5e62)),
    listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)), listOf(Color(0xFF1F1C18), Color(0xFF8E0E00)),
    listOf(Color(0xFFc94b4b), Color(0xFF4b134f)), listOf(Color(0xFF56ab2f), Color(0xFFa8e063))
)

// ==========================================
// أشكال حقيقية
// ==========================================
class StarShape : Shape {
    override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline {
        val path = Path()
        val centerX = size.width / 2
        val centerY = size.height / 2
        val outerRadius = size.width / 2
        val innerRadius = outerRadius * 0.4f
        val points = 5
        for (i in 0 until points * 2) {
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val angle = Math.PI / points * i - Math.PI / 2
            val x = (centerX + radius * Math.cos(angle)).toFloat()
            val y = (centerY + radius * Math.sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

val shapesSquare = listOf(RoundedCornerShape(0.dp), RoundedCornerShape(4.dp), RoundedCornerShape(8.dp), RoundedCornerShape(16.dp), RoundedCornerShape(24.dp), CircleShape)
val shapesPolygon = listOf(CutCornerShape(4.dp), CutCornerShape(8.dp), CutCornerShape(16.dp))
val shapesStar = listOf(StarShape())
val shapesAbstract = listOf(RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp), RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp))

// ==========================================
// الشاشة الرئيسية
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeStudioScreen(onBack: () -> Unit) {
    var keyboardBgColor by remember { mutableStateOf<Brush>(SolidColor(Color(0xFFFDE8C4))) }
    var keyBrush by remember { mutableStateOf<Brush>(SolidColor(Color(0xFFE8F0D8))) }
    var keyTextColor by remember { mutableStateOf(Color(0xFF1A1A1A)) }
    var borderColor by remember { mutableStateOf(Color(0xFF8BA87E)) }
    var keyShape by remember { mutableStateOf<Shape>(RoundedCornerShape(16.dp)) }

    var selectedPage by remember { mutableIntStateOf(1) }
    val pages = listOf("الخلفية", "الكتابة", "الوظيفية", "أدوات", "المسافة", "الأرقام", "الخطوط", "العلوية")
    val icons = listOf(Icons.Default.Settings, Icons.Default.Edit, Icons.Default.Build, Icons.Default.Create, Icons.Default.Menu, Icons.Default.List, Icons.Default.Info, Icons.Default.MoreVert)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استوديو التصميم", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") } },
                actions = {
                    IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Check, "حفظ وتطبيق", tint = Color(0xFF4CAF50)) }
                    IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Share, "تصدير") }
                    IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Send, "مشاركة") }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)) {
                LazyRow(contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(pages) { index, title ->
                        val isSelected = selectedPage == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { selectedPage = index }
                                .padding(8.dp).width(60.dp)
                        ) {
                            Icon(icons[index], contentDescription = title, tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray)
                            Text(title, fontSize = 10.sp, textAlign = TextAlign.Center, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.fillMaxWidth().weight(0.4f).background(keyboardBgColor).padding(4.dp)) {
                RealKeyboardPreview(keyBrush, keyTextColor, borderColor, keyShape)
            }
            Divider(thickness = 2.dp)
            Box(modifier = Modifier.fillMaxWidth().weight(0.6f)) {
                if (selectedPage == 0) {
                    BackgroundEditor(keyboardBgColor, { keyboardBgColor = it })
                } else {
                    KeyEditor(keyBrush, { keyBrush = it }, keyShape, { keyShape = it })
                }
            }
        }
    }
}

// ==========================================
// محرر المفاتيح
// ==========================================
@Composable
fun KeyEditor(currentBrush: Brush, onBrushChange: (Brush) -> Unit, currentShape: Shape, onShapeChange: (Shape) -> Unit) {
    var innerScreen by remember { mutableIntStateOf(0) }

    when (innerScreen) {
        0 -> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("ماذا تريد أن تعدل؟", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { innerScreen = 1 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("مكتبة الألوان 🎨") }
                    Button(onClick = { innerScreen = 2 }, modifier = Modifier.weight(1f).height(60.dp)) { Text("مكتبة الأشكال 🔷") }
                }
            }
        }
        1 -> ColorLibraryScreen(onBrushChange = { onBrushChange(it); innerScreen = 0 }, onBack = { innerScreen = 0 })
        2 -> ShapeLibraryScreen(onShapeChange = { onShapeChange(it); innerScreen = 0 }, onBack = { innerScreen = 0 })
    }
}

// ==========================================
// مكتبة الألوان (المتداخلة)
// ==========================================
@Composable
fun ColorLibraryScreen(onBrushChange: (Brush) -> Unit, onBack: () -> Unit) {
    var colorMode by remember { mutableIntStateOf(0) }
    var solidTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }
            Text("مكتبة الألوان", fontWeight = FontWeight.Bold)
        }
        
        TabRow(selectedTabIndex = colorMode) {
            Tab(selected = colorMode == 0, onClick = { colorMode = 0 }, text = { Text("أحادية") })
            Tab(selected = colorMode == 1, onClick = { colorMode = 1 }, text = { Text("متدرجة") })
        }

        if (colorMode == 0) {
            TabRow(selectedTabIndex = solidTab) {
                Tab(selected = solidTab == 0, onClick = { solidTab = 0 }, text = { Text("فاتح") })
                Tab(selected = solidTab == 1, onClick = { solidTab = 1 }, text = { Text("داكن") })
            }
            
            val colors = if (solidTab == 0) ColorsLight else ColorsDark
            LazyVerticalGrid(columns = GridCells.Fixed(6), contentPadding = PaddingValues(8.dp)) {
                items(colors) { c ->
                    Box(Modifier.padding(4.dp).aspectRatio(1f).clip(CircleShape).background(c).border(1.dp, Color.Gray, CircleShape).clickable { onBrushChange(SolidColor(c)) })
                }
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = PaddingValues(8.dp)) {
                items(ColorsGradients) { colors ->
                    Box(Modifier.padding(4.dp).aspectRatio(1.5f).clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(colors)).clickable { onBrushChange(Brush.linearGradient(colors)) })
                }
            }
        }
    }
}

// ==========================================
// مكتبة الأشكال (المتداخلة)
// ==========================================
@Composable
fun ShapeLibraryScreen(onShapeChange: (Shape) -> Unit, onBack: () -> Unit) {
    var shapeCat by remember { mutableIntStateOf(0) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }
            Text("مكتبة الأشكال", fontWeight = FontWeight.Bold)
        }
        
        TabRow(selectedTabIndex = shapeCat) {
            Tab(selected = shapeCat == 0, onClick = { shapeCat = 0 }, text = { Text("مربع") })
            Tab(selected = shapeCat == 1, onClick = { shapeCat = 1 }, text = { Text("مضلع") })
            Tab(selected = shapeCat == 2, onClick = { shapeCat = 2 }, text = { Text("نجوم") })
            Tab(selected = shapeCat == 3, onClick = { shapeCat = 3 }, text = { Text("تجريدي") })
        }

        val shapes = when(shapeCat) { 0 -> shapesSquare; 1 -> shapesPolygon; 2 -> shapesStar; else -> shapesAbstract }
        
        LazyVerticalGrid(columns = GridCells.Fixed(5), contentPadding = PaddingValues(8.dp)) {
            items(shapes) { s ->
                Box(Modifier.padding(8.dp).aspectRatio(1f).clip(s).background(Color.LightGray).border(1.dp, Color.Gray, s).clickable { onShapeChange(s) })
            }
        }
    }
}

// ==========================================
// المعاينة (نسخة طبق الأصل 100%)
// ==========================================
@Composable
fun RealKeyboardPreview(keyBrush: Brush, textColor: Color, borderColor: Color, shape: Shape) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        // شريط الأدوات العلوي (بالأيقونات الدقيقة)
        Row(modifier = Modifier.fillMaxWidth().height(28.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            listOf(
                Icons.Default.MoreHoriz, 
                Icons.Default.Language, 
                Icons.Default.Undo, 
                Icons.Default.Redo, 
                Icons.Default.Edit, 
                Icons.Default.ContentCopy, 
                Icons.Default.Face, 
                Icons.Default.Settings
            ).forEach {
                Icon(it, null, tint = textColor, modifier = Modifier.size(14.dp))
            }
        }
        // شريط الاقتراحات
        Row(modifier = Modifier.fillMaxWidth().height(20.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Text("شوف", color = textColor, fontSize = 12.sp)
            Text("اشتي", color = textColor, fontSize = 12.sp)
            Text("طيب", color = textColor, fontSize = 12.sp)
        }
        // الأرقام العربية
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠").forEach {
                PK(it, keyBrush, textColor, borderColor, shape, 14f, Modifier.weight(1f))
            }
        }
        // أزرار التحديد
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("تحديد", "مسح", "لصق", "نسخ").forEach { PK(it, keyBrush, textColor, borderColor, shape, 12f, Modifier.weight(1f)) }
        }
        // صفوف الحروف
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج").forEach { PK(it, keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f)) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط").forEach { PK(it, keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f)) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ", "د").forEach { PK(it, keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f)) }
            PK("⌫", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1.5f), Icons.Default.Backspace)
        }
        // الصف السفلي
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PK("١٢٣", keyBrush, textColor, borderColor, shape, 14f, Modifier.weight(1f))
            PK("😊", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f))
            PK("العربية", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(4f))
            PK(".", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f))
            PK("↵", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f), Icons.Default.KeyboardReturn)
        }
    }
}

@Composable
fun PK(text: String, brush: Brush, fg: Color, border: Color, shape: Shape, size: Float, mod: Modifier, icon: ImageVector? = null) {
    Box(modifier = mod.fillMaxHeight().clip(shape).background(brush).border(1.dp, border, shape), contentAlignment = Alignment.Center) {
        if (icon != null) Icon(icon, null, tint = fg, modifier = Modifier.size(size.dp))
        else Text(text, color = fg, fontSize = size.sp, textAlign = TextAlign.Center, maxLines = 1)
    }
}

@Composable
fun BackgroundEditor(bgBrush: Brush, onBgChange: (Brush) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("الخلفية", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp)).background(bgBrush).border(1.dp, Color.Gray, RoundedCornerShape(8.dp)))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) { Text("رفع صورة خلفية") }
    }
}
