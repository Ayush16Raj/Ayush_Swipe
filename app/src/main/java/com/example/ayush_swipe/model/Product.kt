package com.example.ayush_swipe.model

data class ProductEntity(
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