package com.example.thundora.view.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

//val navigationItems = listOf(
//    NavigationItem(
//        title = "Home",
//        icon = Icons.Default.Home,
//        route = Screen.Home.rout
//    ),
//    NavigationItem(
//        title = "Profile",
//        icon = Icons.Default.Person,
//        route = Screen.Profile.rout
//    ),
//    NavigationItem(
//        title = "Cart",
//        icon = Icons.Default.ShoppingCart,
//        route = Screen.Cart.rout
//    ),
//    NavigationItem(
//        title = "Setting",
//        icon = Icons.Default.Settings,
//        route = Screen.Setting.rout
//    )
//)