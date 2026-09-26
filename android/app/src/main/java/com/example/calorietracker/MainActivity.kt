package com.example.calorietracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.example.calorietracker.data.FoodEntity
import com.example.calorietracker.data.FoodLogEntity
import com.example.calorietracker.ui.TrackerViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val trackerViewModel: TrackerViewModel by lazy {
        ViewModelProvider(this)[TrackerViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TrackerApp(trackerViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackerApp(viewModel: TrackerViewModel) {
    val foods by viewModel.foods.collectAsState()
    val logs by viewModel.todayLogs.collectAsState()
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var search by rememberSaveable { mutableStateOf("") }
    var showFoodEditor by remember { mutableStateOf(false) }
    var foodToEdit by remember { mutableStateOf<FoodEntity?>(null) }
    var foodToLog by remember { mutableStateOf<FoodEntity?>(null) }
    var logToEdit by remember { mutableStateOf<FoodLogEntity?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Calorie Tracker · Offline") }) }) { insets ->
        Column(
            modifier = Modifier.fillMaxSize().padding(insets).padding(horizontal = 16.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { selectedTab = 0 }) { Text("Today") }
                TextButton(onClick = { selectedTab = 1 }) { Text("Foods") }
                TextButton(onClick = { selectedTab = 2 }) { Text("Goal") }
            }
            Spacer(Modifier.height(12.dp))
            when (selectedTab) {
                0 -> TodayScreen(logs = logs, onDelete = viewModel::deleteLog, onEdit = { logToEdit = it })
                1 -> FoodListScreen(
                    foods = foods,
                    search = search,
                    onSearchChange = { search = it },
                    onAddFood = {
                        foodToEdit = null
                        showFoodEditor = true
                    },
                    onEdit = {
                        foodToEdit = it
                        showFoodEditor = true
                    },
                    onLog = { foodToLog = it },
                )
                else -> GoalEstimateScreen()
            }
        }
    }

    if (showFoodEditor) {
        FoodEditorDialog(
            food = foodToEdit,
            onDismiss = { showFoodEditor = false },
            onSave = { viewModel.saveFood(it); showFoodEditor = false },
        )
    }
    foodToLog?.let { food ->
        LogFoodDialog(
            food = food,
            onDismiss = { foodToLog = null },
            onSave = { grams, meal -> viewModel.logFood(food, grams, meal); foodToLog = null },
        )
    }
    logToEdit?.let { log ->
        EditFoodLogDialog(
            log = log,
            onDismiss = { logToEdit = null },
            onSave = { grams, meal -> viewModel.updateLog(log, grams, meal); logToEdit = null },
        )
    }
}

@Composable
private fun GoalEstimateScreen() {
    var currentWeight by rememberSaveable { mutableStateOf("") }
    var goalWeight by rememberSaveable { mutableStateOf("") }
    var targetDays by rememberSaveable { mutableStateOf("90") }
    val current = currentWeight.toDoubleOrNull()
    val goal = goalWeight.toDoubleOrNull()
    val days = targetDays.toIntOrNull()

    Text("Goal planning", style = MaterialTheme.typography.headlineSmall)
    Text("Enter weights in kilograms and an optional number of days.")
    Spacer(Modifier.height(8.dp))
    MacroField("Current weight (kg)", currentWeight) { currentWeight = it }
    MacroField("Goal weight (kg)", goalWeight) { goalWeight = it }
    MacroField("Days to goal", targetDays) { targetDays = it }
    Spacer(Modifier.height(12.dp))
    when {
        current == null || goal == null || days == null || !current.isFinite() || !goal.isFinite() || current <= goal || goal < 0 || days <= 0 ->
            Text("Enter a current weight greater than your goal and a positive number of days.")
        (current - goal) / days * 7.0 > 0.9 ->
            Text("This target implies more than about 0.9 kg per week. No energy-gap estimate is shown; consider a slower goal and seek qualified health advice.", color = MaterialTheme.colorScheme.error)
        else -> {
            val approximateDailyGap = ((current - goal) * 7_700.0 / days).roundToInt()
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Rough energy-gap arithmetic", style = MaterialTheme.typography.titleLarge)
                    Text("About $approximateDailyGap kcal/day")
                    Text("This is a static planning estimate, not a forecast or a number of exercise calories you should burn. Any energy gap may come from food intake and activity together.")
                }
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    Text(
        "This simple estimate does not account for age, height, sex, health conditions, normal activity or changing metabolism. It is intended only for adults; do not use for anyone under 18, during pregnancy/breastfeeding, or with a relevant medical condition. Ask a qualified health professional for personal advice.",
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
private fun TodayScreen(
    logs: List<FoodLogEntity>,
    onDelete: (FoodLogEntity) -> Unit,
    onEdit: (FoodLogEntity) -> Unit,
) {
    val calories = logs.sumOf { it.calories }
    val protein = logs.sumOf { it.protein }
    val carbs = logs.sumOf { it.carbs }
    val fat = logs.sumOf { it.fat }

    Text("Today's intake", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(8.dp))
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("${calories.roundToInt()} kcal", style = MaterialTheme.typography.headlineMedium)
            Text("Protein ${protein.oneDecimal()} g  ·  Carbs ${carbs.oneDecimal()} g  ·  Fat ${fat.oneDecimal()} g")
        }
    }
    Spacer(Modifier.height(16.dp))
    Text("Food diary", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(6.dp))
    if (logs.isEmpty()) {
        Text("Nothing logged yet. Open Indian foods to add your first item.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(logs, key = { it.id }) { log ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(log.foodNameSnapshot, style = MaterialTheme.typography.titleMedium)
                            Text("${log.amountGrams.oneDecimal()} g · ${log.meal} · ${log.calories.roundToInt()} kcal")
                            Text("P ${log.protein.oneDecimal()} g · C ${log.carbs.oneDecimal()} g · F ${log.fat.oneDecimal()} g")
                        }
                        Column {
                            TextButton(onClick = { onEdit(log) }) { Text("Edit") }
                            TextButton(onClick = { onDelete(log) }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodListScreen(
    foods: List<FoodEntity>,
    search: String,
    onSearchChange: (String) -> Unit,
    onAddFood: () -> Unit,
    onEdit: (FoodEntity) -> Unit,
    onLog: (FoodEntity) -> Unit,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Foods stored on this device", style = MaterialTheme.typography.titleLarge)
        Button(onClick = onAddFood) { Text("Add food") }
    }
    Text("Starter nutrition values are estimates. Adjust them for your recipe or food label.")
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = search,
        onValueChange = onSearchChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Search foods") },
        singleLine = true,
    )
    Spacer(Modifier.height(8.dp))
    val matches = foods.filter { it.name.contains(search.trim(), ignoreCase = true) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(matches, key = { it.id }) { food ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(food.name, style = MaterialTheme.typography.titleMedium)
                    Text("Per 100 g: ${food.caloriesPer100g.roundToInt()} kcal · P ${food.proteinPer100g.oneDecimal()} g · C ${food.carbsPer100g.oneDecimal()} g · F ${food.fatPer100g.oneDecimal()} g")
                    Text(food.sourceNote, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onLog(food) }) { Text("Log amount") }
                        OutlinedButton(onClick = { onEdit(food) }) { Text("Edit macros") }
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodEditorDialog(
    food: FoodEntity?,
    onDismiss: () -> Unit,
    onSave: (FoodEntity) -> Unit,
) {
    var name by remember(food?.id) { mutableStateOf(food?.name.orEmpty()) }
    var category by remember(food?.id) { mutableStateOf(food?.category ?: "Custom") }
    var calories by remember(food?.id) { mutableStateOf(food?.caloriesPer100g?.toString().orEmpty()) }
    var protein by remember(food?.id) { mutableStateOf(food?.proteinPer100g?.toString().orEmpty()) }
    var carbs by remember(food?.id) { mutableStateOf(food?.carbsPer100g?.toString().orEmpty()) }
    var fat by remember(food?.id) { mutableStateOf(food?.fatPer100g?.toString().orEmpty()) }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (food == null) "Add a local food" else "Edit food macros") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                OutlinedTextField(name, { name = it }, label = { Text("Food name") }, singleLine = true)
                OutlinedTextField(category, { category = it }, label = { Text("Category") }, singleLine = true)
                MacroField("Calories per 100 g (kcal)", calories) { calories = it }
                MacroField("Protein per 100 g (g)", protein) { protein = it }
                MacroField("Carbohydrates per 100 g (g)", carbs) { carbs = it }
                MacroField("Fat per 100 g (g)", fat) { fat = it }
                if (error) Text("Enter a name, calories from 0–1,000, and macros from 0–100 g per 100 g.", color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                val values = listOf(calories, protein, carbs, fat).map { it.toDoubleOrNull() }
                val validValues = values.all { value -> value != null && value.isFinite() && value >= 0.0 }
                val caloriesInRange = values[0]?.let { it <= 1_000.0 } == true
                val macrosInRange = values.drop(1).all { it?.let { value -> value <= 100.0 } == true }
                if (name.isBlank() || !validValues || !caloriesInRange || !macrosInRange) {
                    error = true
                } else {
                    val parsed = values.map { requireNotNull(it) }
                    onSave(
                        FoodEntity(
                            id = food?.id ?: 0,
                            name = name.trim(),
                            category = category.trim().ifBlank { "Custom" },
                            caloriesPer100g = parsed[0],
                            proteinPer100g = parsed[1],
                            carbsPer100g = parsed[2],
                            fatPer100g = parsed[3],
                            sourceNote = if (food == null) "User entered; verify against your recipe or label." else food.sourceNote,
                            isCustom = food?.isCustom ?: true,
                        ),
                    )
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun MacroField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
    )
}

@Composable
private fun LogFoodDialog(
    food: FoodEntity,
    onDismiss: () -> Unit,
    onSave: (Double, String) -> Unit,
) {
    var grams by remember(food.id) { mutableStateOf("100") }
    var meal by remember(food.id) { mutableStateOf("Meal") }
    var error by remember { mutableStateOf(false) }
    val amount = grams.toDoubleOrNull()?.takeIf { it > 0.0 && it.isFinite() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log ${food.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MacroField("Amount in grams", grams) { grams = it }
                OutlinedTextField(meal, { meal = it }, label = { Text("Meal (e.g. breakfast)") }, singleLine = true)
                amount?.let {
                    val factor = it / 100.0
                    Text("${(food.caloriesPer100g * factor).roundToInt()} kcal · P ${(food.proteinPer100g * factor).oneDecimal()} g · C ${(food.carbsPer100g * factor).oneDecimal()} g · F ${(food.fatPer100g * factor).oneDecimal()} g")
                }
                if (error) Text("Enter an amount greater than zero and no more than 100,000 g.", color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                if (amount == null || amount > 100_000.0) error = true else onSave(amount, meal.trim().ifBlank { "Meal" })
            }) { Text("Add to today") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun EditFoodLogDialog(
    log: FoodLogEntity,
    onDismiss: () -> Unit,
    onSave: (Double, String) -> Unit,
) {
    var grams by remember(log.id) { mutableStateOf(log.amountGrams.toString()) }
    var meal by remember(log.id) { mutableStateOf(log.meal) }
    var error by remember { mutableStateOf(false) }
    val amount = grams.toDoubleOrNull()?.takeIf { it > 0.0 && it.isFinite() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit ${log.foodNameSnapshot}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MacroField("Amount in grams", grams) { grams = it }
                OutlinedTextField(meal, { meal = it }, label = { Text("Meal") }, singleLine = true)
                amount?.let {
                    val factor = it / log.amountGrams
                    Text("${(log.calories * factor).roundToInt()} kcal · P ${(log.protein * factor).oneDecimal()} g · C ${(log.carbs * factor).oneDecimal()} g · F ${(log.fat * factor).oneDecimal()} g")
                }
                if (error) Text("Enter an amount greater than zero and no more than 100,000 g.", color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                if (amount == null || amount > 100_000.0) error = true
                else onSave(amount, meal.trim().ifBlank { "Meal" })
            }) { Text("Save changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

private fun Double.oneDecimal(): String = String.format(Locale.getDefault(), "%.1f", this)
