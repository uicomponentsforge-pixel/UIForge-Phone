package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForgeCyanPrimary
import com.example.ui.theme.ForgeEmeraldTertiary
import kotlin.math.sqrt

@Composable
fun CalculatorApp(
    onClose: () -> Unit
) {
    var expression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("0") }
    var memoryValue by remember { mutableDoubleStateOf(0.0) }
    var history by remember { mutableStateOf(listOf("125 × 8 = 1,000", "2500 ÷ 5 = 500")) }
    var showHistory by remember { mutableStateOf(false) }

    fun evaluate() {
        if (expression.isBlank()) return
        try {
            val sanitized = expression
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
            val res = evaluateSimpleMath(sanitized)
            val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else String.format("%.4f", res).trimEnd('0').trimEnd('.')
            history = listOf("$expression = $formatted") + history
            resultText = formatted
            expression = formatted
        } catch (_: Exception) {
            resultText = "Error"
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calculator",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showHistory = !showHistory }) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = "History",
                        tint = if (showHistory) ForgeCyanPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (showHistory) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Calculation History", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Clear",
                                color = ForgeCyanPrimary,
                                modifier = Modifier.clickable { history = emptyList() }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(history) { item ->
                                Text(
                                    text = item,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val part = item.substringAfter("= ").trim()
                                            expression = part
                                            resultText = part
                                            showHistory = false
                                        }
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Display Screen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = expression,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resultText,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }

            // Keypad Grid
            val buttons = listOf(
                listOf("C", "√", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "−"),
                listOf("1", "2", "3", "+"),
                listOf("±", "0", ".", "=")
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { label ->
                            CalcButton(
                                label = label,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.15f),
                                onClick = {
                                    when (label) {
                                        "C" -> {
                                            expression = ""
                                            resultText = "0"
                                        }
                                        "=" -> evaluate()
                                        "√" -> {
                                            val cur = expression.toDoubleOrNull() ?: resultText.toDoubleOrNull()
                                            if (cur != null && cur >= 0) {
                                                val res = sqrt(cur)
                                                resultText = res.toString()
                                                expression = res.toString()
                                            }
                                        }
                                        "±" -> {
                                            if (expression.startsWith("-")) {
                                                expression = expression.drop(1)
                                            } else if (expression.isNotEmpty()) {
                                                expression = "-$expression"
                                            }
                                        }
                                        "%" -> {
                                            val cur = expression.toDoubleOrNull() ?: resultText.toDoubleOrNull()
                                            if (cur != null) {
                                                val res = cur / 100.0
                                                resultText = res.toString()
                                                expression = res.toString()
                                            }
                                        }
                                        else -> {
                                            if (resultText != "0" && expression == resultText && !"+−×÷".contains(label)) {
                                                expression = label
                                            } else {
                                                expression += label
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalcButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isOp = label in listOf("÷", "×", "−", "+", "=")
    val isAction = label in listOf("C", "√", "%", "±")

    val bgColor = when {
        label == "=" -> ForgeEmeraldTertiary
        isOp -> ForgeCyanPrimary
        isAction -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val textColor = when {
        isOp -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = onClick,
        modifier = modifier.testTag("calc_btn_$label"),
        shape = RoundedCornerShape(18.dp),
        color = bgColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = if (isOp || isAction) 22.sp else 24.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

private fun evaluateSimpleMath(expr: String): Double {
    // Simple infix parser supporting +, -, *, /
    var text = expr.trim()
    if (text.isEmpty()) return 0.0

    // Handle tokenization
    val tokens = mutableListOf<String>()
    var currentNumber = StringBuilder()

    var i = 0
    while (i < text.length) {
        val c = text[i]
        if (c in "+-*/") {
            if (currentNumber.isNotEmpty()) {
                tokens.add(currentNumber.toString())
                currentNumber = StringBuilder()
            }
            tokens.add(c.toString())
        } else if (c.isDigit() || c == '.') {
            currentNumber.append(c)
        }
        i++
    }
    if (currentNumber.isNotEmpty()) tokens.add(currentNumber.toString())

    if (tokens.isEmpty()) return 0.0

    // Pass 1: Handle * and /
    val pass1 = mutableListOf<String>()
    var idx = 0
    while (idx < tokens.size) {
        val tok = tokens[idx]
        if (tok == "*" || tok == "/") {
            val left = pass1.removeAt(pass1.lastIndex).toDouble()
            val right = tokens[idx + 1].toDouble()
            val res = if (tok == "*") left * right else left / right
            pass1.add(res.toString())
            idx += 2
        } else {
            pass1.add(tok)
            idx++
        }
    }

    // Pass 2: Handle + and -
    var finalResult = pass1[0].toDouble()
    idx = 1
    while (idx < pass1.size) {
        val op = pass1[idx]
        val right = pass1[idx + 1].toDouble()
        if (op == "+") finalResult += right
        else if (op == "-") finalResult -= right
        idx += 2
    }

    return finalResult
}
