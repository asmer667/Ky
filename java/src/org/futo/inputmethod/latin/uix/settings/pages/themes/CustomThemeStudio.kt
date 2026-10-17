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
// 1. مكتبات الألوان
// ==========================================
val ColorsLight = listOf(Color.White, Color(0xFFF5F5F5), Color(0xFFFFCDD2), Color(0xFFFFE082), Color(0xFFC8E6C9), Color(0xFFBBDEFB), Color(0xFFE1BEE7), Color(0xFFCFD8DC))
val ColorsDark = listOf(Color.Black, Color(0xFF212121), Color(0xFFB71C1C), Color(0xFFF57F17), Color(0xFF1B5E20), Color(0xFF0D47A1), Color(0xFF4A148C), Color(0xFF263238))
val ColorsGradients = listOf(
    listOf(Color(0xFFFF9A9E), Color(0xFFFECFEF)), listOf(Color(0xFFA18CD1), Color(0xFFFBC2EB)),
    listOf(Color(0xFF84FAB0), Color(0xFF8FD3F4)), listOf(Color(0xFFFCCB90), Color(0xFFD57EEB)),
    listOf(Color(0xFF43C6AC), Color(0xFFF8FFAE)), listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
)

// ==========================================
// 2. مكتبات الأشكال (توليد أشكال حقيقية)
// ==========================================
// شكل النجمة
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
val shapesStar = listOf(StarShape()) // شكل النجمة
val shapesAbstract = listOf(RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp), RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp))

// ==========================================
// 3. الشاشة الرئيسية
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeStudioScreen(onBack: () -> Unit) {
    var keyboardBgColor by remember { mutableStateOf<Brush>(SolidColor(Color(0xFFFDE8C4))) }
    var keyBrush by remember { mutableStateOf<Brush>(SolidColor(Color(0xFFE8F0D8))) }
    var keyTextColor by remember { mutableStateOf(Color(0xFF1A1A1A)) }
    var borderColor by remember { mutableStateOf(Color(0xFF8BA87E)) }
    var keyShape by remember { mutableStateOf<Shape>(RoundedCornerShape(16.dp)) }

    var selectedPage by remember { mutableIntStateOf(1) } // 0: خلفية, 1: كتابة...
    val pages = listOf("الخلفية", "الكتابة", "الوظيفية", "أدوات", "المسافة", "الأرقام", "الخطوط", "العلوية")
    val icons = listOf(Icons.Default.Settings, Icons.Default.Edit, Icons.Default.Build, Icons.Default.Create, Icons.Default.Menu, Icons.Default.List, Icons.Default.Info, Icons.Default.MoreVert)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استوديو التصميم", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") } },
                actions = {
                    // أزرار الحفظ، التصدير، والمشاركة
                    IconButton(onClick = { /* TODO: Save & Apply */ }) { Icon(Icons.Default.Check, "حفظ وتطبيق", tint = Color(0xFF4CAF50)) }
                    IconButton(onClick = { /* TODO: Export ZIP */ }) { Icon(Icons.Default.Share, "تصدير") }
                    IconButton(onClick = { /* TODO: Share */ }) { Icon(Icons.Default.Send, "مشاركة") }
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
            // المعاينة
            Box(modifier = Modifier.fillMaxWidth().weight(0.4f).background(keyboardBgColor).padding(4.dp)) {
                RealKeyboardPreview(keyBrush, keyTextColor, borderColor, keyShape)
            }
            Divider(thickness = 2.dp)
            // المساحة الذكية
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
// 4. محرر المفاتيح (النظام المتداخل)
// ==========================================
@Composable
fun KeyEditor(currentBrush: Brush, onBrushChange: (Brush) -> Unit, currentShape: Shape, onShapeChange: (Shape) -> Unit) {
    // 0: القائمة, 1: الألوان, 2: الأشكال
    var innerScreen by remember { mutableIntStateOf(0) }

    when (innerScreen) {
        0 -> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("ماذا تريد أن تعدل؟", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { innerScreen = 1 }, modifier = Modifier.weight(1f).height(60.dp)) {
                        Text("مكتبة الألوان 🎨")
                    }
                    Button(onClick = { innerScreen = 2 }, modifier = Modifier.weight(1f).height(60.dp)) {
                        Text("مكتبة الأشكال 🔷")
                    }
                }
            }
        }
        1 -> ColorLibraryScreen(onBrushChange = { onBrushChange(it); innerScreen = 0 }, onBack = { innerScreen = 0 })
        2 -> ShapeLibraryScreen(onShapeChange = { onShapeChange(it); innerScreen = 0 }, onBack = { innerScreen = 0 })
    }
}

