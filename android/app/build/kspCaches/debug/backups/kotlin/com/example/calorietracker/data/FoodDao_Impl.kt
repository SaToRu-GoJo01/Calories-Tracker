package com.example.calorietracker.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FoodDao_Impl(
  __db: RoomDatabase,
) : FoodDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFoodEntity: EntityInsertAdapter<FoodEntity>

  private val __insertAdapterOfFoodLogEntity: EntityInsertAdapter<FoodLogEntity>

  private val __deleteAdapterOfFoodLogEntity: EntityDeleteOrUpdateAdapter<FoodLogEntity>

  private val __updateAdapterOfFoodEntity: EntityDeleteOrUpdateAdapter<FoodEntity>

  private val __updateAdapterOfFoodLogEntity: EntityDeleteOrUpdateAdapter<FoodLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFoodEntity = object : EntityInsertAdapter<FoodEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `foods` (`id`,`name`,`category`,`caloriesPer100g`,`proteinPer100g`,`carbsPer100g`,`fatPer100g`,`sourceNote`,`isCustom`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FoodEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.category)
        statement.bindDouble(4, entity.caloriesPer100g)
        statement.bindDouble(5, entity.proteinPer100g)
        statement.bindDouble(6, entity.carbsPer100g)
        statement.bindDouble(7, entity.fatPer100g)
        statement.bindText(8, entity.sourceNote)
        val _tmp: Int = if (entity.isCustom) 1 else 0
        statement.bindLong(9, _tmp.toLong())
      }
    }
    this.__insertAdapterOfFoodLogEntity = object : EntityInsertAdapter<FoodLogEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `food_logs` (`id`,`foodId`,`foodNameSnapshot`,`amountGrams`,`calories`,`protein`,`carbs`,`fat`,`meal`,`loggedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FoodLogEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.foodId)
        statement.bindText(3, entity.foodNameSnapshot)
        statement.bindDouble(4, entity.amountGrams)
        statement.bindDouble(5, entity.calories)
        statement.bindDouble(6, entity.protein)
        statement.bindDouble(7, entity.carbs)
        statement.bindDouble(8, entity.fat)
        statement.bindText(9, entity.meal)
        statement.bindLong(10, entity.loggedAt)
      }
    }
    this.__deleteAdapterOfFoodLogEntity = object : EntityDeleteOrUpdateAdapter<FoodLogEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `food_logs` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FoodLogEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfFoodEntity = object : EntityDeleteOrUpdateAdapter<FoodEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `foods` SET `id` = ?,`name` = ?,`category` = ?,`caloriesPer100g` = ?,`proteinPer100g` = ?,`carbsPer100g` = ?,`fatPer100g` = ?,`sourceNote` = ?,`isCustom` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FoodEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.category)
        statement.bindDouble(4, entity.caloriesPer100g)
        statement.bindDouble(5, entity.proteinPer100g)
        statement.bindDouble(6, entity.carbsPer100g)
        statement.bindDouble(7, entity.fatPer100g)
        statement.bindText(8, entity.sourceNote)
        val _tmp: Int = if (entity.isCustom) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindLong(10, entity.id)
      }
    }
    this.__updateAdapterOfFoodLogEntity = object : EntityDeleteOrUpdateAdapter<FoodLogEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `food_logs` SET `id` = ?,`foodId` = ?,`foodNameSnapshot` = ?,`amountGrams` = ?,`calories` = ?,`protein` = ?,`carbs` = ?,`fat` = ?,`meal` = ?,`loggedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FoodLogEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.foodId)
        statement.bindText(3, entity.foodNameSnapshot)
        statement.bindDouble(4, entity.amountGrams)
        statement.bindDouble(5, entity.calories)
        statement.bindDouble(6, entity.protein)
        statement.bindDouble(7, entity.carbs)
        statement.bindDouble(8, entity.fat)
        statement.bindText(9, entity.meal)
        statement.bindLong(10, entity.loggedAt)
        statement.bindLong(11, entity.id)
      }
    }
  }

  public override suspend fun insertFoods(foods: List<FoodEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFoodEntity.insert(_connection, foods)
  }

  public override suspend fun insertFood(food: FoodEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfFoodEntity.insertAndReturnId(_connection, food)
    _result
  }

  public override suspend fun insertLog(log: FoodLogEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFoodLogEntity.insert(_connection, log)
  }

  public override suspend fun deleteLog(log: FoodLogEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfFoodLogEntity.handle(_connection, log)
  }

  public override suspend fun updateFood(food: FoodEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfFoodEntity.handle(_connection, food)
  }

  public override suspend fun updateLog(log: FoodLogEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfFoodLogEntity.handle(_connection, log)
  }

  public override fun observeFoods(): Flow<List<FoodEntity>> {
    val _sql: String = "SELECT * FROM foods ORDER BY name COLLATE NOCASE"
    return createFlow(__db, false, arrayOf("foods")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfCaloriesPer100g: Int = getColumnIndexOrThrow(_stmt, "caloriesPer100g")
        val _columnIndexOfProteinPer100g: Int = getColumnIndexOrThrow(_stmt, "proteinPer100g")
        val _columnIndexOfCarbsPer100g: Int = getColumnIndexOrThrow(_stmt, "carbsPer100g")
        val _columnIndexOfFatPer100g: Int = getColumnIndexOrThrow(_stmt, "fatPer100g")
        val _columnIndexOfSourceNote: Int = getColumnIndexOrThrow(_stmt, "sourceNote")
        val _columnIndexOfIsCustom: Int = getColumnIndexOrThrow(_stmt, "isCustom")
        val _result: MutableList<FoodEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FoodEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpCaloriesPer100g: Double
          _tmpCaloriesPer100g = _stmt.getDouble(_columnIndexOfCaloriesPer100g)
          val _tmpProteinPer100g: Double
          _tmpProteinPer100g = _stmt.getDouble(_columnIndexOfProteinPer100g)
          val _tmpCarbsPer100g: Double
          _tmpCarbsPer100g = _stmt.getDouble(_columnIndexOfCarbsPer100g)
          val _tmpFatPer100g: Double
          _tmpFatPer100g = _stmt.getDouble(_columnIndexOfFatPer100g)
          val _tmpSourceNote: String
          _tmpSourceNote = _stmt.getText(_columnIndexOfSourceNote)
          val _tmpIsCustom: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCustom).toInt()
          _tmpIsCustom = _tmp != 0
          _item = FoodEntity(_tmpId,_tmpName,_tmpCategory,_tmpCaloriesPer100g,_tmpProteinPer100g,_tmpCarbsPer100g,_tmpFatPer100g,_tmpSourceNote,_tmpIsCustom)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun foodCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM foods"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeLogsForDay(startMillis: Long, endMillis: Long): Flow<List<FoodLogEntity>> {
    val _sql: String = "SELECT * FROM food_logs WHERE loggedAt >= ? AND loggedAt < ? ORDER BY loggedAt DESC"
    return createFlow(__db, false, arrayOf("food_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startMillis)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endMillis)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFoodId: Int = getColumnIndexOrThrow(_stmt, "foodId")
        val _columnIndexOfFoodNameSnapshot: Int = getColumnIndexOrThrow(_stmt, "foodNameSnapshot")
        val _columnIndexOfAmountGrams: Int = getColumnIndexOrThrow(_stmt, "amountGrams")
        val _columnIndexOfCalories: Int = getColumnIndexOrThrow(_stmt, "calories")
        val _columnIndexOfProtein: Int = getColumnIndexOrThrow(_stmt, "protein")
        val _columnIndexOfCarbs: Int = getColumnIndexOrThrow(_stmt, "carbs")
        val _columnIndexOfFat: Int = getColumnIndexOrThrow(_stmt, "fat")
        val _columnIndexOfMeal: Int = getColumnIndexOrThrow(_stmt, "meal")
        val _columnIndexOfLoggedAt: Int = getColumnIndexOrThrow(_stmt, "loggedAt")
        val _result: MutableList<FoodLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FoodLogEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpFoodId: Long
          _tmpFoodId = _stmt.getLong(_columnIndexOfFoodId)
          val _tmpFoodNameSnapshot: String
          _tmpFoodNameSnapshot = _stmt.getText(_columnIndexOfFoodNameSnapshot)
          val _tmpAmountGrams: Double
          _tmpAmountGrams = _stmt.getDouble(_columnIndexOfAmountGrams)
          val _tmpCalories: Double
          _tmpCalories = _stmt.getDouble(_columnIndexOfCalories)
          val _tmpProtein: Double
          _tmpProtein = _stmt.getDouble(_columnIndexOfProtein)
          val _tmpCarbs: Double
          _tmpCarbs = _stmt.getDouble(_columnIndexOfCarbs)
          val _tmpFat: Double
          _tmpFat = _stmt.getDouble(_columnIndexOfFat)
          val _tmpMeal: String
          _tmpMeal = _stmt.getText(_columnIndexOfMeal)
          val _tmpLoggedAt: Long
          _tmpLoggedAt = _stmt.getLong(_columnIndexOfLoggedAt)
          _item = FoodLogEntity(_tmpId,_tmpFoodId,_tmpFoodNameSnapshot,_tmpAmountGrams,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpMeal,_tmpLoggedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllLogs(): Flow<List<FoodLogEntity>> {
    val _sql: String = "SELECT * FROM food_logs ORDER BY loggedAt DESC"
    return createFlow(__db, false, arrayOf("food_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFoodId: Int = getColumnIndexOrThrow(_stmt, "foodId")
        val _columnIndexOfFoodNameSnapshot: Int = getColumnIndexOrThrow(_stmt, "foodNameSnapshot")
        val _columnIndexOfAmountGrams: Int = getColumnIndexOrThrow(_stmt, "amountGrams")
        val _columnIndexOfCalories: Int = getColumnIndexOrThrow(_stmt, "calories")
        val _columnIndexOfProtein: Int = getColumnIndexOrThrow(_stmt, "protein")
        val _columnIndexOfCarbs: Int = getColumnIndexOrThrow(_stmt, "carbs")
        val _columnIndexOfFat: Int = getColumnIndexOrThrow(_stmt, "fat")
        val _columnIndexOfMeal: Int = getColumnIndexOrThrow(_stmt, "meal")
        val _columnIndexOfLoggedAt: Int = getColumnIndexOrThrow(_stmt, "loggedAt")
        val _result: MutableList<FoodLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FoodLogEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpFoodId: Long
          _tmpFoodId = _stmt.getLong(_columnIndexOfFoodId)
          val _tmpFoodNameSnapshot: String
          _tmpFoodNameSnapshot = _stmt.getText(_columnIndexOfFoodNameSnapshot)
          val _tmpAmountGrams: Double
          _tmpAmountGrams = _stmt.getDouble(_columnIndexOfAmountGrams)
          val _tmpCalories: Double
          _tmpCalories = _stmt.getDouble(_columnIndexOfCalories)
          val _tmpProtein: Double
          _tmpProtein = _stmt.getDouble(_columnIndexOfProtein)
          val _tmpCarbs: Double
          _tmpCarbs = _stmt.getDouble(_columnIndexOfCarbs)
          val _tmpFat: Double
          _tmpFat = _stmt.getDouble(_columnIndexOfFat)
          val _tmpMeal: String
          _tmpMeal = _stmt.getText(_columnIndexOfMeal)
          val _tmpLoggedAt: Long
          _tmpLoggedAt = _stmt.getLong(_columnIndexOfLoggedAt)
          _item = FoodLogEntity(_tmpId,_tmpFoodId,_tmpFoodNameSnapshot,_tmpAmountGrams,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpMeal,_tmpLoggedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
