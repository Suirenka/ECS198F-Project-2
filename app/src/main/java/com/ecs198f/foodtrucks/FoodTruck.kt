package com.ecs198f.foodtrucks

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Parcelize
data class FoodTruck(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val priceLevel: Int,
    val location: String,
    val openTime: LocalDateTime,
    val closeTime: LocalDateTime
): Parcelable {
    val formattedTimeInterval: String
        get() = "${openTime.format(timeOnlyFormatter)} - ${closeTime.format(dateTimeFormatter)}"

    companion object {
        private val timeOnlyFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("hh:mm a")

        private val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("hh:mm a, EEE, MMM d")
    }
}
