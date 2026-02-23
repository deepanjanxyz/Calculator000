package com.example.premiumcalculator.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumcalculator.data.HistoryEntity
import com.example.premiumcalculator.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {

    private val _history = mutableStateOf<List<HistoryEntity>>(emptyList())
    val history: State<List<HistoryEntity>> = _history

    init {
        viewModelScope.launch {
            repository.allHistory.collect { list ->
                _history.value = list
            }
        }
    }

    fun deleteItem(item: HistoryEntity) {
        viewModelScope.launch { repository.delete(item) }
    }

    fun clearAll() {
        viewModelScope.launch { repository.deleteAll() }
    }
}
