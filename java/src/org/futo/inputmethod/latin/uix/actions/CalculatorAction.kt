package org.futo.inputmethod.latin.uix.actions

import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionCategory

val CalculatorAction = Action(
    name = "Calculator",
    label = R.string.calculator_action_label,
    icon = R.drawable.calculator_bg,
    category = ActionCategory.ActionKey
) {
    CalculatorScreen()
}
