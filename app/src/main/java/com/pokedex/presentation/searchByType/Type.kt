package com.pokedex.presentation.searchByType

data class Type(
    val id: String,
    val name: String
)

val typesList = listOf(
    Type(id = "1", name = "normal"),
    Type(id = "10", name = "fire"),
    Type(id = "11", name = "water"),
    Type(id = "12", name = "grass"),
    Type(id = "7", name = "bug"),
    Type(id = "13", name = "electric"),
    Type(id = "18", name = "fairy"),
    Type(id = "14", name = "psychic")
)