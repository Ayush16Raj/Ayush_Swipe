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
    suspend fun fetchProducts(): List<ProductEntity> = withContext(Dispatchers.IO) {
        try {
            val products = productApi.getProducts()
            productDao.insertProducts(products)
            products
        } catch (e: Exception) {
            productDao.getAllProducts()
        } as List<ProductEntity>
    }

    suspend fun addProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        try {
            // Prepare data for API
            val name = product.product_name.toRequestBody()
            val type = product.product_type.toRequestBody()
            val price = product.price.toString().toRequestBody()
            val tax = product.tax.toString().toRequestBody()
            val imagePart = product.image?.let { getImagePart(it) }

            productApi.addProduct(name, type, price, tax, imagePart)
        } catch (e: Exception) {
            productDao.insertProduct(product)
        }
    }

    private fun String.toRequestBody() = this.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun getImagePart(imageUri: String): MultipartBody.Part {
        val file = File(imageUri)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }
}