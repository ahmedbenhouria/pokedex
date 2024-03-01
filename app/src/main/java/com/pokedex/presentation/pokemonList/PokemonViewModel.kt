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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class PokemonViewModel @Inject constructor(private val repository: PokemonRepositoryImpl) : ViewModel() {

    private var _curPage = 0

    private val _screenState = MutableStateFlow(PokemonListScreenState())

    private val _searchQuery= MutableStateFlow("")

    val screenState = _searchQuery
        .debounce(500L)
        .combine(_screenState) { searchQuery, screenState ->
            if (searchQuery.isNotEmpty()) {
                screenState.copy(
                    pokemonList = screenState.pokemonList.filter { pokemon ->
                        pokemon.name.contains(searchQuery.trim(), ignoreCase = true) ||
                                pokemon.id.padStart(3, '0') == searchQuery.trim().padStart(3, '0')
                    },
                    isLoading = false,
                    isSearching = true
                )
            } else {
                screenState.copy(isSearching = false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PokemonListScreenState())

    init {
        loadPokemonPaginated()
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            _screenState.update {
                it.copy(isLoading = true)
            }

            when (val result = repository.getPokemonList(_curPage)) {
                is Resource.Success -> {
                    _screenState.update {
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
                    val updatedPokemonList = _screenState.value.pokemonList.toMutableList()
                    updatedPokemonList += pokemonEntries

                    _screenState.update {
                        it.copy(
                            loadError = "",
                            isLoading = false,
                            pokemonList = updatedPokemonList
                        )
                    }
                }

                is Resource.Error -> {
                    _screenState.update {
                        it.copy(
                            loadError = result.message!!,
                            isLoading = false
                        )
                    }
                }

                else -> {
                    _screenState.update {
                        it.copy(isLoading = true)
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


