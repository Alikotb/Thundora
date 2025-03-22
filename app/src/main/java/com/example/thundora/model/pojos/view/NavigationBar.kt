package com.example.thundora.model.pojos.view

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNAvigationBar(
    val title: ScreensRout,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)