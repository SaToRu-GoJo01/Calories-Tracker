package com.example.calorietracker.data

/** Generic starter estimates per 100 g; cooked dish nutrition varies by recipe. */
object StarterFoods {
    private const val NOTE = "Starter estimate; recipe/brand varies. Edit to match your food."

    val all = listOf(
        food("Rice, white, cooked", "Staples", 130.0, 2.7, 28.2, 0.3),
        food("Rice, brown, cooked", "Staples", 123.0, 2.7, 25.6, 1.0),
        food("Roti / chapati, plain", "Breads", 260.0, 8.5, 49.0, 3.5),
        food("Paratha, plain", "Breads", 326.0, 7.5, 45.0, 13.0),
        food("Poha, prepared", "Breakfast", 180.0, 3.5, 30.0, 5.5),
        food("Upma, prepared", "Breakfast", 150.0, 4.0, 23.0, 4.5),
        food("Idli", "Breakfast", 58.0, 2.0, 12.0, 0.4),
        food("Dosa, plain", "Breakfast", 168.0, 4.0, 26.0, 5.0),
        food("Dosa, masala", "Breakfast", 190.0, 4.0, 28.0, 7.0),
        food("Besan chilla", "Breakfast", 190.0, 10.0, 21.0, 8.0),
        food("Dal, cooked", "Pulses", 116.0, 7.0, 20.0, 2.0),
        food("Dal tadka", "Pulses", 140.0, 7.0, 17.0, 5.0),
        food("Sambar", "Pulses", 70.0, 3.0, 10.0, 2.0),
        food("Rajma curry", "Pulses", 127.0, 8.7, 22.8, 0.5),
        food("Chana masala", "Pulses", 164.0, 8.9, 27.4, 2.6),
        food("Paneer", "Dairy", 265.0, 18.3, 1.2, 20.8),
        food("Palak paneer", "Meals", 180.0, 8.0, 8.0, 13.0),
        food("Aloo gobi", "Meals", 110.0, 3.0, 15.0, 4.0),
        food("Mixed vegetable sabzi", "Meals", 95.0, 3.0, 12.0, 4.0),
        food("Vegetable pulao", "Meals", 150.0, 3.5, 25.0, 4.0),
        food("Vegetable biryani", "Meals", 160.0, 4.0, 25.0, 5.0),
        food("Chicken biryani", "Meals", 180.0, 9.0, 22.0, 6.0),
        food("Chicken curry", "Meals", 150.0, 12.0, 5.0, 9.0),
        food("Tandoori chicken", "Meals", 165.0, 27.0, 3.0, 5.0),
        food("Egg, boiled", "Protein", 155.0, 13.0, 1.1, 11.0),
        food("Curd / dahi, plain", "Dairy", 61.0, 3.5, 4.7, 3.3),
        food("Milk, whole", "Dairy", 61.0, 3.2, 4.8, 3.3),
        food("Banana", "Fruit", 89.0, 1.1, 22.8, 0.3),
        food("Mango", "Fruit", 60.0, 0.8, 15.0, 0.4),
        food("Chai with milk and sugar", "Drinks", 50.0, 1.5, 8.0, 1.5),
    )

    private fun food(
        name: String,
        category: String,
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double,
    ) = FoodEntity(
        name = name,
        category = category,
        caloriesPer100g = calories,
        proteinPer100g = protein,
        carbsPer100g = carbs,
        fatPer100g = fat,
        sourceNote = NOTE,
    )
}
