package com.pokedex.data.remote.responses

import com.google.gson.annotations.SerializedName

data class FlavorTextEntries (
    @SerializedName("flavor_text")
    val flavorText: String,
    @SerializedName("language")
    val language: Language,
    @SerializedName("version")
    val version: Version

)