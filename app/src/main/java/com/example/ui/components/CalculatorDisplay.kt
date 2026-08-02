package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CalculatorUiState

@Composable
fun CalculatorDisplay(
    state: CalculatorUiState,
    onToggleRadian: () -> Unit,
    onToggleScientific: () -> Unit,
    onToggleDarkTheme: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleVibe: () -> Unit,
    onToggleHistory: () -> Unit,
    onCopyResult: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Auto-scroll display to the right whenever expression changes
    LaunchedEffect(state.expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val cardBg = if (state.isDarkTheme) {
        Color(0xCC0F172A) // Deep Bento Glass Card Surface
    } else {
        Color(0xF0FFFFFF) // Bento Light Card Surface
    }

    val borderColor = if (state.isDarkTheme) {
        Color(0x1AFFFFFF) // White 10% subtle border
    } else {
        Color(0x1A000000)
    }

    val textColor = if (state.isDarkTheme) Color.White else Color(0xFF0F172A)
    val previewColor = if (state.isDarkTheme) Color(0xFF22D3EE) else Color(0xFF0284C7)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .border(1.dp, borderColor, RoundedCornerShape(28.dp)),
        color = cardBg,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // --- Top Toolbar Controls ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RAD / DEG Mode Switcher Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (state.isDarkTheme) Color(0x3300E5FF) else Color(0x336200EA))
                            .clickable { onToggleRadian() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("mode_rad_deg_pill")
                    ) {
                        Text(
                            text = if (state.isRadian) "RAD" else "DEG",
                            color = if (state.isDarkTheme) Color(0xFF00E5FF) else Color(0xFF6200EA),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (state.memoryValue != 0.0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFFFD600))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "M",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Action Icons (Sound, Vibe, Theme, Scientific, History)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier.size(36.dp).testTag("sound_toggle")
                    ) {
                        Icon(
                            imageVector = if (state.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Sound Toggle",
                            tint = textColor.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleVibe,
                        modifier = Modifier.size(36.dp).testTag("vibe_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Vibration Toggle",
                            tint = if (state.vibeEnabled) previewColor else textColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleDarkTheme,
                        modifier = Modifier.size(36.dp).testTag("theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (state.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme Toggle",
                            tint = textColor.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleScientific,
                        modifier = Modifier.size(36.dp).testTag("scientific_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Functions,
                            contentDescription = "Scientific Mode Toggle",
                            tint = if (state.isScientific) previewColor else textColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleHistory,
                        modifier = Modifier.size(36.dp).testTag("history_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = previewColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Main Expression Display Row (Auto-scrolling) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (state.expression.isEmpty()) {
                    Text(
                        text = "0",
                        color = textColor.copy(alpha = 0.3f),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.expression,
                            color = textColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif,
                            maxLines = 1,
                            modifier = Modifier.testTag("expression_display")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- Live Result Preview & Error Message ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        color = Color(0xFFFF5252),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth().testTag("error_display")
                    )
                } else if (state.liveResult.isNotEmpty()) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = state.liveResult,
                            color = previewColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth().testTag("live_result_display")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Bottom Display Quick Actions (Copy, Clear, Backspace) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCopyResult() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Result",
                        tint = previewColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Copy",
                        color = previewColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x22FF5252))
                            .clickable { onClear() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("clear_button")
                    ) {
                        Text(
                            text = "AC",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onBackspace,
                        modifier = Modifier.size(32.dp).testTag("backspace_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
