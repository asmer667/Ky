package org.futo.inputmethod.latin.uix.settings.pages.fonts

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import java.io.File

/**
 * مدير الخطوط — يقرأ من assets + filesDir + يستورد من الجهاز.
 */
object FontManager {

    // مجلد تخزين الخطوط المستوردة
    fun importedDir(context: Context): File {
        val dir = File(context.filesDir, "fonts")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    // ملف الخط الحالي للعربية
    fun currentArabicFile(context: Context): File =
        File(importedDir(context), "current_arabic.ttf")

    // ملف الخط الحالي للإنجليزية
    fun currentEnglishFile(context: Context): File =
        File(importedDir(context), "current_english.ttf")

    /**
     * يقرأ قائمة الخطوط من assets/fonts/<lang>/
     */
    fun listBundledFonts(context: Context, language: String): List<String> {
        return try {
            context.assets.list("fonts/$language")?.filter {
                it.endsWith(".ttf", true) || it.endsWith(".otf", true)
            }?.sorted() ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    /**
     * يحمّل Typeface من assets.
     */
    fun loadBundledTypeface(context: Context, language: String, fileName: String): Typeface? {
        return try {
            Typeface.createFromAsset(context.assets, "fonts/$language/$fileName")
        } catch (e: Exception) { null }
    }

    /**
     * ينسخ خطًا من assets إلى filesDir/fonts/ ويُعيد الملف.
     */
    fun copyBundledToInternal(context: Context, language: String, fileName: String, target: File): Boolean {
        return try {
            context.assets.open("fonts/$language/$fileName").use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }
            true
        } catch (e: Exception) { false }
    }

    /**
     * ينسخ خطًا مستوردًا من URI إلى filesDir/fonts/.
     */
    fun importFromUri(context: Context, uri: Uri, target: File): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }
            target.exists() && target.length() > 1000
        } catch (e: Exception) { false }
    }

    /**
     * يحمّل Typeface من ملف داخلي.
     */
    fun loadInternalTypeface(file: File): Typeface? {
        return try {
            if (file.exists() && file.length() > 1000) {
                Typeface.createFromFile(file)
            } else null
        } catch (e: Exception) { null }
    }

    /**
     * يعيد اسم الملف الحالي للخط.
     */
    fun currentFontName(context: Context, language: String): String? {
        val file = if (language == "Arabic") currentArabicFile(context)
                   else currentEnglishFile(context)
        return if (file.exists()) file.nameWithoutExtension else null
    }
}
