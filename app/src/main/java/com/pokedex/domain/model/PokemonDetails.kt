package com.pokedex.domain.model

data class PokemonDetails(
    val id: Int = 0,
    val name: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val height: Int = 0,
    val weight: Int = 0,
    val type: List<String?> = emptyList(),
    val flavorText: String = ""
) {
 /*   fun getMetricHeight(): String {
        val x = height * 10
        return "$x cm"
    }

    fun getMetricWeight (): String {
        val x = weight.toDouble() / 100.0
        return "$x kg"
    }*/
}