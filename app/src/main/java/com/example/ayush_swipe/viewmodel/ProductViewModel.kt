package com.example.ayush_swipe.viewmodel

import android.content.Context
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

    init {
        fetchProducts()
    }

    // Fetch products from the repository
    fun fetchProducts() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                // Fetch products from the repository
                val productList = repository.fetchProducts()
                _products.value = productList
            } catch (e: Exception) {
                // Handle errors
                _errorMessage.value = "Failed to fetch products: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add a new product
    fun addProduct(context: Context, product: ProductEntity) {
        viewModelScope.launch {
            try {
                // Validate product fields
                if (product.product_name.isBlank()) {
                    _errorMessage.value = "Product name cannot be empty"
                    return@launch
                }
                if (product.price <= 0) {
                    _errorMessage.value = "Price must be greater than 0"
                    return@launch
                }
                if (product.tax < 0) {
                    _errorMessage.value = "Tax cannot be negative"
                    return@launch
                }

                // Add product to the repository
                repository.addProduct(context, product)
                // Refresh the product list after adding a new product
                fetchProducts()
            } catch (e: Exception) {
                // Handle errors
                _errorMessage.value = "Failed to add product: ${e.message}"
            }
        }
    }
}
