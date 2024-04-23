package com.pokedex.presentation.pokemonDetails

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
import com.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
    private val repository: PokemonRepositoryImpl,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _pokemonDetailsState = MutableStateFlow(PokemonDetailsUiState())
    val pokemonDetailsState = _pokemonDetailsState.asStateFlow()

    init {
        savedStateHandle.get<String>("pokemonId")?.let { pokemonId ->
            getPokemonDetails(pokemonId)
        }
    }

    private fun getPokemonDetails(id: String) {
        viewModelScope.launch {
            _pokemonDetailsState.update {
                it.copy(isLoading = true)
            }

            when (val result = repository.getPokemonDetails(id)) {
                is Resource.Success -> {
                    _pokemonDetailsState.update {
                        it.copy(
                            pokemon = result.data!!,
                            loadError = "",
                            isLoading = false
                        )
                    }
                }

                is Resource.Error -> {
                    _pokemonDetailsState.update {
                        it.copy(
                            loadError = result.message!!,
                            isLoading = false
                        )
                    }
                }

                else -> {
                    _pokemonDetailsState.update {
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
}