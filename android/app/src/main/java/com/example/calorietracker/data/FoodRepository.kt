package com.example.calorietracker.data

import java.util.Calendar
import kotlinx.coroutines.flow.Flow

class FoodRepository(private val dao: FoodDao) {
    val foods: Flow<List<FoodEntity>> = dao.observeFoods()

    fun todayLogs(): Flow<List<FoodLogEntity>> {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = start.clone() as Calendar
        end.add(Calendar.DAY_OF_YEAR, 1)
        return dao.observeLogsForDay(start.timeInMillis, end.timeInMillis)
    }

    fun allLogs(): Flow<List<FoodLogEntity>> = dao.observeAllLogs()

    suspend fun seedIfEmpty() {
        if (dao.foodCount() == 0) dao.insertFoods(StarterFoods.all)
    }

    suspend fun saveFood(food: FoodEntity) {
        if (food.id == 0L) dao.insertFood(food) else dao.updateFood(food)
    }

    suspend fun logFood(food: FoodEntity, amountGrams: Double, meal: String) {
        require(amountGrams > 0.0 && amountGrams <= 100_000.0 && amountGrams.isFinite())
        val factor = amountGrams / 100.0
        dao.insertLog(
            FoodLogEntity(
                foodId = food.id,
                foodNameSnapshot = food.name,
                amountGrams = amountGrams,
                calories = food.caloriesPer100g * factor,
                protein = food.proteinPer100g * factor,
                carbs = food.carbsPer100g * factor,
                fat = food.fatPer100g * factor,
                meal = meal,
                loggedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun updateLog(log: FoodLogEntity, amountGrams: Double, meal: String) {
        require(amountGrams > 0.0 && amountGrams <= 100_000.0 && amountGrams.isFinite())
        val factor = amountGrams / log.amountGrams
        dao.updateLog(
            log.copy(
                amountGrams = amountGrams,
                calories = log.calories * factor,
                protein = log.protein * factor,
                carbs = log.carbs * factor,
                fat = log.fat * factor,
                meal = meal,
            ),
        )
    }

    suspend fun deleteLog(log: FoodLogEntity) = dao.deleteLog(log)
}
