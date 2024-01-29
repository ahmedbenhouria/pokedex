package com.pokemon.app.data.models.remote

import com.google.gson.annotations.SerializedName

data class Version(
    val name: String,
    val url: String
)