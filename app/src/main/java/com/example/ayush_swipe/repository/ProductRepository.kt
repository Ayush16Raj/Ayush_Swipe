package com.example.ayush_swipe.repository

import android.content.Context
import android.net.Uri
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

            // Validate the data
            if (products.isNotEmpty()) {
                // Cache the products in the local database
                productDao.insertProducts(products)
            }

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
    suspend fun addProduct(context: Context, product: ProductEntity) = withContext(Dispatchers.IO) {
        try {
            // Prepare data for API
            val name = product.product_name.toRequestBody()
            val type = product.product_type.toRequestBody()
            val price = product.price.toString().toRequestBody()
            val tax = product.tax.toString().toRequestBody()
            val imagePart = product.image?.let { getImagePart(context, it) }

            // Log the request data
            println("Sending request to API with data:")
            println("Product Name: ${product.product_name}")
            println("Price: ${product.price}")
            println("Tax: ${product.tax}")
            println("Product Type: ${product.product_type}")
            println("Image: ${product.image}")

            // Add product to the API
            val response = productApi.addProduct(name, type, price, tax, imagePart)

            // Log the API response
            if (response.isSuccessful) {
                println("API Response: ${response.body()}")
            } else {
                println("API Error: ${response.errorBody()?.string()}")
            }

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
    private fun getImagePart(context: Context, imageUri: String): MultipartBody.Part? {
        return try {
            val file = File(imageUri)
            if (file.exists()) {
                // If the file already exists, use it directly
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("files[]", file.name, requestFile)
            } else {
                // Handle content URIs
                val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))
                val mimeType = context.contentResolver.getType(Uri.parse(imageUri))

                // Determine the file extension based on the MIME type
                val extension = when (mimeType) {
                    "image/jpeg" -> ".jpg"
                    "image/png" -> ".png"
                    else -> null
                }

                if (extension == null) {
                    println("Unsupported MIME type: $mimeType")
                    return null
                }

                // Create a temporary file with the correct extension
                val file = File.createTempFile("temp", extension, context.cacheDir)
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // Prepare the MultipartBody.Part
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("files[]", file.name, requestFile)
            }
        } catch (e: Exception) {
            println("Error creating image part: ${e.message}")
            null
        }
    }
}