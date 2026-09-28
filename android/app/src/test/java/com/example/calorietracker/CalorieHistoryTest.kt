package com.example.calorietracker

import com.example.calorietracker.data.FoodLogEntity
import com.example.calorietracker.data.buildDailyCalories
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class CalorieHistoryTest {
    @Test
    fun `daily calorie totals include yesterday and today`() {
        val now = Instant.parse("2026-09-28T12:00:00Z").toEpochMilli()
        val zone = ZoneId.systemDefault()
        val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
        val yesterday = today.minusDays(1)

        val logs = listOf(
            FoodLogEntity(
                id = 1,
                foodId = 1,
                foodNameSnapshot = "Banana",
                amountGrams = 100.0,
                calories = 89.0,
                protein = 1.1,
                carbs = 22.0,
                fat = 0.3,
                meal = "Breakfast",
                loggedAt = yesterday.atStartOfDay(zone).toInstant().plus(2, ChronoUnit.HOURS).toEpochMilli(),
            ),
            FoodLogEntity(
                id = 2,
                foodId = 2,
                foodNameSnapshot = "Rice",
                amountGrams = 200.0,
                calories = 260.0,
                protein = 4.0,
                carbs = 56.0,
                fat = 0.6,
                meal = "Lunch",
                loggedAt = now - 3_600_000L,
            ),
        )

        val history = buildDailyCalories(logs, now)
        val yesterdayEntry = history.firstOrNull { it.label == "Yesterday" }
        val todayEntry = history.firstOrNull { it.label == "Today" }

        assertTrue(yesterdayEntry != null)
        assertTrue(todayEntry != null)
        assertEquals(89.0, yesterdayEntry!!.totalCalories, 0.01)
        assertEquals(260.0, todayEntry!!.totalCalories, 0.01)
    }
}
