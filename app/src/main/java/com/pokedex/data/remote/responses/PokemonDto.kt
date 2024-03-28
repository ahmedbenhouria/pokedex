package com.pokedex.data.remote.responses

import com.google.gson.annotations.SerializedName

data class PokemonDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
) {
    fun getId(): String {
        val pattern = """/(\d+)/?$""".toRegex()
        val match = pattern.find(url)
        val pokemonNumber = match?.value?.substringBeforeLast("/") ?: "0"
        return pokemonNumber.replace("/", "")
    }
}