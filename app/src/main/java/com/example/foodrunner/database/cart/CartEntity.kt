package com.example.foodrunner.database.cart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
class CartEntity(
    @ColumnInfo(name = "restaurant_id") val restaurantId: String,
    @PrimaryKey val itemId: String,
    @ColumnInfo(name = "item_name") val itemName: String,
//    @ColumnInfo(name = "item_price")val itemPrice: String,
    @ColumnInfo(name = "item_cost_for_one") val itemCostForOne: String
)