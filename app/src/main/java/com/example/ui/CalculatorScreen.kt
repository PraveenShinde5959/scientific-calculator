package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.FloatingParticlesBackground
import com.example.ui.components.HistorySheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()

    Box(modifier = modifier.fillMaxSize()) {
        // Animated Floating Glowing Particles Background
        FloatingParticlesBackground(isDarkTheme = uiState.isDarkTheme)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bento Grid Title Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "PRO VERSION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = if (uiState.isDarkTheme) Color(0xFF22D3EE) else Color(0xFF0284C7)
                )
                Text(
                    text = "NeoCalc Pro",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isDarkTheme) Color.White else Color(0xFF0F172A)
                )
            }

            // Glassmorphic Display Component
            CalculatorDisplay(
                state = uiState,
                onToggleRadian = { viewModel.onKeyInput("RAD") },
                onToggleScientific = viewModel::toggleScientific,
                onToggleDarkTheme = viewModel::toggleDarkTheme,
                onToggleSound = viewModel::toggleSound,
                onToggleVibe = viewModel::toggleVibe,
                onToggleHistory = viewModel::toggleHistorySheet,
                onCopyResult = { viewModel.copyResultToClipboard(context) },
                onClear = { viewModel.onKeyInput("AC") },
                onBackspace = { viewModel.onKeyInput("⌫") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Keypad Grid Component
            CalculatorKeypad(
                isScientific = uiState.isScientific,
                isDarkTheme = uiState.isDarkTheme,
                onKeyClick = viewModel::onKeyInput
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Notice
            Text(
                text = "Scientific Calculator © 2026",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (uiState.isDarkTheme) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // History Modal Bottom Sheet
        if (uiState.isHistoryOpen) {
            HistorySheet(
                historyList = historyList,
                isDarkTheme = uiState.isDarkTheme,
                sheetState = sheetState,
                onDismiss = { viewModel.toggleHistorySheet(false) },
                onSelectHistory = viewModel::onHistoryItemClick,
                onDeleteHistory = viewModel::deleteHistoryItem,
                onClearAllHistory = viewModel::clearAllHistory
            )
        }
    }
}
