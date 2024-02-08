package com.pokedex.ui.screens.pokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.pokedex.data.models.local.PokemonListItem
import com.pokedex.data.models.remote.Pokemon
import com.pokedex.repository.PokemonRepository
import com.pokedex.util.Constants.PAGE_SIZE
import com.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokemonListItem>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)
            when(result) {
                is Resource.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    val pokemonEntries = result.data.results.mapIndexed { _, entry ->
                        val number = if(entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${number}.png"
                        PokemonListItem(
                            pokemonName = entry.name.replaceFirstChar {
                                if (it.isLowerCase())
                                    it.titlecase(Locale.ROOT)
                                else
                                    it.toString() },
                            imageUrl = url,
                            number = number.toInt()
                        )
                    }
                    curPage++

                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokemonEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }

                else -> {}
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

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }
}