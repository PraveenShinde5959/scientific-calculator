package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationHistory
import com.example.data.HistoryRepository
import com.example.util.EvaluationResult
import com.example.util.FeedbackManager
import com.example.util.MathEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val liveResult: String = "",
    val errorMessage: String? = null,
    val isRadian: Boolean = false,
    val isScientific: Boolean = true,
    val isDarkTheme: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibeEnabled: Boolean = true,
    val memoryValue: Double = 0.0,
    val isHistoryOpen: Boolean = false,
    val lastEvaluatedExpr: String? = null
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    private val feedbackManager: FeedbackManager = FeedbackManager(application)

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    val historyList: StateFlow<List<CalculationHistory>>

    init {
        val dao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(dao)
        historyList = repository.allHistory.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    override fun onCleared() {
        super.onCleared()
        feedbackManager.release()
    }

    fun onKeyInput(symbol: String) {
        val state = _uiState.value
        feedbackManager.playClickSound(state.soundEnabled)
        feedbackManager.triggerVibration(state.vibeEnabled)

        when (symbol) {
            "AC" -> clearAll()
            "C" -> clearCurrent()
            "⌫" -> backspace()
            "=" -> calculateResult()
            "±" -> toggleSign()
            "RAD", "DEG" -> toggleRadian()
            "MC" -> clearMemory()
            "MR" -> recallMemory()
            "M+" -> addToMemory()
            "M-" -> subtractFromMemory()
            "x²" -> appendFunction("^(2)")
            "x³" -> appendFunction("^(3)")
            "xʸ" -> appendOperator("^")
            "10ˣ" -> appendFunction("10^(")
            "eˣ" -> appendFunction("e^(")
            "1/x" -> wrapReciprocal()
            "√" -> appendFunction("√(")
            "³√" -> appendFunction("cbrt(")
            "sin" -> appendFunction("sin(")
            "cos" -> appendFunction("cos(")
            "tan" -> appendFunction("tan(")
            "asin" -> appendFunction("asin(")
            "acos" -> appendFunction("acos(")
            "atan" -> appendFunction("atan(")
            "sinh" -> appendFunction("sinh(")
            "cosh" -> appendFunction("cosh(")
            "tanh" -> appendFunction("tanh(")
            "log" -> appendFunction("log(")
            "ln" -> appendFunction("ln(")
            "n!" -> appendFunction("!")
            "abs" -> appendFunction("abs(")
            "mod" -> appendOperator("mod")
            "floor" -> appendFunction("floor(")
            "ceil" -> appendFunction("ceil(")
            "round" -> appendFunction("round(")
            "rand" -> appendText("rand()")
            "π" -> appendText("π")
            "e" -> appendText("e")
            "+", "-", "×", "÷", "%" -> appendOperator(symbol)
            else -> appendText(symbol)
        }
    }

    private fun appendText(str: String) {
        _uiState.update { curr ->
            val newExpr = curr.expression + str
            curr.copy(
                expression = newExpr,
                errorMessage = null
            )
        }
        recalculateLiveResult()
    }

    private fun appendOperator(op: String) {
        _uiState.update { curr ->
            var expr = curr.expression
            if (expr.isNotEmpty()) {
                val lastChar = expr.last()
                if (lastChar in listOf('+', '-', '×', '÷', '%', '^')) {
                    expr = expr.dropLast(1)
                }
            }
            curr.copy(
                expression = expr + op,
                errorMessage = null
            )
        }
        recalculateLiveResult()
    }

    private fun appendFunction(fn: String) {
        _uiState.update { curr ->
            curr.copy(
                expression = curr.expression + fn,
                errorMessage = null
            )
        }
        recalculateLiveResult()
    }

    private fun wrapReciprocal() {
        _uiState.update { curr ->
            if (curr.expression.isBlank()) {
                curr.copy(expression = "1/(")
            } else {
                curr.copy(expression = "1/(${curr.expression})")
            }
        }
        recalculateLiveResult()
    }

    private fun backspace() {
        _uiState.update { curr ->
            if (curr.expression.isNotEmpty()) {
                val newExpr = curr.expression.dropLast(1)
                curr.copy(
                    expression = newExpr,
                    errorMessage = null
                )
            } else {
                curr
            }
        }
        recalculateLiveResult()
    }

    private fun clearCurrent() {
        _uiState.update { it.copy(expression = "", liveResult = "", errorMessage = null) }
    }

    private fun clearAll() {
        _uiState.update { it.copy(expression = "", liveResult = "", errorMessage = null) }
    }

    private fun toggleSign() {
        _uiState.update { curr ->
            val expr = curr.expression
            if (expr.isEmpty()) {
                curr.copy(expression = "-")
            } else if (expr.startsWith("-(")) {
                curr.copy(expression = expr.removePrefix("-(").dropLast(1))
            } else if (expr.startsWith("-")) {
                curr.copy(expression = expr.removePrefix("-"))
            } else {
                curr.copy(expression = "-($expr)")
            }
        }
        recalculateLiveResult()
    }

    private fun toggleRadian() {
        _uiState.update { curr ->
            val newRad = !curr.isRadian
            curr.copy(isRadian = newRad)
        }
        recalculateLiveResult()
    }

    fun toggleScientific() {
        _uiState.update { it.copy(isScientific = !it.isScientific) }
    }

    fun toggleDarkTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun toggleSound() {
        _uiState.update { it.copy(soundEnabled = !it.soundEnabled) }
    }

    fun toggleVibe() {
        _uiState.update { it.copy(vibeEnabled = !it.vibeEnabled) }
    }

    fun toggleHistorySheet(open: Boolean? = null) {
        _uiState.update { curr ->
            curr.copy(isHistoryOpen = open ?: !curr.isHistoryOpen)
        }
    }

    // --- Memory Operations ---
    private fun clearMemory() {
        _uiState.update { it.copy(memoryValue = 0.0) }
        showToast("Memory Cleared")
    }

    private fun recallMemory() {
        val state = _uiState.value
        if (state.memoryValue != 0.0) {
            val formattedMem = MathEvaluator.evaluate("${state.memoryValue}", state.isRadian)
            if (formattedMem is EvaluationResult.Success) {
                appendText(formattedMem.result)
            }
        }
    }

    private fun addToMemory() {
        evaluateCurrentToValue { value ->
            _uiState.update { curr -> curr.copy(memoryValue = curr.memoryValue + value) }
            showToast("Added to Memory")
        }
    }

    private fun subtractFromMemory() {
        evaluateCurrentToValue { value ->
            _uiState.update { curr -> curr.copy(memoryValue = curr.memoryValue - value) }
            showToast("Subtracted from Memory")
        }
    }

    private fun evaluateCurrentToValue(onValue: (Double) -> Unit) {
        val state = _uiState.value
        val expr = state.expression
        if (expr.isBlank()) return
        when (val res = MathEvaluator.evaluate(expr, state.isRadian)) {
            is EvaluationResult.Success -> {
                res.result.toDoubleOrNull()?.let { onValue(it) }
            }
            else -> {}
        }
    }

    // --- Live & Final Calculations ---
    private fun recalculateLiveResult() {
        val state = _uiState.value
        val expr = state.expression
        if (expr.isBlank()) {
            _uiState.update { it.copy(liveResult = "", errorMessage = null) }
            return
        }

        // Auto-close open parentheses for preview
        val openParens = expr.count { it == '(' } - expr.count { it == ')' }
        val evalExpr = if (openParens > 0) expr + ")".repeat(openParens) else expr

        when (val result = MathEvaluator.evaluate(evalExpr, state.isRadian)) {
            is EvaluationResult.Success -> {
                if (result.result != expr) {
                    _uiState.update { it.copy(liveResult = "= ${result.result}", errorMessage = null) }
                } else {
                    _uiState.update { it.copy(liveResult = "", errorMessage = null) }
                }
            }
            is EvaluationResult.Error -> {
                _uiState.update { it.copy(liveResult = "") }
            }
        }
    }

    private fun calculateResult() {
        val state = _uiState.value
        var expr = state.expression.trim()
        if (expr.isBlank()) return

        // Auto-close open parentheses
        val openParens = expr.count { it == '(' } - expr.count { it == ')' }
        if (openParens > 0) {
            expr += ")".repeat(openParens)
        }

        when (val res = MathEvaluator.evaluate(expr, state.isRadian)) {
            is EvaluationResult.Success -> {
                val resultString = res.result
                _uiState.update {
                    it.copy(
                        expression = resultString,
                        liveResult = "",
                        errorMessage = null,
                        lastEvaluatedExpr = "$expr = $resultString"
                    )
                }

                // Save to Room DB history asynchronously
                viewModelScope.launch {
                    repository.addCalculation(expr, resultString, state.isRadian)
                }
            }
            is EvaluationResult.Error -> {
                _uiState.update { it.copy(errorMessage = res.message, liveResult = "") }
            }
        }
    }

    // --- History Actions ---
    fun onHistoryItemClick(history: CalculationHistory) {
        _uiState.update {
            it.copy(
                expression = history.result,
                isHistoryOpen = false,
                errorMessage = null
            )
        }
        recalculateLiveResult()
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    // --- Clipboard ---
    fun copyResultToClipboard(context: Context) {
        val state = _uiState.value
        val textToCopy = if (state.liveResult.isNotEmpty()) {
            state.liveResult.removePrefix("= ")
        } else if (state.expression.isNotEmpty()) {
            state.expression
        } else {
            return
        }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Calculator Result", textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied $textToCopy to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
    }
}
