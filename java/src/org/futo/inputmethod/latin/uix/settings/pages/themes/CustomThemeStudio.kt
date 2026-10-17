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
import kotlinx.coroutines.runBlocking

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
    val customKeyBorderColors: Map<String, Color> = emptyMap(),
    val customKeyImages: Map<String, String> = emptyMap()
)

object ThemeManager {
    fun applyTheme(context: Context, theme: AppTheme) {
        try {
            // 1. إنشاء المجلد الصحيح الذي يقرأ منه FUTO
            val customThemeDir = File(context.filesDir, "themes/custom")
            customThemeDir.mkdirs()

            // 2. بناء ملف theme.txt
            FileOutputStream(zipFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    val toml = StringBuilder()

                    zos.putNextEntry(ZipEntry("theme.txt"))
                    zos.write(toml.toString().toByteArray())
                    zos.closeEntry()

                    // إضافة الصور
                    theme.customKeyImages.forEach { (key, uriStr) ->
                        val imgName = "key_$key.png"
                        context.contentResolver.openInputStream(Uri.parse(uriStr))?.use { isImg ->
                            zos.putNextEntry(ZipEntry(imgName))
                            isImg.copyTo(zos)
                            zos.closeEntry()
            
            // 3. تطبيق الثيم فوراً (استخدام الدوال الحقيقية في FUTO)
            ZipThemes.updateCount.intValue++

            Toast.makeText(context, "تم التطبيق على الكيبورد بنجاح!", Toast.LENGTH_LONG).show()

fun generateSolidColors(): List<Color> {
    val colors = mutableListOf<Color>()
    for (i in 0 until 100) colors.add(Color.hsv((i * 3.6f) % 360f, 0.6f, 1f))
    for (i in 0 until 100) colors.add(Color.hsv((i * 3.6f) % 360f, 0.9f, 0.5f))
    return colors
val LibraryColors = generateSolidColors()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeStudioScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val tabs = listOf("الخلفية", "الكتابة", "الوظيفية", "المسافة", "العلوية")

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                        Icon(Icons.Default.Check, "حفظ وتطبيق", tint = Color(0xFF69F0AE)) 
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7E57C2))
            )
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFF3E5F5)) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                    )
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.fillMaxWidth().weight(0.45f).background(Color(0xFFFDE8C4)).padding(4.dp)) {
                RealLivePreview(currentTheme)
            Divider(thickness = 2.dp, color = Color.Gray)
            Box(modifier = Modifier.fillMaxWidth().weight(0.55f)) {

@Composable
fun RealLivePreview(theme: AppTheme) {
    
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠").forEach { key -> 
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج").forEach { key -> 
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط").forEach { key -> 
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ", "د").forEach { key -> 
        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {

@Composable
fun KeyItem(text: String, theme: AppTheme, isSelected: Boolean, onClick: () -> Unit, mod: Modifier) {
    val context = LocalContext.current
    val color = theme.customKeyColors[text] ?: theme.keyColor
    val borderColor = theme.customKeyBorderColors[text] ?: theme.borderColor
    val imgUri = theme.customKeyImages[text]

    val imgBitmap = remember(imgUri) {

    Box(
        modifier = mod.fillMaxHeight().alpha(theme.opacity).clip(RoundedCornerShape(8.dp))
            .background(color)
            .border(if (isSelected) 3.dp else theme.borderWidth.dp, if (isSelected) Color.Red else borderColor, RoundedCornerShape(8.dp))
        contentAlignment = Alignment.Center
    ) {
        if (imgBitmap != null) {
            Image(bitmap = imgBitmap, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)))
            Text(text, color = theme.textColor, fontSize = theme.textSize.sp, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal)
@Composable
fun EditorArea(theme: AppTheme, onThemeUpdate: (AppTheme) -> Unit) {
    
    when (innerView) {
        0 -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = if (selectedKey != null) "أنت تعدل الزر: [ $selectedKey ]" else "اضغط على أي زر في المعاينة لتحديده",
                fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (selectedKey != null) Color.Red else Color(0xFF7E57C2)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

@Composable
fun ColorEditor(theme: AppTheme, targetKey: String?, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {

    Column(modifier = Modifier.fillMaxSize()) {
        
        TabRow(selectedTabIndex = target) {

        LazyVerticalGrid(columns = GridCells.Fixed(8), modifier = Modifier.padding(8.dp)) {
            items(LibraryColors) { c ->
                Box(Modifier.padding(4.dp).aspectRatio(1f).clip(CircleShape).background(c).clickable {
                    if (targetKey == null) {
                        if (target == 0) onUpdate(theme.copy(bgColor = c))
                        else if (target == 1) onUpdate(theme.copy(keyColor = c))
                        else onUpdate(theme.copy(borderColor = c))
                        if (target == 1) onUpdate(theme.copy(customKeyColors = theme.customKeyColors + (targetKey to c)))
                        else if (target == 2) onUpdate(theme.copy(customKeyBorderColors = theme.customKeyBorderColors + (targetKey to c)))

@Composable
fun ImageEditor(theme: AppTheme, targetKey: String?, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> 
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(Modifier.height(16.dp))
        if (targetKey == null) {
            Text("الرجاء تحديد زر من المعاينة أولاً لتطبيق الصورة عليه.")
            Text("رفع صورة للزر: $targetKey")
            Spacer(Modifier.height(16.dp))

@Composable
fun OpacityEditor(theme: AppTheme, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(Modifier.height(16.dp))

@Composable
fun TextEditor(theme: AppTheme, onUpdate: (AppTheme) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(Modifier.height(16.dp))
        Spacer(Modifier.height(16.dp))
