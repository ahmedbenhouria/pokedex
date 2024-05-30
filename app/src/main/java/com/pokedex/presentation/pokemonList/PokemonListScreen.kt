package com.pokedex.presentation.pokemonList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.pokedex.R
import com.pokedex.domain.model.Pokemon
import com.pokedex.navigation.Screen
import com.pokedex.presentation.filterPokemons.Filter
import com.pokedex.presentation.filterPokemons.typeFilterList
import com.pokedex.presentation.pokemonList.components.LoadingAnimation
import com.pokedex.presentation.pokemonList.components.RetrySection
import com.pokedex.ui.theme.clashDisplayFont
import com.pokedex.ui.theme.interFont
import com.pokedex.ui.theme.sfProFont
import com.pokedex.util.parseTypeToColor
import com.pokedex.util.parseTypeToDrawable
import java.util.Locale

@Composable
fun PokemonListScreen(
    navController: NavHostController,
    onColorChange: (Color) -> Unit,
    searchQuery: String,
    onItemClick: (String, List<String>) -> Unit,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val typeId by viewModel.pokemonTypeId.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = searchQuery) {
        viewModel.onEvent(PokemonUiEvent.SearchQueryChanged(searchQuery))
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (typeId.isNotEmpty()) {
            typeFilterList.find { it.id == typeId }?.let {
                TypeBarSection(
                    type = it,
                    onColorChange = { color ->
                        if (navController.currentDestination?.route != Screen.PokemonDetails.route) {
                            onColorChange(color)
                        }
                    }
                )
            }
        }

        if (state.loadError.isNotEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                RetrySection(error = state.loadError) {
                    if (typeId.isNotEmpty()) {
                        viewModel.getPokemonListByType(typeId)
                    } else {
                        viewModel.loadPokemonPaginated()
                    }
                }
            }
        } else {
            PokemonListSection(
                modifier = if (typeId.isNotEmpty()) {
                    Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topEnd = 19.dp, topStart = 19.dp)
                        )
                        .padding(top = 35.dp)
                } else
                    Modifier.padding(top = 5.dp),
                state = state,
                onItemClick = { id, color ->
                    onColorChange(color)
                    onItemClick(id, if (typeId.isNotEmpty()) state.data.map { it.id } else emptyList())
                },
                loadPokemonPaginated = {
                    if (typeId.isNotEmpty()) {
                        viewModel.getPokemonListByType(typeId)
                    } else {
                        viewModel.loadPokemonPaginated()
                    }
                }
            )
        }
    }
}

@Composable
private fun PokemonListSection(
    modifier: Modifier = Modifier,
    state: PokemonListUiState,
    onItemClick: (String, Color) -> Unit,
    loadPokemonPaginated: () -> Unit = {}
) {
    val listState = rememberLazyGridState()

    if (state.isSearching && state.data.isEmpty()) {
        EmptyPokemonList(modifier)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyVerticalGrid(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(17.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    val itemCount = if (state.data.size % 2 == 0) {
                        state.data.size
                    } else {
                        state.data.size + 1
                    }
                    items(count = state.data.size, key = { index -> state.data[index].id }) { item ->
                        if (item >= itemCount - 1 && !state.endReached && !state.isLoading && !state.isDataFiltered) {
                            LaunchedEffect(key1 = true) {
                                loadPokemonPaginated()
                            }
                        }
                        PokemonItem(
                            item = state.data[item],
                            onItemClick = { id, color ->
                                onItemClick(id, color)
                            }
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    if (state.isLoading && state.data.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(90.dp),
                            color = Color(0xFFE4A121),
                            strokeWidth = 5.dp
                        )
                    }
                }

                this@Column.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    visible = state.isLoading && state.data.isNotEmpty(),
                    enter = slideInVertically { 200 }.plus(fadeIn(animationSpec = tween(120))),
                    exit = slideOutVertically { 200 }.plus(fadeOut(animationSpec = tween(120))),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 21.dp)
                            .size(38.dp)
                            .background(color = Color(0xFF3D2A04), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(21.dp),
                            color = Color(0xFFE4A121),
                            strokeWidth = 2.8.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonItem(
    item: Pokemon,
    onItemClick: (String, Color) -> Unit,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {
    val defaultColor = Color.Gray

    var dominantColor by remember { mutableStateOf(defaultColor) }
    var dominantDarkerColor by remember { mutableStateOf(defaultColor) }

    val softerColor = ColorUtils.blendARGB(dominantColor.toArgb(), Color.White.toArgb(), 0.3f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(softerColor))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onItemClick(item.id, Color(softerColor))
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 11.dp, horizontal = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = item.id.padStart(3, '0'),
                            color = dominantDarkerColor,
                            fontFamily = clashDisplayFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 25.sp
                        )
                        Box(
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .fillMaxWidth()
                                .height(20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            val modifiedName = item.name.replaceFirstChar { char ->
                                if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString()
                            }

                            val formattedName = if (modifiedName.length > 10) {
                                modifiedName.take(8) + "..."
                            } else {
                                modifiedName
                            }

                            Text(
                                text = formattedName,
                                color = Color.Black,
                                style = TextStyle(
                                    fontFamily = sfProFont,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.5.sp
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            for(type in item.type) {
                                Box(
                                    modifier = Modifier
                                        .size(17.dp)
                                        .clip(CircleShape)
                                        .background(parseTypeToColor(type!!)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = parseTypeToDrawable(type)),
                                        contentDescription = type,
                                        modifier = Modifier.size(10.5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(95.dp)
                            .align(Alignment.CenterEnd),
                        contentScale = ContentScale.Crop,
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.name,
                        onSuccess = {
                            viewModel.calcDominantColor(it.result.drawable) { color, darkerColor ->
                                dominantColor = color
                                dominantDarkerColor = darkerColor
                            }
                        },
                        loading = {
                            LoadingAnimation()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TypeBarSection(
    type: Filter,
    onColorChange: (Color) -> Unit
) {
    val colorId = ColorUtils.blendARGB(parseTypeToColor(type.name).toArgb(), Color.White.toArgb(), 0.7f)
    onColorChange(Color(colorId))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(134.dp)
            .background(Color.Transparent)
            .padding(bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(parseTypeToColor(type.name)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = parseTypeToDrawable(type.name)),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(110.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(parseTypeToColor(type.name)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = type.name.uppercase(),
                color = Color.White,
                fontFamily = interFont,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
fun EmptyPokemonList(
    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .padding(bottom = 150.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 22.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_pokemons),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = "No Pokemons Found!",
            color = Color.Black,
            fontFamily = sfProFont,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp
        )
    }
}