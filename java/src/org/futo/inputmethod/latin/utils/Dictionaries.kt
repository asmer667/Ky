package org.futo.inputmethod.latin.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.Resources.NotFoundException
import android.util.Log
import org.futo.inputmethod.latin.AssetFileAddress
import org.futo.inputmethod.latin.BundleHelper
import org.futo.inputmethod.latin.R
import java.io.File
import java.io.IOException
import java.util.Locale

object Dictionaries {
    enum class DictionaryKind(val candidateNameGenerator: (Locale) -> List<String>) {
        BinaryDictionary({ listOf(
            "main_" + it.toString().lowercase(),
            "main_" + it.language.lowercase())
        }),

        Mozc({
            if(it.language == "ja") listOf("builtin_mozc_data") else emptyList()
        }),

        Any({ locale ->
            DictionaryKind.entries.filter { it != Any }.flatMap { it.candidateNameGenerator(locale) }
        })
    }

    /**
     * Returns the fallback dictionary resource ID for a given locale.
     * Priority:
     *   1. main_<locale>.dict  (e.g. main_ar.dict for Arabic)
     *   2. main_en.dict        (fallback for languages without a built-in dict)
     *   3. main.dict           (legacy empty)
     */
    private fun fallbackResourceIdForLocale(locale: Locale?): Int {
        val lang = locale?.language ?: "en"
        return when (lang) {
            "ar" -> R.raw.main_ar
            "en" -> R.raw.main_en
            "de" -> R.raw.main_de
            "es" -> R.raw.main_es
            "fr" -> R.raw.main_fr
            "it" -> R.raw.main_it
            "pt" -> R.raw.main_pt_br
            "ru" -> R.raw.main_ru
            // For all other languages, fall back to English.
            else -> R.raw.main_en
        }
    }

    fun getDictionaryIfExists(context: Context, locale: Locale?, kind: DictionaryKind): AssetFileAddress? {
        if(locale == null) return null

        return kind.candidateNameGenerator(locale).firstNotNullOfOrNull {
            BundleHelper.obtainSplitAssetFileDescriptor(context, it, locale)
        }
    }

    /**
     * Opens the fallback dictionary for the given locale.
     */
    fun getFallbackDictionary(context: Context, locale: Locale? = null): AssetFileAddress? {
        val resId = fallbackResourceIdForLocale(locale)

        var afd: AssetFileDescriptor? = null
        try {
            if (resId != 0) {
                afd = try {
                    context.resources.openRawResourceFd(resId)
                } catch (e: NotFoundException) {
                    null
                }
                if (afd != null) {
                    val sourceDir = context.getApplicationInfo().sourceDir
                    val packagePath = File(sourceDir)
                    if (packagePath.isFile()) {
                        return AssetFileAddress(sourceDir, afd.startOffset, afd.length)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Dictionaries", "Error reading fallback dict for ${locale?.language}", e)
        } finally {
            if (afd != null) {
                try { afd.close() } catch (_: IOException) {}
            }
        }

        // Last resort: try the legacy main.dict
        return getLegacyFallbackDictionary(context)
    }

    private fun getLegacyFallbackDictionary(context: Context): AssetFileAddress? {
        var afd: AssetFileDescriptor? = null
        try {
            val resId: Int = R.raw.main
            if (resId != 0) {
                afd = try {
                    context.resources.openRawResourceFd(resId)
                } catch (e: NotFoundException) {
                    null
                }
                if (afd != null) {
                    val sourceDir = context.getApplicationInfo().sourceDir
                    val packagePath = File(sourceDir)
                    if (packagePath.isFile()) {
                        return AssetFileAddress(sourceDir, afd.startOffset, afd.length)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Dictionaries", "Error reading legacy main dict", e)
        } finally {
            if (afd != null) {
                try { afd.close() } catch (_: IOException) {}
            }
        }
        return null
    }
}
