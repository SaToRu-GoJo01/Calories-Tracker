package com.example.calorietracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorietracker.data.FoodDatabase
import com.example.calorietracker.data.FoodEntity
import com.example.calorietracker.data.FoodLogEntity
import com.example.calorietracker.data.FoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FoodRepository(FoodDatabase.get(application).foodDao())

    val foods = repository.foods.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val todayLogs = repository.todayLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    fun saveFood(food: FoodEntity) {
        val nutrients = listOf(food.caloriesPer100g, food.proteinPer100g, food.carbsPer100g, food.fatPer100g)
        if (nutrients.any { it < 0 || !it.isFinite() }) return
        viewModelScope.launch { repository.saveFood(food) }
    }

    fun logFood(food: FoodEntity, amountGrams: Double, meal: String) {
        if (amountGrams <= 0 || amountGrams > 100_000.0 || !amountGrams.isFinite()) return
        viewModelScope.launch { repository.logFood(food, amountGrams, meal) }
    }

    fun updateLog(log: FoodLogEntity, amountGrams: Double, meal: String) {
        if (amountGrams <= 0 || amountGrams > 100_000.0 || !amountGrams.isFinite()) return
        viewModelScope.launch { repository.updateLog(log, amountGrams, meal) }
    }

    fun deleteLog(log: FoodLogEntity) {
        viewModelScope.launch { repository.deleteLog(log) }
    }
}
