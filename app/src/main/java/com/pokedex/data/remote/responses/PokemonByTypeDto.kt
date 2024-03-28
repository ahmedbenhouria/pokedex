package com.pokedex.data.remote.responses

import com.google.gson.annotations.SerializedName

data class PokemonByTypeDto(
    @SerializedName("pokemon")
    val pokemon : PokemonDto,
    @SerializedName("slot")
    val slot: Int
)