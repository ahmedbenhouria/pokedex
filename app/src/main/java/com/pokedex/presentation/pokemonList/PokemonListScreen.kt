package com.pokedex.presentation.pokemonList

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mxalbert.sharedelements.LocalSharedElementsRootScope
import com.mxalbert.sharedelements.SharedElement
import com.mxalbert.sharedelements.SharedMaterialContainer
import com.pokedex.R
import com.pokedex.domain.model.Pokemon
import com.pokedex.domain.model.PokemonDetails
import com.pokedex.presentation.CrossFadeTransitionSpec
import com.pokedex.presentation.ListScreen
import com.pokedex.presentation.MaterialFadeInTransitionSpec
import com.pokedex.presentation.changePokemon
import com.pokedex.presentation.pokemonList.components.CustomTopAppBar
import com.pokedex.presentation.pokemonList.components.LoadingAnimation
import com.pokedex.presentation.pokemonList.components.RetrySection
import com.pokedex.presentation.previousSelectedPokemon
import com.pokedex.ui.theme.clashDisplayFont
import com.pokedex.ui.theme.interFont
import com.pokedex.util.Resource
import com.pokedex.util.parseTypeToColor
import com.pokedex.util.parseTypeToDrawable
import timber.log.Timber
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PokemonListScreen(
    listState: LazyGridState,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val localFocusManager = LocalFocusManager.current

    val state by viewModel.screenState.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val pokemonTypeId by viewModel.pokemonTypeId.collectAsStateWithLifecycle()

    LaunchedEffect(listState) {
        val previousIndex = previousSelectedPokemon.coerceAtLeast(0)
        if (!listState.layoutInfo.visibleItemsInfo.any { it.index == previousIndex }) {
            listState.scrollToItem(previousIndex)
        }
    }

    if (state.isSearching && !state.isDataFiltered) {
        viewModel.onSearchByTypeVisible(false)
        LaunchedEffect(true) {
            listState.animateScrollToItem(0)
        }
    } else if (state.isSearching) {
        LaunchedEffect(true) {
            listState.animateScrollToItem(0)
        }
    }

    var bgColor by remember {
        mutableStateOf(Color.White)
    }

    if (pokemonTypeId.isEmpty()) {
        bgColor = Color.White
    }

    Scaffold(
        containerColor = bgColor,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            CustomTopAppBar(
                bgColor = bgColor,
                searchQuery = searchQuery,
                isSearchByTypeVisible = state.isSearchByTypeVisible,
                pokemonTypeId = pokemonTypeId,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onSearchBarClick = { viewModel.onSearchByTypeVisible(it) },
                onSearchByTypeVisible = { viewModel.onSearchByTypeVisible(it) },
                onPokemonTypeChange = { viewModel.onPokemonTypeChange(it) }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = state.isSearchByTypeVisible,
            label = "",
            transitionSpec = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300),
                    initialOffset = { 500 }
                )
                    .plus(fadeIn(animationSpec = tween(durationMillis = 300)))
                    .togetherWith(slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(300),
                        targetOffset = { 500 }
                    )
                        .plus(fadeOut(animationSpec = tween(300))))
            }
        ) { targetState ->
            when (targetState) {
                true -> {
                    if (pokemonTypeId.isEmpty()) {
                        SearchByTypesSection(
                            paddingValues = paddingValues,
                            onPokemonTypeChange = {
                                viewModel.onPokemonTypeChange(it)
                                localFocusManager.clearFocus()
                            }
                        )
                    } else {
                        if (state.loadError.isNotEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                RetrySection(error = state.loadError) {
                                    viewModel.getPokemonListByType(id = pokemonTypeId)
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                typesList.find { it.id == pokemonTypeId }
                                    ?.let { TypeBarSection(it) { colorId ->
                                        bgColor = Color(colorId)
                                    } }

                                PokemonListSection(
                                    modifier = Modifier
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                        )
                                        .padding(top = 42.dp),
                                    listState = listState,
                                    paddingValues = paddingValues,
                                    pokemonList = state.data,
                                    isLoading = state.isLoading,
                                    isSearching = state.isSearching
                                )
                            }

                        }
                    }
                }
                false -> {
                    if (state.loadError.isNotEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            RetrySection(error = state.loadError) {
                                viewModel.loadPokemonPaginated()
                            }
                        }
                    } else {
                        PokemonListSection(
                            modifier = Modifier
                                .padding(paddingValues)
                                .padding(vertical = 3.dp),
                            listState = listState,
                            paddingValues = paddingValues,
                            pokemonList = state.data,
                            isLoading = state.isLoading,
                            endReached = state.endReached,
                            isSearching = state.isSearching,
                            loadPokemonPaginated = viewModel::loadPokemonPaginated
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonListSection(
    modifier: Modifier = Modifier,
    listState: LazyGridState,
    paddingValues: PaddingValues,
    pokemonList: List<Pokemon>,
    isLoading: Boolean = false,
    endReached: Boolean = false,
    isSearching: Boolean = false,
    isDataFiltered: Boolean = false,
    loadPokemonPaginated: () -> Unit = {}
) {

    if (isSearching && pokemonList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                fontFamily = clashDisplayFont,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            val scope = LocalSharedElementsRootScope.current!!

            LazyVerticalGrid(
                state = listState,
                contentPadding = PaddingValues(horizontal = 13.dp),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(17.dp),
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                val itemCount = if (pokemonList.size % 2 == 0) {
                    pokemonList.size
                } else {
                    pokemonList.size + 1
                }
                items(count = pokemonList.size, key = { it }) {item ->
                    if (item >= itemCount - 1 && !endReached && !isLoading && !isDataFiltered) {
                        LaunchedEffect(key1 = true) {
                            loadPokemonPaginated()
                        }
                    }
                    PokemonItem(
                        item = pokemonList[item],
                        modifier = Modifier.clickable(
                            enabled = !scope.isRunningTransition,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            scope.changePokemon(item, pokemonList)
                        }
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                if(isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = Color(0xFFE4A121),
                        strokeWidth = 5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonItem(
    item: Pokemon,
    modifier: Modifier = Modifier,
    viewModel: PokemonViewModel = hiltViewModel(),
) {
    val defaultColor = Color.Gray

    var dominantColor by remember {
        mutableStateOf(defaultColor)
    }

    var dominantDarkerColor by remember {
        mutableStateOf(defaultColor)
    }

    val softerColor = ColorUtils.blendARGB(dominantColor.toArgb(), Color.White.toArgb(), 0.3f)

    val pokemonDetails by produceState<Resource<PokemonDetails>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonDetails(item.id)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(softerColor))
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
                    modifier = Modifier.padding(vertical = 11.dp, horizontal = 10.dp)
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
                        SharedElement(
                            key = item.name,
                            screenKey = ListScreen,
                            transitionSpec = CrossFadeTransitionSpec
                        ) {
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
                                        fontFamily = clashDisplayFont,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }

                        PokemonTypesSection(pokemonDetails = pokemonDetails)
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    SharedMaterialContainer(
                        key = item.imageUrl,
                        screenKey = ListScreen,
                        color = Color.Transparent,
                        transitionSpec = MaterialFadeInTransitionSpec
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
}

@Composable
fun PokemonTypesSection(
    pokemonDetails: Resource<PokemonDetails>
) {
    when (pokemonDetails) {
        is Resource.Success -> {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for(type in pokemonDetails.data!!.type) {
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
        is Resource.Error -> {
            Timber.tag("ERROR TYPES").e(pokemonDetails.message)
        }
        is Resource.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color(0xFFE4A121),
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}

@Composable
fun SearchByTypesSection(
    paddingValues: PaddingValues,
    onPokemonTypeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 21.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Search by type",
            color = Color.Black,
            fontFamily = interFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(typesList) {type ->
                TypeItem(type) { typeId ->
                    onPokemonTypeChange(typeId)
                }
            }
        }
    }
}

data class Type(val id: String, val name: String)

val typesList = listOf(
    Type(id = "1", name = "normal"),
    Type(id = "10", name = "fire"),
    Type(id = "11", name = "water"),
    Type(id = "12", name = "grass"),
    Type(id = "7", name = "bug"),
    Type(id = "13", name = "electric"),
    Type(id = "18", name = "fairy"),
    Type(id = "14", name = "psychic")
)

@Composable
fun TypeItem(
    type: Type,
    onTypeClick: (String) -> Unit
) {
    val softerColor = ColorUtils.blendARGB(parseTypeToColor(type.name).toArgb(), Color.White.toArgb(), 0.65f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .clip(CircleShape)
            .background(Color(softerColor))
            .clickable {
                onTypeClick(type.id)
            }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 21.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = type.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else
                        it.toString()
                },
                color = Color.Black,
                fontFamily = interFont,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
                    .background(parseTypeToColor(type.name)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = parseTypeToDrawable(type.name)),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun TypeBarSection(
    type: Type,
    onColorChange: (Int) -> Unit
) {
    val softerColor = ColorUtils.blendARGB(parseTypeToColor(type.name).toArgb(), Color.White.toArgb(), 0.8f)
    onColorChange(softerColor)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(142.dp)
            .background(Color(softerColor))
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(parseTypeToColor(type.name)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = parseTypeToDrawable(type.name)),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(110.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(parseTypeToColor(type.name)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = type.name.uppercase(),
                color = Color.White,
                fontFamily = interFont,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}

/*
@Preview (showBackground = true)
@Composable
fun PokemonFilteredListSectionPreview() {
    PokedexAppTheme {
        PokemonFilteredListSection(paddingValues = PaddingValues())
    }
}
*/
