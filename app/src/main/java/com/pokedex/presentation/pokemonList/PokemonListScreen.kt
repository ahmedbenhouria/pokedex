package com.pokedex.presentation.pokemonList

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.WhitePoint
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
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pokedex.R
import com.pokedex.domain.model.Pokemon
import com.pokedex.domain.model.PokemonDetails
import com.pokedex.presentation.pokemonList.components.CustomTopAppBar
import com.pokedex.presentation.pokemonList.components.LoadingAnimation
import com.pokedex.presentation.pokemonList.components.RetrySection
import com.pokedex.presentation.pokemonList.components.ShimmerListItem
import com.pokedex.presentation.pokemonList.destinations.PokemonListScreenDestination
import com.pokedex.presentation.pokemonList.destinations.SearchByTypeScreenDestination
import com.pokedex.ui.theme.clashDisplayFont
import com.pokedex.ui.theme.interFont
import com.pokedex.util.Resource
import com.pokedex.util.parseTypeToColor
import com.pokedex.util.parseTypeToDrawable
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.rememberNavHostEngine
import timber.log.Timber
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PokemonListRoot(
    viewModel: PokemonViewModel
) {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    var color by remember {
        mutableStateOf(Color.White)
    }

    val bgColor by animateColorAsState(
        targetValue = color,
        label = "bgColor",
        animationSpec = tween(
            durationMillis = 100,
            easing = LinearEasing
        )
    )

    SideEffect {
        systemUiController.setStatusBarColor(
            bgColor,
            darkIcons = true
        )
        systemUiController.setNavigationBarColor(
            Color.White,
            darkIcons = true
        )
    }

    val state by viewModel.pokemonListState.collectAsStateWithLifecycle()
    val typeId by viewModel.pokemonTypeId.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var isBtnVisible by remember {
        mutableStateOf(false)
    }

    if (searchQuery.isNotEmpty()) {
        if (navController.currentDestination?.route == SearchByTypeScreenDestination.route) {
            viewModel.onEvent(PokemonUiEvent.ResetData)
            navController.navigate(PokemonListScreenDestination.route)
            isBtnVisible = false
        }
    }

    Timber.tag("LOGGG").d(state.toString())

    Scaffold(
        containerColor = bgColor,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            CustomTopAppBar(
                searchQuery = searchQuery,
                isVisible = isBtnVisible,
                onSearchQueryChange = {
                    viewModel.onEvent(PokemonUiEvent.SearchQueryChanged(it)) },
                onSearchBarClick = {
                    if (typeId.isEmpty()) {
                        isBtnVisible = it
                        if (navController.currentDestination?.route != SearchByTypeScreenDestination.route) {
                            navController.navigate(SearchByTypeScreenDestination.route)
                        }
                    }
                },
                onBackBtnClick = {
                    when (navController.currentDestination?.route) {
                        PokemonListScreenDestination.route -> {
                            color = Color.White
                            viewModel.onEvent(PokemonUiEvent.SearchQueryChanged(""))
                            navController.navigate(SearchByTypeScreenDestination.route)
                        }
                        SearchByTypeScreenDestination.route -> {
                            viewModel.onEvent(PokemonUiEvent.ResetData)
                            isBtnVisible = false
                            navController.navigate(PokemonListScreenDestination.route)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            val navHostEngine = rememberNavHostEngine(
                rootDefaultAnimations = RootNavGraphDefaultAnimations(
                    enterTransition = { fadeIn(animationSpec = tween(400)).plus(
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(400),
                            initialOffset = { 250 }
                        )) },
                    exitTransition = { fadeOut(animationSpec = tween(300)).plus(
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(400),
                            targetOffset = { 250 }
                        )) }
                )
            )

            DestinationsNavHost(
                navController = navController,
                navGraph = NavGraphs.root,
                engine = navHostEngine,
            ) {
                composable(PokemonListScreenDestination) {
                    PokemonListScreen(
                        state = state,
                        loadPokemonPaginated = { viewModel.loadPokemonPaginated() },
                        onColorChange = { color = it },
                        typeId = typeId
                    )
                }
                composable(SearchByTypeScreenDestination) {
                    SearchByTypeScreen {
                        viewModel.onEvent(PokemonUiEvent.PokemonTypeIdChanged(it))
                        if (it.isNotEmpty()) {
                            navController.navigate(PokemonListScreenDestination.route)
                            isBtnVisible = true
                        }
                    }
                }
            }
        }
    }
}

@Destination (start = true)
@Composable
fun AnimatedVisibilityScope.PokemonListScreen(
    state: PokemonListUiState,
    loadPokemonPaginated: () -> Unit,
    onColorChange: (Color) -> Unit,
    typeId: String = ""
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (typeId.isNotEmpty()) {
            typesList.find { it.id == typeId }?.let {
                TypeBarSection(
                    type = it,
                    onColorChange = { color -> onColorChange(color) }
                )
            }
        }

        if (state.loadError.isNotEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                RetrySection(error = state.loadError) {
                    loadPokemonPaginated()
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
                loadPokemonPaginated = { loadPokemonPaginated() }
            )
        }
    }
}

@Destination
@Composable
fun SearchByTypeScreen(
    onPokemonTypeChange: (String) -> Unit = {}
) {
    val localFocusManager = LocalFocusManager.current

    SearchByTypesSection(
        onPokemonTypeChange = {
            onPokemonTypeChange(it)
            localFocusManager.clearFocus()
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedVisibilityScope.PokemonListSection(
    modifier: Modifier = Modifier,
    state: PokemonListUiState,
    loadPokemonPaginated: () -> Unit = {}
) {
    val listState = rememberLazyGridState()

    LaunchedEffect(key1 = state.isSearching) {
        if (listState.firstVisibleItemIndex != 0) {
            listState.animateScrollToItem(0)
        }
    }

    if (state.isSearching && state.data.isEmpty()) {
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
            if (!this@PokemonListSection.transition.isRunning) {
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
                    items(count = state.data.size, key = { it }) { item ->
                        if (item >= itemCount - 1 && !state.endReached && !state.isLoading && !state.isDataFiltered) {
                            LaunchedEffect(key1 = true) {
                                loadPokemonPaginated()
                            }
                        }
                        PokemonItem(
                            item = state.data[item],
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {}
                        )
                    }
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = Color(0xFFE4A121),
                        strokeWidth = 5.dp
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                if(state.isLoading) {
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

    val softerColor = ColorUtils.blendARGB(dominantColor.toArgb(), Color.White.toArgb(), 0.4f)

    val pokemonDetails by produceState<Resource<PokemonDetails>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonDetails(item.id)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(10.dp))
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
                        PokemonTypesSection(pokemonDetails = pokemonDetails)
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
    onPokemonTypeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            .height(58.dp)
            .clip(RoundedCornerShape(28.dp))
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
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp
            )

            Box(
                modifier = Modifier
                    .size(34.dp)
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

/*
@Preview (showBackground = true)
@Composable
fun PokemonFilteredListSectionPreview() {
    PokedexAppTheme {
        PokemonFilteredListSection(paddingValues = PaddingValues())
    }
}
*/
