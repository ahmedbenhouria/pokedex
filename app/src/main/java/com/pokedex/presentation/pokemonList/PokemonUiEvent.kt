package com.pokedex.presentation.pokemonList

sealed interface PokemonUiEvent {
    data class SearchQueryChanged(val searchQuery: String): PokemonUiEvent
    data class PokemonTypeIdChanged(val typeId: String): PokemonUiEvent
    data object ResetData : PokemonUiEvent
}