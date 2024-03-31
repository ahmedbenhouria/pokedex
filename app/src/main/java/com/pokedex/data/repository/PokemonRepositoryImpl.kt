package com.pokedex.data.repository

import com.pokedex.data.remote.PokeApi
import com.pokedex.data.remote.responses.PokemonListByTypeResponse
import com.pokedex.data.remote.responses.PokemonListResponse
import com.pokedex.data.mapper.PokemonDetailsMapper
import com.pokedex.domain.model.PokemonDetails
import com.pokedex.domain.repository.PokemonRepository
import com.pokedex.util.Constants.PAGE_SIZE
import com.pokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@ActivityScoped
class PokemonRepositoryImpl @Inject constructor(private val api: PokeApi) : PokemonRepository {

   override suspend fun getPokemonList(curPage: Int): Resource<PokemonListResponse> {
      val response = try {
         api.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)
      } catch(e: Exception) {
         return Resource.Error("An unknown error occurred.")
      }
      return Resource.Success(response)
   }

   override suspend fun getPokemonListByType(id: String): Resource<PokemonListByTypeResponse> {
      val response = try {
         api.getPokemonListByType(id)
      } catch(e: Exception) {
         return Resource.Error("An unknown error occurred.")
      }
      return Resource.Success(response)
   }

   override suspend fun getPokemonDetails(id: String): Resource<PokemonDetails> = coroutineScope {
      val pokemonResponse = async {
         api.getPokemonDetails(id)
      }.await()

      val pokemonSpeciesResponse = async {
         val pokemonId = pokemonResponse.species.getId()
         api.getPokemonSpecies(pokemonId)
      }.await()

      return@coroutineScope try {
         val response = PokemonDetailsMapper.buildFrom(
            pokemonResponse = pokemonResponse,
            pokemonSpeciesResponse = pokemonSpeciesResponse
         )
         Resource.Success(response)
      } catch(e: Exception) {
         Resource.Error("An unknown error occurred.")
      }
   }

   override suspend fun getPokemonTypes(id: String): List<String> {
      return api.getPokemonDetails(id).types.map {
         it.type.name
      }
   }
}