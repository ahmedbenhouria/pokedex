package com.pokedex.domain.repository

import com.pokedex.data.remote.responses.PokemonListResponse
import com.pokedex.domain.model.PokemonDetails
import com.pokedex.util.Resource

interface PokemonRepository {

    suspend fun getPokemonList(curPage: Int): Resource<PokemonListResponse>

    suspend fun getPokemonDetails(id: String): Resource<PokemonDetails>
}