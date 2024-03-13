package com.pokedex.presentation.pokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.pokedex.data.repository.PokemonRepositoryImpl
import com.pokedex.domain.model.Pokemon
import com.pokedex.domain.model.PokemonDetails
import com.pokedex.util.Constants.PAGE_SIZE
import com.pokedex.util.Resource
import com.pokedex.util.getPokemonImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class PokemonViewModel @Inject constructor(private val repository: PokemonRepositoryImpl) : ViewModel() {

    private var _curPage = 0

    private val _pokemonListState = MutableStateFlow(PokemonListUiState())

    private val _pokemonTypeId= MutableStateFlow("")
    val pokemonTypeId = _pokemonTypeId.asStateFlow()

    private val _searchQuery= MutableStateFlow("")
    val searchQuery= _searchQuery.asStateFlow()

    private var cachedPokemonList = mutableListOf<Pokemon>()

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
        loadPokemonPaginated()

        _pokemonTypeId
            .filter { it.isNotEmpty() }
            .mapLatest { id ->
                if (cachedPokemonList.isEmpty()) {
                    cachedPokemonList = _pokemonListState.value.data.toMutableList()
                }
                getPokemonListByType(id)
            }.launchIn(viewModelScope)
    }

    fun onEvent(event: PokemonUiEvent) {
        when (event) {
            is PokemonUiEvent.SearchQueryChanged -> {
                _searchQuery.value = event.searchQuery
            }
            is PokemonUiEvent.PokemonTypeIdChanged -> {
                _pokemonTypeId.value = event.typeId
            }
            is PokemonUiEvent.ResetData -> {
                _pokemonTypeId.value = ""

                if (_pokemonListState.value.isDataFiltered) {
                    _pokemonListState.update {
                        it.copy(
                            data = cachedPokemonList,
                            isDataFiltered = false,
                            isSearching = false
                        )
                    }
                }
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

                    _curPage++
                    val updatedPokemonList = _pokemonListState.value.data.toMutableList()
                    updatedPokemonList += pokemonEntries

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

    private fun getPokemonListByType(id: String) {
        viewModelScope.launch {
            _pokemonListState.update {
                it.copy(
                    isLoading = true,
                    isSearching = false
                )
            }

            when (val result = repository.getPokemonListByType(id)) {
                is Resource.Success -> {
                    _pokemonListState.update { screenState ->
                        screenState.copy(
                            data = result.data!!.pokemon!!.map {
                                Pokemon(
                                    id = it.pokemon.getId(),
                                    name = it.pokemon.name,
                                    imageUrl = getPokemonImage(it.pokemon.getId())
                                )
                            },
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

    suspend fun getPokemonDetails(id: String): Resource<PokemonDetails> {
        return repository.getPokemonDetails(id)
    }
}


