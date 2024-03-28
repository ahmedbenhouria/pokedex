package com.pokedex.data.remote

import com.pokedex.data.remote.responses.PokemonListByTypeResponse
import com.pokedex.data.remote.responses.PokemonResponse
import com.pokedex.data.remote.responses.PokemonListResponse
import com.pokedex.data.remote.responses.PokemonSpeciesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse

    @GET("type/{id}")
    suspend fun getPokemonListByType(
        @Path("id") id: String
    ): PokemonListByTypeResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetails(
        @Path("id") id: String
    ): PokemonResponse

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: String
    ): PokemonSpeciesResponse
}