package com.example.ayush_swipe.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.ayush_swipe.model.ProductEntity
import com.example.ayush_swipe.viewmodel.ProductViewModel

@Composable
fun ProductListScreen(navController: NavController, viewModel: ProductViewModel) {
    // Observe LiveData from ViewModel
    val products by viewModel.products.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

    // Fetch products when the screen is first launched
    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }

    // UI Layout
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Products") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            // Loading Indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }

            // Error Message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            // Product List
            if (products.isEmpty() && !isLoading) {
                Text(
                    text = "No products found.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(
                        products.filter {
                            it.product_name.contains(searchQuery, ignoreCase = true)
                        }
                    ) { product ->
                        ProductItem(product)
                    }
                }
            }
        }

        // Floating Action Button (FAB) to add a new product
        FloatingActionButton(
            onClick = { navController.navigate("addProduct") },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Product")
        }
    }
}

@Composable
fun ProductItem(product: ProductEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image (if available)
            if (!product.image.isNullOrEmpty()) {
                AsyncImage(
                    model = product.image,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Product Details
            Column {
                Text(
                    text = product.product_name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Price: ₹${product.price}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Tax: ₹${product.tax}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(viewModel: ProductViewModel, onDismiss: () -> Unit) {
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var tax by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("Product") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }



    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            if (validateImage(context, uri)) {
                imageUri = uri
                errorMessage = null
            } else {
                errorMessage = "Please select a JPEG or PNG image with a 1:1 aspect ratio."
            }
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Add Product", style = MaterialTheme.typography.titleLarge)

            TextField(
                value = productName, onValueChange = { productName = it },
                label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = price, onValueChange = { price = it },
                label = { Text("Price") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextField(
                value = tax, onValueChange = { tax = it },
                label = { Text("Tax") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextField(
                value = productType, onValueChange = { productType = it },
                label = { Text("Product Type") }, modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image")
            }

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                )
            }

            errorMessage?.let {
                Text(text = it, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isUploading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        isUploading = true
                        val product = ProductEntity(
                            product_name = productName,
                            price = price.toDoubleOrNull() ?: 0.0,
                            tax = tax.toDoubleOrNull() ?: 0.0,
                            product_type = productType,
                            image = imageUri?.toString()
                        )
                        viewModel.addProduct(product)
                        isUploading = false
                        showDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Success") },
            text = { Text("Product added successfully!") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onDismiss()
                    Toast.makeText(context, "Product Added", Toast.LENGTH_SHORT).show()
                }) {
                    Text("OK")
                }
            }
        )
    }
}


// Function to validate image format and aspect ratio
private fun validateImage(context: Context, uri: Uri): Boolean {
    val inputStream = context.contentResolver.openInputStream(uri)
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeStream(inputStream, null, options)
    inputStream?.close()

    // Check image format
    val mimeType = context.contentResolver.getType(uri)
    if (mimeType != "image/jpeg" && mimeType != "image/png") {
        return false
    }

    // Check aspect ratio (1:1)
    val width = options.outWidth
    val height = options.outHeight
    return width == height
}

