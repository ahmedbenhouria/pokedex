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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val repository: PokemonRepositoryImpl) : ViewModel() {

    private val _state = MutableStateFlow(PokemonListUiState())
    val state = _state.asStateFlow()

    private var _curPage = 0

    init {
        loadPokemonPaginated()
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )
            when (val result = repository.getPokemonList(_curPage)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        endReached = _curPage * PAGE_SIZE >= result.data!!.count
                    )
                    val pokemonEntries = result.data.results.map {
                        Pokemon(
                            id = it.getId(),
                            name = it.name,
                            imageUrl = getPokemonImage(it.getId())
                        )
                    }

                    _curPage++
                    val updatedPokemonList = _state.value.pokemonList.toMutableList()
                    updatedPokemonList += pokemonEntries

                    _state.value = _state.value.copy(
                        loadError = "",
                        isLoading = false,
                        pokemonList = updatedPokemonList
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        loadError = result.message!!,
                        isLoading = false
                    )
                }

                else -> {
                    _state.value = _state.value.copy(
                        isLoading = true
                    )
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


