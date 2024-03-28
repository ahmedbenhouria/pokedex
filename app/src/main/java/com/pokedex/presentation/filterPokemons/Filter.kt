package com.pokedex.presentation.filterPokemons

data class Filter(
    val id: String,
    val name: String
)

val typesFilteringList = listOf(
    Filter(id = "1", name = "normal"),
    Filter(id = "10", name = "fire"),
    Filter(id = "11", name = "water"),
    Filter(id = "12", name = "grass"),
    Filter(id = "7", name = "bug"),
    Filter(id = "13", name = "electric"),
    Filter(id = "18", name = "fairy"),
    Filter(id = "14", name = "psychic")
)

val sortFilteringList = listOf(
    Filter(id = "1", name = "A - Z"),
    Filter(id = "2", name = "Heaviest -\nLightest"),
    Filter(id = "3", name = "Lightest -\nHeaviest"),
    Filter(id = "4", name = "Tallest -\nShortest"),
    Filter(id = "5", name = "Shortest -\nTallest")
)