package com.example.ayush_swipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ayush_swipe.screens.AddProductScreen
import com.example.ayush_swipe.screens.ProductListScreen
import com.example.ayush_swipe.ui.theme.Ayush_SwipeTheme
import com.example.ayush_swipe.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(2000)
            keepSplashScreen = false
            enableEdgeToEdge()
            setContent {
                val navController = rememberNavController()
                val productViewModel: ProductViewModel by viewModel()
                Ayush_SwipeTheme {
                    AppNav(navController = navController, productViewModel = productViewModel)

                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object ProductList : Screen("productList")
    object AddProduct : Screen("addProduct")
}

@Composable
fun AppNav(navController: NavHostController,productViewModel: ProductViewModel) {
    NavHost(navController = navController, startDestination = Screen.ProductList.route) {
        composable(route = Screen.ProductList.route) {
            ProductListScreen(
                navController = navController,
                viewModel = productViewModel
            )
        }
        composable(route = Screen.AddProduct.route) {
            AddProductScreen(
                viewModel = productViewModel,
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}


