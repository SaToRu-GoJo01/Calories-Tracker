package com.example.calorietracker.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FoodDatabase_Impl : FoodDatabase() {
  private val _foodDao: Lazy<FoodDao> = lazy {
    FoodDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "ae8f9a79babce09ebc069922cae762d7", "615627e1ffbd872d03cf1d1069131093") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `foods` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `caloriesPer100g` REAL NOT NULL, `proteinPer100g` REAL NOT NULL, `carbsPer100g` REAL NOT NULL, `fatPer100g` REAL NOT NULL, `sourceNote` TEXT NOT NULL, `isCustom` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `food_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `foodId` INTEGER NOT NULL, `foodNameSnapshot` TEXT NOT NULL, `amountGrams` REAL NOT NULL, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `meal` TEXT NOT NULL, `loggedAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ae8f9a79babce09ebc069922cae762d7')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `foods`")
        connection.execSQL("DROP TABLE IF EXISTS `food_logs`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsFoods: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFoods.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoods.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoods.put("category", TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoods.put("caloriesPer100g", TableInfo.Column("caloriesPer100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoods.put("proteinPer100g", TableInfo.Column("proteinPer100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoods.put("carbsPer100g", TableInfo.Column("carbsPer100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoods.put("fatPer100g", TableInfo.Column("fatPer100g", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoods.put("sourceNote", TableInfo.Column("sourceNote", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoods.put("isCustom", TableInfo.Column("isCustom", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFoods: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFoods: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFoods: TableInfo = TableInfo("foods", _columnsFoods, _foreignKeysFoods, _indicesFoods)
        val _existingFoods: TableInfo = read(connection, "foods")
        if (!_infoFoods.equals(_existingFoods)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |foods(com.example.calorietracker.data.FoodEntity).
              | Expected:
              |""".trimMargin() + _infoFoods + """
              |
              | Found:
              |""".trimMargin() + _existingFoods)
        }
        val _columnsFoodLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFoodLogs.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("foodId", TableInfo.Column("foodId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("foodNameSnapshot", TableInfo.Column("foodNameSnapshot", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("amountGrams", TableInfo.Column("amountGrams", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("calories", TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("protein", TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("carbs", TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("fat", TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("meal", TableInfo.Column("meal", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodLogs.put("loggedAt", TableInfo.Column("loggedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFoodLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFoodLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFoodLogs: TableInfo = TableInfo("food_logs", _columnsFoodLogs, _foreignKeysFoodLogs, _indicesFoodLogs)
        val _existingFoodLogs: TableInfo = read(connection, "food_logs")
        if (!_infoFoodLogs.equals(_existingFoodLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |food_logs(com.example.calorietracker.data.FoodLogEntity).
              | Expected:
              |""".trimMargin() + _infoFoodLogs + """
              |
              | Found:
              |""".trimMargin() + _existingFoodLogs)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "foods", "food_logs")
  }

  public override fun clearAllTables() {
    super.performClear(false, "foods", "food_logs")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(FoodDao::class, FoodDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun foodDao(): FoodDao = _foodDao.value
}
