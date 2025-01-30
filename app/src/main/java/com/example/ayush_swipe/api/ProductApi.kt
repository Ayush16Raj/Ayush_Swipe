package com.example.ayush_swipe.api

import com.example.ayush_swipe.model.ProductEntity
import com.example.ayush_swipe.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface ProductApi {
    @GET("public/get")
    suspend fun getProducts(): List<ProductEntity>

    @Multipart
    @POST("public/add")
    suspend fun addProduct(
        @Part("product_name") productName: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ProductResponse>
}
