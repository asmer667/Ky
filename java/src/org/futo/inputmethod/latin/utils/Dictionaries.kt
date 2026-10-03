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
     * All built-in dictionary resource IDs extracted from the official FUTO Keyboard APK.
     * When a language-specific dictionary isn't found, we try each of these in order.
     * The first one that can be opened by BinaryDictionary will be used.
     */
    private val fallbackDictionaryResources = listOf(
        R.raw.dict_tw,
        R.raw.dict_w4,
        R.raw.dict_kk,
        R.raw.dict_pm,
        R.raw.dict_4s,
        R.raw.dict_3u,
        R.raw.dict_yz,
    )

    fun getDictionaryIfExists(context: Context, locale: Locale?, kind: DictionaryKind): AssetFileAddress? {
        if(locale == null) return null

        return kind.candidateNameGenerator(locale).firstNotNullOfOrNull {
            BundleHelper.obtainSplitAssetFileDescriptor(context, it, locale)
        }
    }

    /**
     * Tries to open each built-in dictionary in [fallbackDictionaryResources] in order.
     * Returns the first one that could be opened successfully.
     */
    fun getAnyFallbackDictionary(context: Context): AssetFileAddress? {
        for (resId in fallbackDictionaryResources) {
            try {
                val afd = context.resources.openRawResourceFd(resId) ?: continue
                try {
                    val sourceDir = context.getApplicationInfo().sourceDir
                    val packagePath = File(sourceDir)
                    if (!packagePath.isFile()) continue
                    return AssetFileAddress(sourceDir, afd.startOffset, afd.length)
                } finally {
                    try { afd.close() } catch (_: IOException) {}
                }
            } catch (e: NotFoundException) {
                continue
            } catch (e: Exception) {
                Log.w("Dictionaries", "Failed to open dict resource $resId", e)
                continue
            }
        }
        return null
    }

    fun getFallbackDictionary(context: Context): AssetFileAddress? {
        // First, try the raw main dictionary (legacy behavior)
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
            Log.e("Dictionaries", "Error reading main dict", e)
        } finally {
            if (afd != null) {
                try { afd.close() } catch (_: IOException) {}
            }
        }

        // Fall back to trying each built-in dictionary in turn
        return getAnyFallbackDictionary(context)
    }
}
