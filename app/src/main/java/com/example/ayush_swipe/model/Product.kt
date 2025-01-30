package com.example.ayush_swipe.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int? = null,
    val image: String?,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double
)

data class ProductResponse(
    val message: String,
    val product_details: ProductEntity,
    val product_id: Int,
    val success: Boolean
)