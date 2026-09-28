package com.example.calorietracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.calorietracker.data.FoodEntity
import com.example.calorietracker.data.FoodLogEntity
import com.example.calorietracker.data.buildDailyCalories
import com.example.calorietracker.ui.TrackerViewModel
import java.time.Instant
import java.util.Locale
import kotlin.math.roundToInt

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
    val allLogs by viewModel.allLogs.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var search by rememberSaveable { mutableStateOf("") }
    var showFoodEditor by remember { mutableStateOf(false) }
    var foodToEdit by remember { mutableStateOf<FoodEntity?>(null) }
    var foodToLog by remember { mutableStateOf<FoodEntity?>(null) }
    var logToEdit by remember { mutableStateOf<FoodLogEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calorie Tracker") },
            )
        },
    ) { insets ->
        Column(
            modifier = Modifier.fillMaxSize().padding(insets).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { selectedTab = 0 }) { Text("Overview") }
                TextButton(onClick = { selectedTab = 1 }) { Text("Foods") }
                TextButton(onClick = { selectedTab = 2 }) { Text("Goal") }
                TextButton(onClick = { selectedTab = 3 }) { Text("Insights") }
            }
            when (selectedTab) {
                0 -> TodayScreen(
                    logs = logs,
                    allLogs = allLogs,
                    onDelete = viewModel::deleteLog,
                    onEdit = { logToEdit = it },
                )
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
                2 -> GoalEstimateScreen()
                else -> InsightsScreen(allLogs = allLogs)
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

    val scrollState = androidx.compose.foundation.rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Goal planning", style = MaterialTheme.typography.headlineSmall)
        Text("Enter weights in kilograms and an optional number of days.")
        MacroField("Current weight (kg)", currentWeight) { currentWeight = it }
        MacroField("Goal weight (kg)", goalWeight) { goalWeight = it }
        MacroField("Days to goal", targetDays) { targetDays = it }
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
        Text(
            "This simple estimate does not account for age, height, sex, health conditions, normal activity or changing metabolism. It is intended only for adults; do not use for anyone under 18, during pregnancy/breastfeeding, or with a relevant medical condition. Ask a qualified health professional for personal advice.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun TodayScreen(
    logs: List<FoodLogEntity>,
    allLogs: List<FoodLogEntity>,
    onDelete: (FoodLogEntity) -> Unit,
    onEdit: (FoodLogEntity) -> Unit,
) {
    val calories = logs.sumOf { it.calories }
    val protein = logs.sumOf { it.protein }
    val carbs = logs.sumOf { it.carbs }
    val fat = logs.sumOf { it.fat }
    val chartData = buildDailyCalories(allLogs, days = 7)
    val maxValue = chartData.maxOfOrNull { it.totalCalories }?.coerceAtLeast(1.0) ?: 1.0
    val zone = java.time.ZoneId.systemDefault()
    var selectedDate by rememberSaveable { mutableStateOf<java.time.LocalDate?>(null) }
    val selectedDayEntries = selectedDate?.let { date ->
        allLogs.filter { Instant.ofEpochMilli(it.loggedAt).atZone(zone).toLocalDate().isEqual(date) }
    } ?: emptyList()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Today", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text("${calories.roundToInt()} kcal", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(6.dp))
                    Text("Protein ${protein.oneDecimal()} g · Carbs ${carbs.oneDecimal()} g · Fat ${fat.oneDecimal()} g")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Weekly calorie trend", style = MaterialTheme.typography.titleLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        chartData.forEach { item ->
                            val barHeight = ((item.totalCalories / maxValue) * 150.0).coerceAtLeast(8.0)
                            val isSelected = selectedDate == item.date
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(barHeight.dp)
                                        .background(
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                            shape = RoundedCornerShape(10.dp),
                                        )
                                        .clickable { selectedDate = if (isSelected) null else item.date },
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = item.label.take(3),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedDate != null) {
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${selectedDate!!.dayOfMonth}/${selectedDate!!.monthValue}/${selectedDate!!.year}",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        val selectedCalories = selectedDayEntries.sumOf { it.calories }
                        val selectedProtein = selectedDayEntries.sumOf { it.protein }
                        val selectedCarbs = selectedDayEntries.sumOf { it.carbs }
                        val selectedFat = selectedDayEntries.sumOf { it.fat }
                        Text("${selectedCalories.roundToInt()} kcal total")
                        Text("P ${selectedProtein.oneDecimal()} g · C ${selectedCarbs.oneDecimal()} g · F ${selectedFat.oneDecimal()} g")
                        if (selectedDayEntries.isEmpty()) {
                            Text("No meals logged for this day.")
                        } else {
                            selectedDayEntries.sortedByDescending { it.loggedAt }.forEach { log ->
                                Text("• ${log.foodNameSnapshot} · ${log.meal} · ${log.calories.roundToInt()} kcal")
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Food diary", style = MaterialTheme.typography.titleLarge)
        }

        if (logs.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Nothing logged yet. Open Foods and add your first item.",
                    )
                }
            }
        } else {
            items(logs, key = { it.id }) { log ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
private fun InsightsScreen(allLogs: List<FoodLogEntity>) {
    var selectedPeriod by rememberSaveable { mutableStateOf("Week") }
    val periodSummary = averageMacroSummary(allLogs, selectedPeriod)
    val scrollState = androidx.compose.foundation.rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Macro insights", style = MaterialTheme.typography.headlineSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Week", "Month", "Year", "Lifetime").forEach { period ->
                val selected = selectedPeriod == period
                Button(
                    onClick = { selectedPeriod = period },
                    enabled = true,
                ) {
                    Text(period)
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Average per day", style = MaterialTheme.typography.titleLarge)
                StatRow(label = "Calories", value = "${periodSummary.averageCalories.roundToInt()} kcal")
                StatRow(label = "Protein", value = "${periodSummary.averageProtein.oneDecimal()} g")
                StatRow(label = "Carbs", value = "${periodSummary.averageCarbs.oneDecimal()} g")
                StatRow(label = "Fat", value = "${periodSummary.averageFat.oneDecimal()} g")
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

private data class MacroSummary(
    val averageCalories: Double,
    val averageProtein: Double,
    val averageCarbs: Double,
    val averageFat: Double,
)

private fun averageMacroSummary(logs: List<FoodLogEntity>, selectedPeriod: String): MacroSummary {
    val zone = java.time.ZoneId.systemDefault()
    val now = java.time.Instant.now().atZone(zone).toLocalDate()

    val startDate = when (selectedPeriod) {
        "Week" -> now.minusDays(6)
        "Month" -> now.minusDays(29)
        "Year" -> now.minusDays(364)
        else -> {
            val earliest = logs.minOfOrNull { it.loggedAt } ?: System.currentTimeMillis()
            Instant.ofEpochMilli(earliest).atZone(zone).toLocalDate()
        }
    }

    val filteredLogs = logs.filter { log ->
        val logDate = Instant.ofEpochMilli(log.loggedAt).atZone(zone).toLocalDate()
        logDate.isAfter(startDate.minusDays(1)) || logDate.isEqual(startDate)
    }

    val dailyTotals = mutableMapOf<java.time.LocalDate, Double>()
    val proteinTotals = mutableMapOf<java.time.LocalDate, Double>()
    val carbsTotals = mutableMapOf<java.time.LocalDate, Double>()
    val fatTotals = mutableMapOf<java.time.LocalDate, Double>()

    filteredLogs.forEach { log ->
        val day = Instant.ofEpochMilli(log.loggedAt).atZone(zone).toLocalDate()
        dailyTotals[day] = dailyTotals.getOrDefault(day, 0.0) + log.calories
        proteinTotals[day] = proteinTotals.getOrDefault(day, 0.0) + log.protein
        carbsTotals[day] = carbsTotals.getOrDefault(day, 0.0) + log.carbs
        fatTotals[day] = fatTotals.getOrDefault(day, 0.0) + log.fat
    }

    return MacroSummary(
        averageCalories = if (dailyTotals.isEmpty()) 0.0 else dailyTotals.values.average(),
        averageProtein = if (proteinTotals.isEmpty()) 0.0 else proteinTotals.values.average(),
        averageCarbs = if (carbsTotals.isEmpty()) 0.0 else carbsTotals.values.average(),
        averageFat = if (fatTotals.isEmpty()) 0.0 else fatTotals.values.average(),
    )
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
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Foods stored on this device", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onAddFood) { Text("Add food") }
        }
        Text("Starter nutrition values are estimates. Adjust them for your recipe or food label.")
        OutlinedTextField(
            value = search,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search foods") },
            singleLine = true,
        )
        val matches = foods.filter { it.name.contains(search.trim(), ignoreCase = true) }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(matches, key = { it.id }) { food ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

    val scrollState = androidx.compose.foundation.rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (food == null) "Add a local food" else "Edit food macros") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 420.dp).verticalScroll(scrollState),
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
