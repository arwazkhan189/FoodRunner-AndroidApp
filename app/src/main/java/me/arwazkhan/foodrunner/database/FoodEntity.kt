package me.arwazkhan.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name")
    val foodName: String,

    @ColumnInfo(name = "rating")
    val foodRating: String,

    @ColumnInfo(name = "cost_for_one")
    val foodPrice: String,

    @ColumnInfo(name = "image_url")
    val foodImage: String
)