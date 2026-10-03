package org.futo.inputmethod.engine

import androidx.compose.runtime.Composable
import java.io.File

// ===== IME Classes =====
class ChineseIME {
    companion object {
        val mozcUserProfileDir: File = File("/dev/null")
        fun debugInfo(): String = "disabled"
        fun isEnabled(): Boolean = false
        fun shutdown() {}
        fun startup() {}
    }
}

class JapaneseIME {
    companion object {
        fun debugInfo(): String = "disabled"
        fun isEnabled(): Boolean = false
        fun shutdown() {}
        fun startup() {}
    }
}

// ===== IME Settings =====
object ChineseIMESettings {
    const val title: String = "Chinese"
    const val navPath: String = "chinese"
    val visibilityCheck: () -> Boolean = { false }
    @Composable fun menu() {}
}

object JapaneseIMESettings {
    const val title: String = "Japanese"
    const val navPath: String = "japanese"
    val visibilityCheck: () -> Boolean = { false }
    @Composable fun menu() {}
}

// ===== Personal Dictionary Helpers =====
@Composable
fun JapaneseWordPopupDialog(word: Any?) {}

fun decodeJapanesePersonalWord(input: String): Pair<String, String> = Pair("", "")

fun localeSupportsFileImport(locale: String): Boolean = false

// ===== Extra Dialog =====
@Composable
fun ConfirmDeleteExtraDictFileDialog() {}
