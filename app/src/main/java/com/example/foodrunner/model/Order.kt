package com.example.foodrunner.model

data class Order(
    val orderId: String,
    val resName: String,
    val date: String,
    val totalCost: String,
    val foodItems: List<Item>
)