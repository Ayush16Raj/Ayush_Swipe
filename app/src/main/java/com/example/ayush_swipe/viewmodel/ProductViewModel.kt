package com.example.ayush_swipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ayush_swipe.model.ProductEntity
import com.example.ayush_swipe.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _products = MutableLiveData<List<ProductEntity>>()
    val products: LiveData<List<ProductEntity>> = _products

    fun fetchProducts() {
        viewModelScope.launch {
            _products.value = repository.fetchProducts()
        }
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.addProduct(product)
        }
    }
}
