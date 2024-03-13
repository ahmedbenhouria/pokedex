package com.pokedex.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Pokemon(
    val id: String,
    val name: String,
    val imageUrl: String
)