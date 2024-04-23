package com.pokedex.presentation.parentScaffold

import androidx.compose.ui.graphics.Color

data class ScaffoldUiState(
    val searchQuery: String = "",
    val typeId: String = "",
    val bgColor: Color = Color.White,
    val isBtnVisible: Boolean = false
)