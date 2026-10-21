package org.futo.inputmethod.latin.uix.actions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.futo.inputmethod.latin.R

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf<Double?>(null) }
    var operator by remember { mutableStateOf<String?>(null) }
    var waitingForSecondOperand by remember { mutableStateOf(false) }

    fun handleNumber(num: String) {
        if (waitingForSecondOperand) {
            display = num
            waitingForSecondOperand = false
        } else {
            display = if (display == "0") num else display + num
        }
    }

    fun handleOperator(op: String) {
        val currentValue = display.toDoubleOrNull()
        if (firstOperand != null && operator != null && !waitingForSecondOperand) {
            val result = performCalculation(firstOperand!!, currentValue ?: 0.0, operator!!)
            display = formatResult(result)
            firstOperand = result
        } else {
            firstOperand = currentValue
        }
        operator = op
        waitingForSecondOperand = true
    }

    fun handleEquals() {
        val currentValue = display.toDoubleOrNull()
        if (firstOperand != null && operator != null) {
            val result = performCalculation(firstOperand!!, currentValue ?: 0.0, operator!!)
            display = formatResult(result)
            firstOperand = null
            operator = null
            waitingForSecondOperand = false
        }
    }

    fun handleClear() {
        display = "0"
        firstOperand = null
        operator = null
        waitingForSecondOperand = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // الخلفية (الصورة التي أضفتها)
        Image(
            painter = painterResource(id = R.drawable.calculator_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // المحتوى فوق الخلفية (شفاف قليلاً)
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(0.25f).background(Color(0xCC1E1E1E), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(text = display, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp), textAlign = TextAlign.End, maxLines = 1)
            }

            Spacer(modifier = Modifier.height(8.dp))

            val buttons = listOf(
                listOf("C", "±", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("0", ".", "=")
            )

            for (row in buttons) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (button in row) {
                        val weight = if (button == "0") 2f else 1f
                        Button(
                            onClick = {
                                when (button) {
                                    "C" -> handleClear()
                                    "±" -> display = formatResult(-(display.toDoubleOrNull() ?: 0.0))
                                    "%" -> display = formatResult((display.toDoubleOrNull() ?: 0.0) / 100.0)
                                    "=" -> handleEquals()
                                    "+", "-", "×", "÷" -> handleOperator(button)
                                    else -> handleNumber(button)
                                }
                            },
                            modifier = Modifier.weight(weight).fillMaxHeight().clip(RoundedCornerShape(8.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = if (button in listOf("+", "-", "×", "÷", "=")) Color(0xE6FF9500) else Color(0xE6505050))
                        ) {
                            Text(text = button, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

fun performCalculation(first: Double, second: Double, op: String): Double {
    return when (op) {
        "+" -> first + second
        "-" -> first - second
        "×" -> first * second
        "÷" -> if (second != 0.0) first / second else 0.0
        else -> 0.0
    }
}

fun formatResult(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString() else "%.2f".format(value)
}
