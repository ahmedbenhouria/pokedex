package com.pokedex.data.remote.responses

import com.google.gson.annotations.SerializedName

data class Type(
    @SerializedName("type") val type: TypeX
)