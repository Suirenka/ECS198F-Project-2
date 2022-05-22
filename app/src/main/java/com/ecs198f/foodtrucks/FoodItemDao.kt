package com.ecs198f.foodtrucks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM FoodItem WHERE truckId=:truckId")
    suspend fun listAllFoodItems(truckId: String): List<FoodItem>

    @Insert
    suspend fun addItem(item: FoodItem)

    @Insert
    suspend fun addItems(item: List<FoodItem>)

    @Delete
    suspend fun removeItem(item: FoodItem)
}