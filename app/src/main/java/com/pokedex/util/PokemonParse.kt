package com.pokedex.util

import androidx.compose.ui.graphics.Color
import com.pokedex.R
import com.pokedex.ui.theme.Bug
import com.pokedex.ui.theme.Dragon
import com.pokedex.ui.theme.Electric
import com.pokedex.ui.theme.Fairy
import com.pokedex.ui.theme.Fighting
import com.pokedex.ui.theme.Fire
import com.pokedex.ui.theme.Flying
import com.pokedex.ui.theme.Ghost
import com.pokedex.ui.theme.Grass
import com.pokedex.ui.theme.Ground
import com.pokedex.ui.theme.Ice
import com.pokedex.ui.theme.Normal
import com.pokedex.ui.theme.Poison
import com.pokedex.ui.theme.Psychic
import com.pokedex.ui.theme.Rock
import com.pokedex.ui.theme.Steel
import com.pokedex.ui.theme.Water
import java.util.Locale

fun parseTypeToDrawable(type: String): Int {
    return when(type.lowercase(Locale.ROOT)) {
        "normal" -> R.drawable.normal
        "fire" -> R.drawable.fire
        "water" -> R.drawable.water
        "electric" -> R.drawable.electric
        "grass" -> R.drawable.grass
        "ice" -> R.drawable.ice
        "fighting" -> R.drawable.fighting
        "poison" -> R.drawable.poison
        "ground" -> R.drawable.ground
        "flying" -> R.drawable.flying
        "psychic" -> R.drawable.psychic
        "bug" -> R.drawable.bug
        "rock" -> R.drawable.rock
        "ghost" -> R.drawable.ghost
        "dragon" -> R.drawable.dragon
        "dark" -> R.drawable.dark
        "steel" -> R.drawable.steel
        "fairy" -> R.drawable.fairy
        else -> R.drawable.normal
    }
}

fun parseTypeToColor(type: String): Color {
    return when(type.lowercase(Locale.ROOT)) {
        "normal" -> Steel
        "fire" -> Fire
        "water" -> Water
        "electric" -> Electric
        "grass" -> Grass
        "ice" -> Ice
        "fighting" -> Fighting
        "poison" -> Poison
        "ground" -> Ground
        "flying" -> Flying
        "psychic" -> Psychic
        "bug" -> Bug
        "rock" -> Rock
        "ghost" -> Ghost
        "dragon" -> Dragon
        "dark" -> Dragon
        "steel" -> Steel
        "fairy" -> Fairy
        else -> Normal
    }
}

/*fun parseStatToColor(stat: Stat): Color {
    return when(stat.stat.name.lowercase(Locale.ROOT)) {
        "hp" -> HPColor
        "attack" -> AtkColor
        "defense" -> DefColor
        "special-attack" -> SpAtkColor
        "special-defense" -> SpDefColor
        "speed" -> SpdColor
        else -> Color.White
    }
}

fun parseStatToAbbr(stat: Stat): String {
    return when(stat.stat.name.lowercase(Locale.ROOT)) {
        "hp" -> "HP"
        "attack" -> "ATK"
        "defense" -> "DEF"
        "special-attack" -> "SP.ATK"
        "special-defense" -> "SP.DEF"
        "speed" -> "SPD"
        else -> ""
    }
}*/