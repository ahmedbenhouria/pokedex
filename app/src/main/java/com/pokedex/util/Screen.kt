package com.pokedex.util

sealed class Screen(
    val route: String
) {
    data object PokemonList : Screen(route = "PokemonList?typeId={typeId}") {
        fun passTypeId(id: String): String {
            return "PokemonList?typeId=${id}"
        }
    }
    data object Filter : Screen(route = "Filter")
    data object PokemonDetails : Screen(route = "PokemonDetails?pokemonId={pokemonId}") {
        fun passPokemonId(id: String): String {
            return "PokemonDetails?pokemonId=${id}"
        }
    }

}
