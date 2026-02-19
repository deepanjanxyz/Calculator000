package com.example.premiumcalculator.repository

import com.example.premiumcalculator.data.HistoryDao
import com.example.premiumcalculator.data.HistoryEntity
import javax.inject.Inject

class HistoryRepository @Inject constructor(private val dao: HistoryDao) {

    suspend fun insert(expression: String, result: String) {
        dao.insert(HistoryEntity(expression = expression, result = result))
    }

    suspend fun getAll(): List<HistoryEntity> {
        return dao.getAll()
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
