package com.pokedex.util

object Constants {
    const val BASE_URL = "https://pokeapi.co/api/v2/"

    const val PAGE_SIZE = 20
}

fun getPokemonImage(i: String) =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${i}.png"
