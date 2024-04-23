package com.pokedex.presentation.parentScaffold

import androidx.compose.ui.graphics.Color

sealed interface ScaffoldUiEvent {
    data class SearchQueryChanged(val searchQuery: String): ScaffoldUiEvent
    data class PokemonTypeIdChanged(val typeId: String): ScaffoldUiEvent
    data class BgColorChanged(val bgColor: Color): ScaffoldUiEvent
    data class BackBtnVisibility(val isVisible: Boolean): ScaffoldUiEvent
}