// ==========================================
// 5. شاشة مكتبة الألوان (المتداخلة)
// ==========================================
@Composable
fun ColorLibraryScreen(onBrushChange: (Brush) -> Unit, onBack: () -> Unit) {
    var colorMode by remember { mutableIntStateOf(0) } // 0: أحادية, 1: متدرجة
    var solidTab by remember { mutableIntStateOf(0) } // 0: فاتح, 1: داكن, 2: مخصص

    Column(modifier = Modifier.fillMaxSize()) {
        // شريط علوي للرجوع
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }
            Text("مكتبة الألوان", fontWeight = FontWeight.Bold)
        }
        
        // اختيار النوع (أحادية/متدرجة)
        TabRow(selectedTabIndex = colorMode) {
            Tab(selected = colorMode == 0, onClick = { colorMode = 0 }, text = { Text("أحادية") })
            Tab(selected = colorMode == 1, onClick = { colorMode = 1 }, text = { Text("متدرجة") })
        }

        if (colorMode == 0) {
            // تبويبات الألوان الأحادية
            TabRow(selectedTabIndex = solidTab) {
                Tab(selected = solidTab == 0, onClick = { solidTab = 0 }, text = { Text("فاتح") })
                Tab(selected = solidTab == 1, onClick = { solidTab = 1 }, text = { Text("داكن") })
                Tab(selected = solidTab == 2, onClick = { solidTab = 2 }, text = { Text("مخصص (قريباً)") })
            }
            
            val colors = when(solidTab) { 0 -> ColorsLight; 1 -> ColorsDark; else -> ColorsLight }
            LazyVerticalGrid(columns = GridCells.Fixed(6), contentPadding = PaddingValues(8.dp)) {
                items(colors) { c ->
                    Box(Modifier.padding(4.dp).aspectRatio(1f).clip(CircleShape).background(c).border(1.dp, Color.Gray, CircleShape).clickable { onBrushChange(SolidColor(c)) })
                }
            }
        } else {
            // الألوان المتدرجة
            LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = PaddingValues(8.dp)) {
                items(ColorsGradients) { colors ->
                    Box(Modifier.padding(4.dp).aspectRatio(1.5f).clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(colors)).clickable { onBrushChange(Brush.linearGradient(colors)) })
                }
            }
        }
    }
}

// ==========================================
// 6. شاشة مكتبة الأشكال (المتداخلة)
// ==========================================
@Composable
fun ShapeLibraryScreen(onShapeChange: (Shape) -> Unit, onBack: () -> Unit) {
    var shapeCat by remember { mutableIntStateOf(0) } // 0: مربع, 1: مضلع, 2: نجوم, 3: تجريدي
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "رجوع") }
            Text("مكتبة الأشكال", fontWeight = FontWeight.Bold)
        }
        
        // تصنيفات الأشكال
        TabRow(selectedTabIndex = shapeCat) {
            Tab(selected = shapeCat == 0, onClick = { shapeCat = 0 }, text = { Text("مربع") })
            Tab(selected = shapeCat == 1, onClick = { shapeCat = 1 }, text = { Text("مضلع") })
            Tab(selected = shapeCat == 2, onClick = { shapeCat = 2 }, text = { Text("نجوم") })
            Tab(selected = shapeCat == 3, onClick = { shapeCat = 3 }, text = { Text("تجريدي") })
        }

        val shapes = when(shapeCat) {
            0 -> shapesSquare
            1 -> shapesPolygon
            2 -> shapesStar
            else -> shapesAbstract
        }
        
        LazyVerticalGrid(columns = GridCells.Fixed(5), contentPadding = PaddingValues(8.dp)) {
            items(shapes) { s ->
                Box(Modifier.padding(8.dp).aspectRatio(1f).clip(s).background(Color.LightGray).border(1.dp, Color.Gray, s).clickable { onShapeChange(s) })
            }
        }
    }
}

// ==========================================
// 7. المعاينة (Realistic Preview)
// ==========================================
@Composable
fun RealKeyboardPreview(keyBrush: Brush, textColor: Color, borderColor: Color, shape: Shape) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().height(32.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            listOf(Icons.Default.MoreHoriz, Icons.Default.Language, Icons.Default.Undo, Icons.Default.Redo, Icons.Default.Edit, Icons.Default.ContentCopy, Icons.Default.Face, Icons.Default.Settings).forEach {
                Icon(it, null, tint = textColor, modifier = Modifier.size(16.dp))
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            for (i in 1..9) { PK(i.toString(), keyBrush, textColor, borderColor, shape, 14f, Modifier.weight(1f)) }
            PK("0", keyBrush, textColor, borderColor, shape, 14f, Modifier.weight(1f))
        }
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
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PK("123", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f))
            PK("😊", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f))
            PK("العربية", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(4f))
            PK(".", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f))
            PK("↵", keyBrush, textColor, borderColor, shape, 16f, Modifier.weight(1f), Icons.Default.Search)
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
