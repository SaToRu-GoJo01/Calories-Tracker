package com.example.calorietracker.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val sourceNote: String,
    val isCustom: Boolean = false,
)

@Entity(tableName = "food_logs")
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodId: Long,
    val foodNameSnapshot: String,
    val amountGrams: Double,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val meal: String,
    val loggedAt: Long,
)

@Dao
interface FoodDao {
    @Query("SELECT * FROM foods ORDER BY name COLLATE NOCASE")
    fun observeFoods(): Flow<List<FoodEntity>>

    @Query("SELECT COUNT(*) FROM foods")
    suspend fun foodCount(): Int

    @Insert
    suspend fun insertFoods(foods: List<FoodEntity>)

    @Insert
    suspend fun insertFood(food: FoodEntity): Long

    @Update
    suspend fun updateFood(food: FoodEntity)

    @Query("SELECT * FROM food_logs WHERE loggedAt >= :startMillis AND loggedAt < :endMillis ORDER BY loggedAt DESC")
    fun observeLogsForDay(startMillis: Long, endMillis: Long): Flow<List<FoodLogEntity>>

    @Insert
    suspend fun insertLog(log: FoodLogEntity)

    @Update
    suspend fun updateLog(log: FoodLogEntity)

    @Delete
    suspend fun deleteLog(log: FoodLogEntity)
}

@Database(entities = [FoodEntity::class, FoodLogEntity::class], version = 1, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var instance: FoodDatabase? = null

        fun get(context: Context): FoodDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                FoodDatabase::class.java,
                "calorie_tracker.db",
            ).build().also { instance = it }
        }
    }
}
