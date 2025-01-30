package com.example.ayush_swipe.di

import androidx.room.Room
import com.example.ayush_swipe.api.ProductApi
import com.example.ayush_swipe.repository.ProductRepository
import com.example.ayush_swipe.room.ProductDatabase
import com.example.ayush_swipe.viewmodel.ProductViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { Retrofit.Builder()
        .baseUrl("https://app.getswipe.in/api/public/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProductApi::class.java) }

    single { Room.databaseBuilder(get(), ProductDatabase::class.java, "product_db").build() }
    single { get<ProductDatabase>().productDao() }
    single { ProductRepository(get(), get()) }
    viewModel { ProductViewModel(get()) }

}
