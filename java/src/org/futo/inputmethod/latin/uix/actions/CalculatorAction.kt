package org.futo.inputmethod.latin.uix.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import org.futo.inputmethod.latin.R

val CalculatorAction = Action(
    name = "Calculator",
    label = R.string.calculator_action_label,
    icon = Icons.Default.Calculate,
    category = ActionCategory.ActionKey
) {
    CalculatorScreen()
}
