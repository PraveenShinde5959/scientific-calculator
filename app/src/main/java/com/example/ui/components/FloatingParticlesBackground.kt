package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun BentoGridBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundBrush = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF020617), // Deep Bento Slate
                Color(0xFF0B0F29), // Midnight Indigo Slate
                Color(0xFF020617)  // Deep Bento Slate
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF8FAFC),
                Color(0xFFE2E8F0),
                Color(0xFFF1F5F9)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        if (isDarkTheme) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Top-right cyan ambient radial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x2B06B6D4), // Cyan glow
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.9f, size.height * 0.15f),
                        radius = size.width * 0.6f
                    ),
                    center = Offset(size.width * 0.9f, size.height * 0.15f),
                    radius = size.width * 0.6f
                )

                // Bottom-left purple ambient radial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x26A855F7), // Purple glow
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.1f, size.height * 0.85f),
                        radius = size.width * 0.6f
                    ),
                    center = Offset(size.width * 0.1f, size.height * 0.85f),
                    radius = size.width * 0.6f
                )
            }
        }
    }
}

@Composable
fun FloatingParticlesBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    // Static Bento Grid background without particle animation
    BentoGridBackground(isDarkTheme = isDarkTheme, modifier = modifier)
}
