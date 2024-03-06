package com.pokedex.data.remote.responses

import com.google.gson.annotations.SerializedName

data class PokemonByTypeDto(
    @SerializedName("pokemon") var pokemon : PokemonDto,
    @SerializedName("slot") var slot: Int
)