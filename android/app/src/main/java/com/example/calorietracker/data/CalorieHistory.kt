package com.example.calorietracker.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DailyCaloriesSummary(
    val date: LocalDate,
    val label: String,
    val totalCalories: Double,
)

fun buildDailyCalories(logs: List<FoodLogEntity>, nowMillis: Long = System.currentTimeMillis(), days: Int = 30): List<DailyCaloriesSummary> {
    val zone = ZoneId.systemDefault()
    val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
    val startDate = today.minusDays((days - 1).toLong())
    val totalsByDay = mutableMapOf<LocalDate, Double>()

    for (log in logs) {
        val date = Instant.ofEpochMilli(log.loggedAt).atZone(zone).toLocalDate()
        totalsByDay[date] = totalsByDay.getOrDefault(date, 0.0) + log.calories
    }

    return (0 until days).map { offset ->
        val date = startDate.plusDays(offset.toLong())
        val total = totalsByDay.getOrDefault(date, 0.0)
        val label = when {
            date.isEqual(today) -> "Today"
            date.isEqual(today.minusDays(1)) -> "Yesterday"
            else -> date.dayOfWeek.name.take(3)
        }
        DailyCaloriesSummary(date = date, label = label, totalCalories = total)
    }
}
