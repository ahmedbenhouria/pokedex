package com.pokedex.data.remote

import com.pokedex.data.remote.responses.PokemonListByTypeResponse
import com.pokedex.data.remote.responses.PokemonDetailsDto
import com.pokedex.data.remote.responses.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetails(
        @Path("id") id: String
    ): PokemonDetailsDto

    @GET("type/{id}")
    suspend fun getPokemonListByType(
        @Path("id") id: String
    ): PokemonListByTypeResponse
}