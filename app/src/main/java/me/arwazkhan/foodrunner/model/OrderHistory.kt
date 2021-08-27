package me.arwazkhan.foodrunner.model

import org.json.JSONArray

data class OrderHistory(
    var orderId: String,
    var restaurantName: String,
    var totalCost: String,
    var orderPlacedAt: String,
    var foodItems: JSONArray
)