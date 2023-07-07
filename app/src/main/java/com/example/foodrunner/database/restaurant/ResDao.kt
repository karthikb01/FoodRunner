package com.example.foodrunner.database.restaurant

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResDao {

    @Insert
    fun insertRes(resEntity: ResEntity)

    @Delete
    fun deleteRes(resEntity: ResEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRes(): List<ResEntity>

    @Query("SELECT * FROM restaurants WHERE  resId = :resId")
    fun getResById(resId: String): ResEntity

}