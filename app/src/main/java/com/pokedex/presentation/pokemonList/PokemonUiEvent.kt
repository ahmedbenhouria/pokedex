package com.pokedex.presentation.pokemonList

import androidx.compose.ui.graphics.Color

sealed interface PokemonUiEvent {
    data class SearchQueryChanged(val searchQuery: String): PokemonUiEvent
    data class PokemonTypeIdChanged(val typeId: String): PokemonUiEvent
}