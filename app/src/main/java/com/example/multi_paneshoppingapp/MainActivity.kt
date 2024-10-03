package com.example.multi_paneshoppingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview

data class Product(
    val name: String,
    val price: String,
    val description: String
)

class ShoppingViewModel : ViewModel() {
    var selectedProduct by mutableStateOf<Product?>(null)
}

val products = listOf(
    Product("iPhone 16 Pro", "$999", "Powered by the A18 Pro chip and built for Apple Intelligence, the Pro lineup introduces larger display sizes, Camera Control, innovative pro camera features, and a huge leap in battery life."),
    Product("MacBook Pro 14-inch (M2 Pro, 2023)", "$1,999", "The 14-inch MacBook Pro with M2 Pro chip offers professional-level performance, a stunning Liquid Retina XDR display, advanced thermal architecture, and all-day battery life in a portable form factor."),
    Product("AirPods Pro (2nd generation)", "$249", "AirPods Pro feature Active Noise Cancellation, Adaptive Transparency, personalized Spatial Audio, and improved battery life for an immersive audio experience."),
    Product("HomePod mini", "$99", "HomePod mini offers room-filling sound in a compact design, intelligent assistant capabilities with Siri, and seamless integration with your Apple devices and smart home accessories."),
    Product("Magic Keyboard for iPad Pro 12.9-inch", "$129", "The Magic Keyboard provides a comfortable typing experience, a built-in trackpad, backlit keys, and a floating cantilever design for the iPad Pro 12.9-inch.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                ShoppingApp()
            }
        }
    }
}

@Composable
//The top navigation bar
fun TopBackBar(
    title: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (navigationIcon != null) {
                navigationIcon()
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }
            if (!title.isNullOrEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
//The main product list
fun ProductList(
    products: List<Product>,
    onProductSelected: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(products) { product ->
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProductSelected(product) }
                    .padding(16.dp)
            )
            Divider(color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
//The main screen contains the list of products
fun ProductListScreen(onProductSelected: (Product) -> Unit) {
    Scaffold(
        topBar = {
            TopBackBar(
                title = "Yuanman's Shopping App"
            )
        }
    ) { innerPadding ->
        ProductList(
            products = products,
            onProductSelected = onProductSelected,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
//Product details content
fun ProductDetails(product: Product?, modifier: Modifier = Modifier) {
    if (product != null) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = product.price,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select a product to view details.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
//Details pane setup
fun ProductDetailsScreen(
    product: Product?,
    onBack: (() -> Unit)? = null,
    showTopBar: Boolean = true,
    title: String? = product?.name
) {
    BackHandler(enabled = onBack != null) {
        onBack?.invoke()
    }
    Scaffold(
        topBar = {
            if (showTopBar) {
                TopBackBar(
                    title = title,
                    navigationIcon = if (onBack != null) {
                        {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    } else {
                        null
                    }
                )
            }
        }
    ) { innerPadding ->
        ProductDetails(product, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
//orientations judge
fun isLandscape(): Boolean {
    val orientation = LocalConfiguration.current.orientation
    return orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
//The main function
fun ShoppingApp(shoppingViewModel: ShoppingViewModel = viewModel()) {
    val landscape = isLandscape()

    if (landscape) {
        Row {
            Box(
                modifier = Modifier
                    .weight(3f) // Set left pane to take 30% of the landscape
                    .fillMaxHeight()
            ) {
                ProductListScreen(onProductSelected = { product ->
                    shoppingViewModel.selectedProduct = product
                })
            }
            Box(
                modifier = Modifier
                    .weight(7f) //Set right pane to take 70% of the landscape
                    .fillMaxHeight()
            ) {
                if (shoppingViewModel.selectedProduct != null) {
                    ProductDetailsScreen(
                        product = shoppingViewModel.selectedProduct,
                        onBack = null,
                        showTopBar = true,
                        title = null
                    )
                } else {
                    Scaffold(
                        topBar = {
                            TopBackBar()
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Select a product to view details.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Portrait mode remains the same
        if (shoppingViewModel.selectedProduct == null) {
            ProductListScreen(onProductSelected = { product ->
                shoppingViewModel.selectedProduct = product
            })
        } else {
            ProductDetailsScreen(
                product = shoppingViewModel.selectedProduct,
                onBack = {
                    shoppingViewModel.selectedProduct = null
                }
            )
        }
    }
}

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = Color(0xFFf6f1e5),
        onPrimary = Color(0xFF203655),
        background = Color(0xFFd9c1a1),
        onBackground = Color(0xFF203655),
        surface = Color(0xFFd9c1a1),
        onSurface = Color(0xFF203655)
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewProductDetailsScreen() {
    MyAppTheme {
        ProductDetailsScreen(product = products.first(), onBack = {})
    }
}