package com.pokedex.domain.mapper

import com.pokedex.data.remote.responses.PokemonDetailsDto
import com.pokedex.domain.model.PokemonDetails

fun PokemonDetailsDto.toDomainPokemonDetails(): PokemonDetails {
    return PokemonDetails(
        id = this.id,
        name = this.name,
        height = this.height,
        weight = this.weight,
        type = this.types.map { it.type.name }
    )
}