package org.futo.inputmethod.latin.uix.actions

import org.futo.inputmethod.latin.R

val CalculatorAction = Action(
    name = "Calculator",
    label = R.string.calculator_action_label,
    icon = R.drawable.calculator_bg, // <-- استخدام الأيقونة التي أضفتها
    category = ActionCategory.ActionKey
) {
    CalculatorScreen()
}
