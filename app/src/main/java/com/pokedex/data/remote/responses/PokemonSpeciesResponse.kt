package com.pokedex.data.remote.responses

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesResponse (
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntries>,
    @SerializedName("genera")
    val genera: List<Genera>
) {
    fun getGenusByLanguage(langCode: String): String? {
        return genera.find { genera ->
            genera.language.name == langCode
        }?.genus
    }

    fun getFlavorText(langCode: String, versionName: String): String? {
        return flavorTextEntries.find { flavorText ->
            flavorText.language.name == langCode && flavorText.version.name == versionName
        }?.flavorText
    }

    fun getRandomFlavorText(langCode: String): String? {
        return flavorTextEntries.find { flavorText ->
            flavorText.language.name == langCode && flavorText.version.name.any()
        }?.flavorText
    }
}