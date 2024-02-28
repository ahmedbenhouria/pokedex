package com.pokedex.data.remote.responses

import com.google.gson.annotations.SerializedName

data class PokemonDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("types") val types: List<Type>
)