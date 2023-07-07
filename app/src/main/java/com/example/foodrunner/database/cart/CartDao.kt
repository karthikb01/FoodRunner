package com.example.foodrunner.database.cart

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {
    @Insert
    fun insert(item: CartEntity)

    @Delete
    fun delete(item: CartEntity)

    @Query("SELECT * FROM cart_items")
    fun getAllItems(): List<CartEntity>

    @Query("DELETE FROM cart_items")
    fun deleteAllItems()
}