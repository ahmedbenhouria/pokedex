package com.pokedex.presentation.pokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.pokedex.data.repository.PokemonRepositoryImpl
import com.pokedex.domain.model.Pokemon
import com.pokedex.util.Constants.PAGE_SIZE
import com.pokedex.util.Resource
import com.pokedex.util.getPokemonImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _curPage = 0

    private val _pokemonListState = MutableStateFlow(PokemonListUiState())

    private val _pokemonTypeId= MutableStateFlow("")
    val pokemonTypeId = _pokemonTypeId.asStateFlow()

    private val _searchQuery= MutableStateFlow("")

    val pokemonListState = _searchQuery
        .debounce(500L)
        .combine(_pokemonListState) { searchQuery, screenState ->
            if (searchQuery.isNotEmpty()) {
                screenState.copy(
                    data = screenState.data.filter { pokemon ->
                        pokemon.name.contains(searchQuery.trim(), ignoreCase = true) ||
                                pokemon.id.padStart(3, '0') == searchQuery.trim().padStart(3, '0')
                    },
                    isLoading = false,
                    isSearching = true
                )
            } else {
                screenState.copy(isSearching = false)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PokemonListUiState()
        )

    init {
        savedStateHandle.get<String>("typeId")?.let { typeId ->
            _pokemonTypeId.value = typeId
            if (typeId.isEmpty()) {
                loadPokemonPaginated()
            } else {
                getPokemonListByType(typeId)
            }
        }
    }

    fun onEvent(event: PokemonUiEvent) {
        when (event) {
            is PokemonUiEvent.SearchQueryChanged -> {
                _searchQuery.value = event.searchQuery
            }
            is PokemonUiEvent.PokemonTypeIdChanged -> {
                _pokemonTypeId.value = event.typeId
            }
        }
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            _pokemonListState.update {
                it.copy(isLoading = true)
            }

            when (val result = repository.getPokemonList(_curPage)) {
                is Resource.Success -> {
                    _pokemonListState.update {
                        it.copy(endReached = _curPage * PAGE_SIZE >= result.data!!.count)
                    }

                    val pokemonEntries = result.data!!.results.map {
                        Pokemon(
                            id = it.getId(),
                            name = it.name,
                            imageUrl = getPokemonImage(it.getId())
                        )
                    }

                    val pokemonTypes = pokemonEntries.map {
                        async { repository.getPokemonTypes(it.id) }
                    }.awaitAll()

                    // Combine the Pokémon entries with their types
                    val updatedPokemonEntries = pokemonEntries.zip(pokemonTypes) { pokemonEntry, types ->
                        pokemonEntry.copy(type = types)
                    }

                    _curPage++
                    val updatedPokemonList = _pokemonListState.value.data.toMutableList()
                    updatedPokemonList += updatedPokemonEntries

                    _pokemonListState.update {
                        it.copy(
                            loadError = "",
                            isLoading = false,
                            data = updatedPokemonList
                        )
                    }
                }

                is Resource.Error -> {
                    _pokemonListState.update {
                        it.copy(
                            loadError = result.message!!,
                            isLoading = false
                        )
                    }
                }

                else -> {
                    _pokemonListState.update {
                        it.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun getPokemonListByType(id: String) {
        viewModelScope.launch {
            _pokemonListState.update {
                it.copy(
                    isLoading = true,
                    isSearching = false
                )
            }

            when (val result = repository.getPokemonListByType(id)) {
                is Resource.Success -> {
                    val pokemonEntries = result.data!!.pokemon!!.map {
                        Pokemon(
                            id = it.pokemon.getId(),
                            name = it.pokemon.name,
                            imageUrl = getPokemonImage(it.pokemon.getId())
                        )
                    }

                    val pokemonTypes = pokemonEntries.map {
                        async { repository.getPokemonTypes(it.id) }
                    }.awaitAll()

                    val updatedPokemonEntries = pokemonEntries.zip(pokemonTypes) { pokemonEntry, types ->
                        pokemonEntry.copy(type = types)
                    }

                    _pokemonListState.update { screenState ->
                        screenState.copy(
                            data = updatedPokemonEntries,
                            isLoading = false,
                            loadError = "",
                            isDataFiltered = true
                        )
                    }
                }

                is Resource.Error -> {
                    _pokemonListState.update {
                        it.copy(
                            loadError = result.message!!,
                            isLoading = false,
                            isDataFiltered = false
                        )
                    }
                }

                else -> {
                    _pokemonListState.update {
                        it.copy(isLoading = true, isDataFiltered = false)
                    }
                }
            }
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color, Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                val darkerColor = ColorUtils.blendARGB(colorValue, Color.Black.toArgb(), 0.3f)
                onFinish(Color(colorValue), Color(darkerColor))
            }
        }
    }
}


