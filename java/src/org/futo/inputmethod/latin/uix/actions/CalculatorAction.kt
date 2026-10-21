package org.futo.inputmethod.latin.uix.actions

import androidx.compose.runtime.Composable
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionCategory
import org.futo.inputmethod.latin.uix.ActionWindow

val CalculatorAction = Action(
    icon = R.drawable.calculator_bg,
    name = R.string.calculator_action_label,
    simplePressImpl = null,
    persistentState = null,
    windowImpl = { manager, _ ->
        ActionWindow(
            manager = manager,
            content = { @Composable { CalculatorScreen() } }
        )
    },
    shownInEditor = true,
    category = ActionCategory.ActionKey
)
