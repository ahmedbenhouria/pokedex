package com.pokedex.presentation.pokemonList

import com.pokedex.domain.model.Pokemon

data class PokemonListUiState(
    val pokemonList: List<Pokemon> = listOf(),
    val loadError: String = "",
    val isLoading: Boolean = false,
    val endReached: Boolean = false,
)