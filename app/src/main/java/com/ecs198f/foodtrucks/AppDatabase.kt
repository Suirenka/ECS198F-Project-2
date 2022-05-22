package com.ecs198f.foodtrucks

import androidx.room.*
import java.time.LocalDateTime

@Database(entities = [FoodTruck::class, FoodItem::class], version = 2,
    autoMigrations = [AutoMigration (from = 1, to = 2)])
@TypeConverters(AppDatabase.LocalDateTimeTypeConverters::class)
abstract class AppDatabase: RoomDatabase(){
    abstract fun foodtruckDao(): FoodTruckDao

    abstract fun fooditemDao(): FoodItemDao

    class LocalDateTimeTypeConverters{
        @TypeConverter
        fun fromString(s: String): LocalDateTime = LocalDateTime.parse(s)
        @TypeConverter
        fun toString(ldt: LocalDateTime): String = ldt.toString()
    }
}