package com.example.ayush_swipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ayush_swipe.model.ProductEntity
import com.example.ayush_swipe.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    // LiveData to hold the list of products
    private val _products = MutableLiveData<List<ProductEntity>>()
    val products: LiveData<List<ProductEntity>> get() = _products

    // LiveData to handle loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData to handle errors
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Fetch products from the repository
    fun fetchProducts() {
        _isLoading.value = true // Show loading state
        viewModelScope.launch {
            try {
                // Fetch products from the repository
                val productList = repository.fetchProducts()
                _products.value = productList // Update LiveData with fetched products
            } catch (e: Exception) {
                // Handle errors
                _errorMessage.value = "Failed to fetch products: ${e.message}"
            } finally {
                _isLoading.value = false // Hide loading state
            }
        }
    }

    // Add a new product
    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                // Add product to the repository
                repository.addProduct(product)
                // Refresh the product list after adding a new product
                fetchProducts()
            } catch (e: Exception) {
                // Handle errors
                _errorMessage.value = "Failed to add product: ${e.message}"
            }
        }
    }
}