package org.futo.inputmethod.latin.uix

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue

/**
 * حالة عالمية لحجم وعرض النص في الكيبورد.
 * تُقرأ من KeyboardView عند الرسم.
 */
object FontScaleState {
    /** مقياس حجم النص (0.5 .. 2.0) */
    var textScale by mutableFloatStateOf(1.0f)

    /** مقياس عرض النص (إطالة أفقية) (0.5 .. 2.0) */
    var widthScale by mutableFloatStateOf(1.0f)
}
