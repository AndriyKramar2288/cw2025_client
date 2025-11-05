package com.banew.cw2025_client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = AppTypography,
        content = content,
        colorScheme = MaterialTheme.colorScheme.copy(primary = Color.White)
    )
}