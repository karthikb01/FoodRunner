package com.example.foodrunner.database.restaurant

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ResEntity::class], version = 1)
abstract class ResDatabase : RoomDatabase() {

    abstract fun resDao(): ResDao

}