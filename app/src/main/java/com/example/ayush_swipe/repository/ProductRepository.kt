package com.example.ayush_swipe.repository

import com.example.ayush_swipe.api.ProductApi
import com.example.ayush_swipe.model.ProductEntity
import com.example.ayush_swipe.room.ProductDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductRepository(
    private val productApi: ProductApi,
    private val productDao: ProductDao
) {

    // Fetch products from the API and cache them in the local database
    suspend fun fetchProducts(): List<ProductEntity> = withContext(Dispatchers.IO) {
        try {
            // Fetch products from the API
            val products = productApi.getProducts()

            // Cache the products in the local database
            productDao.insertProducts(products)

            // Return the fetched products
            products
        } catch (e: Exception) {
            // Log the error
            println("Error fetching products from API: ${e.message}")

            // Fallback to local database if API call fails
            productDao.getAllProducts()
        }
    }

    // Add a product to the API and cache it in the local database
    suspend fun addProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        try {
            // Prepare data for API
            val name = product.product_name.toRequestBody()
            val type = product.product_type.toRequestBody()
            val price = product.price.toString().toRequestBody()
            val tax = product.tax.toString().toRequestBody()
            val imagePart = product.image?.let { getImagePart(it) }

            // Add product to the API
            productApi.addProduct(name, type, price, tax, imagePart)

            // Cache the product in the local database
            productDao.insertProduct(product)
        } catch (e: Exception) {
            // Log the error
            println("Error adding product: ${e.message}")

            // Fallback to local database if API call fails
            productDao.insertProduct(product)
        }
    }

    // Convert a string to a RequestBody
    private fun String.toRequestBody() = this.toRequestBody("text/plain".toMediaTypeOrNull())

    // Convert an image URI to a MultipartBody.Part
    private fun getImagePart(imageUri: String): MultipartBody.Part {
        val file = File(imageUri)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("files[]", file.name, requestFile)
    }
}