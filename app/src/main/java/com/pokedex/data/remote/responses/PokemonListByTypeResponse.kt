package com.pokedex.data.remote.responses

import com.google.gson.annotations.SerializedName

data class PokemonListByTypeResponse(
    @SerializedName("pokemon") var pokemon: List<PokemonByTypeDto>? = listOf()
)