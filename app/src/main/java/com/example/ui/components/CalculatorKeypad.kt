package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ScientificCategory(val title: String) {
    TRIG("Trig"),
    POWER("Powers & Roots"),
    FUNC("Log & Functions"),
    MEMORY("Memory")
}

@Composable
fun CalculatorKeypad(
    isScientific: Boolean,
    isDarkTheme: Boolean,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSciCategory by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- Scientific Row / Expandable Panel ---
        AnimatedVisibility(
            visible = isScientific,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category Tabs for Scientific Functions
                ScrollableTabRow(
                    selectedTabIndex = selectedSciCategory,
                    containerColor = Color.Transparent,
                    contentColor = if (isDarkTheme) Color(0xFF00E5FF) else Color(0xFF6200EA),
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        if (selectedSciCategory < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedSciCategory]),
                                color = if (isDarkTheme) Color(0xFF00E5FF) else Color(0xFF6200EA),
                                height = 3.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    ScientificCategory.entries.forEachIndexed { index, cat ->
                        Tab(
                            selected = selectedSciCategory == index,
                            onClick = { selectedSciCategory = index },
                            text = {
                                Text(
                                    text = cat.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedSciCategory == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedSciCategory == index) {
                                        if (isDarkTheme) Color(0xFF00E5FF) else Color(0xFF6200EA)
                                    } else {
                                        if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                                    }
                                )
                            }
                        )
                    }
                }

                // Scientific Grid according to selected tab
                when (selectedSciCategory) {
                    0 -> ScientificTrigGrid(isDarkTheme, onKeyClick)
                    1 -> ScientificPowersGrid(isDarkTheme, onKeyClick)
                    2 -> ScientificFunctionsGrid(isDarkTheme, onKeyClick)
                    3 -> ScientificMemoryGrid(isDarkTheme, onKeyClick)
                }
            }
        }

        // --- Main Numeric Keypad Grid (5 Rows x 4 Columns) ---
        // Row 1: AC, ( ), %, ÷
        KeypadRow {
            CalcButton("AC", isDarkTheme = isDarkTheme, type = KeyType.CLEAR, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("(", isDarkTheme = isDarkTheme, type = KeyType.OPERATOR, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton(")", isDarkTheme = isDarkTheme, type = KeyType.OPERATOR, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("%", isDarkTheme = isDarkTheme, type = KeyType.OPERATOR, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("÷", isDarkTheme = isDarkTheme, type = KeyType.PRIMARY_OPERATOR, modifier = Modifier.weight(1f), onClick = onKeyClick)
        }

        // Row 2: 7, 8, 9, ×
        KeypadRow {
            CalcButton("7", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("8", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("9", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("×", isDarkTheme = isDarkTheme, type = KeyType.PRIMARY_OPERATOR, modifier = Modifier.weight(1f), onClick = onKeyClick)
        }

        // Row 3: 4, 5, 6, -
        KeypadRow {
            CalcButton("4", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("5", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("6", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("-", isDarkTheme = isDarkTheme, type = KeyType.PRIMARY_OPERATOR, modifier = Modifier.weight(1f), onClick = onKeyClick)
        }

        // Row 4: 1, 2, 3, +
        KeypadRow {
            CalcButton("1", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("2", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("3", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("+", isDarkTheme = isDarkTheme, type = KeyType.PRIMARY_OPERATOR, modifier = Modifier.weight(1f), onClick = onKeyClick)
        }

        // Row 5: ±, 0, ., =
        KeypadRow {
            CalcButton("±", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("0", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1.2f), onClick = onKeyClick)
            CalcButton(".", isDarkTheme = isDarkTheme, type = KeyType.NUMBER, modifier = Modifier.weight(1f), onClick = onKeyClick)
            CalcButton("=", isDarkTheme = isDarkTheme, type = KeyType.EQUALS, modifier = Modifier.weight(1.2f), onClick = onKeyClick)
        }
    }
}

@Composable
private fun KeypadRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
private fun ScientificTrigGrid(isDarkTheme: Boolean, onClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CalcButton("sin", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("cos", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("tan", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CalcButton("asin", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("acos", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("atan", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CalcButton("sinh", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("cosh", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("tanh", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        }
    }
}

@Composable
private fun ScientificPowersGrid(isDarkTheme: Boolean, onClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CalcButton("x²", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("x³", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("xʸ", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CalcButton("√", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("³√", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("10ˣ", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CalcButton("eˣ", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("1/x", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("n!", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        }
    }
}

@Composable
private fun ScientificFunctionsGrid(isDarkTheme: Boolean, onClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CalcButton("log", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("ln", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("abs", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CalcButton("mod", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("π", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("e", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CalcButton("floor", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("ceil", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("round", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
            CalcButton("rand", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        }
    }
}

@Composable
private fun ScientificMemoryGrid(isDarkTheme: Boolean, onClick: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        CalcButton("MC", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        CalcButton("MR", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        CalcButton("M+", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
        CalcButton("M-", isDarkTheme, KeyType.SCIENTIFIC, Modifier.weight(1f), onClick)
    }
}

enum class KeyType {
    NUMBER,
    OPERATOR,
    PRIMARY_OPERATOR,
    SCIENTIFIC,
    EQUALS,
    CLEAR
}

@Composable
fun CalcButton(
    text: String,
    isDarkTheme: Boolean,
    type: KeyType,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "btn_press"
    )

    val shape = RoundedCornerShape(20.dp)

    val (bgBrush, textColor, borderBrush) = when (type) {
        KeyType.NUMBER -> {
            if (isDarkTheme) {
                Triple(
                    Brush.verticalGradient(listOf(Color(0x1FFFFFFF), Color(0x0FFFFFFF))),
                    Color.White,
                    Brush.verticalGradient(listOf(Color(0x26FFFFFF), Color(0x0DFFFFFF)))
                )
            } else {
                Triple(
                    Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9))),
                    Color(0xFF0F172A),
                    Brush.verticalGradient(listOf(Color(0x20000000), Color(0x0D000000)))
                )
            }
        }
        KeyType.OPERATOR -> {
            if (isDarkTheme) {
                Triple(
                    Brush.verticalGradient(listOf(Color(0x991E293B), Color(0x661E293B))),
                    Color(0xFFE2E8F0),
                    Brush.verticalGradient(listOf(Color(0x26FFFFFF), Color(0x0DFFFFFF)))
                )
            } else {
                Triple(
                    Brush.verticalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))),
                    Color(0xFF334155),
                    Brush.verticalGradient(listOf(Color(0x20000000), Color(0x0D000000)))
                )
            }
        }
        KeyType.PRIMARY_OPERATOR -> {
            if (isDarkTheme) {
                Triple(
                    Brush.verticalGradient(listOf(Color(0x2606B6D4), Color(0x1A06B6D4))),
                    Color(0xFF22D3EE),
                    Brush.verticalGradient(listOf(Color(0x4006B6D4), Color(0x2006B6D4)))
                )
            } else {
                Triple(
                    Brush.verticalGradient(listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD))),
                    Color(0xFF0284C7),
                    Brush.verticalGradient(listOf(Color(0x400284C7), Color(0x200284C7)))
                )
            }
        }
        KeyType.SCIENTIFIC -> {
            if (isDarkTheme) {
                Triple(
                    Brush.verticalGradient(listOf(Color(0x801E293B), Color(0x401E293B))),
                    Color(0xFF94A3B8),
                    Brush.verticalGradient(listOf(Color(0x1AFFFFFF), Color(0x0DFFFFFF)))
                )
            } else {
                Triple(
                    Brush.verticalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))),
                    Color(0xFF64748B),
                    Brush.verticalGradient(listOf(Color(0x1A000000), Color(0x0D000000)))
                )
            }
        }
        KeyType.EQUALS -> {
            if (isDarkTheme) {
                Triple(
                    Brush.horizontalGradient(listOf(Color(0xFF06B6D4), Color(0xFF0891B2))),
                    Color(0xFF020617),
                    Brush.horizontalGradient(listOf(Color(0xFF22D3EE), Color(0xFF06B6D4)))
                )
            } else {
                Triple(
                    Brush.horizontalGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1))),
                    Color.White,
                    Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
                )
            }
        }
        KeyType.CLEAR -> {
            if (isDarkTheme) {
                Triple(
                    Brush.verticalGradient(listOf(Color(0x33A855F7), Color(0x20A855F7))),
                    Color(0xFFD8B4FE),
                    Brush.verticalGradient(listOf(Color(0x40A855F7), Color(0x20A855F7)))
                )
            } else {
                Triple(
                    Brush.verticalGradient(listOf(Color(0xFFF3E8FF), Color(0xE9D5FF))),
                    Color(0xFF7E22CE),
                    Brush.verticalGradient(listOf(Color(0x407E22CE), Color(0x207E22CE)))
                )
            }
        }
    }

    Box(
        modifier = modifier
            .scale(scale)
            .height(52.dp)
            .clip(shape)
            .background(bgBrush)
            .border(1.dp, borderBrush, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClick(text) }
            )
            .testTag("key_btn_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = if (text.length > 3) 12.sp else if (text.length > 1) 15.sp else 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
