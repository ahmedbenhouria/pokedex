package com.pokedex.presentation.pokemonDetails

import com.pokedex.domain.model.PokemonDetails

data class PokemonDetailsUiState(
    val pokemon: PokemonDetails = PokemonDetails(),
    val loadError: String = "",
    val isLoading: Boolean = false,
)