package com.example.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<CalculationHistory>> = historyDao.getAllHistory()

    suspend fun addCalculation(expression: String, result: String, isRadian: Boolean) {
        if (expression.isBlank() || result.isBlank()) return
        historyDao.insertHistory(
            CalculationHistory(
                expression = expression,
                result = result,
                isRadian = isRadian
            )
        )
    }

    suspend fun deleteItem(id: Long) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun clearAll() {
        historyDao.clearAllHistory()
    }
}
