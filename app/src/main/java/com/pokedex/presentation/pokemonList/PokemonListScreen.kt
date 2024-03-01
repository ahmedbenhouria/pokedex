package com.pokedex.presentation.pokemonList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.pokedex.presentation.pokemonList.components.CustomSearchBar
import com.pokedex.presentation.pokemonList.components.LoadingAnimation
import com.pokedex.presentation.pokemonList.components.RetrySection
import com.pokedex.presentation.previousSelectedPokemon
import com.pokedex.ui.theme.clashDisplayFont
import com.pokedex.util.Resource
import com.pokedex.util.parseTypeToColor
import com.pokedex.util.parseTypeToDrawable
import timber.log.Timber
import java.util.Locale

@Composable
fun PokemonListScreen(
    listState: LazyGridState,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(listState) {
        val previousIndex = previousSelectedPokemon.coerceAtLeast(0)
        if (!listState.layoutInfo.visibleItemsInfo.any { it.index == previousIndex }) {
            listState.scrollToItem(previousIndex)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopAppBar(onSearchQueryChange = viewModel::onSearchQueryChange)
        }
    ) { paddingValues ->
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
                listState = listState,
                paddingValues = paddingValues,
                pokemonList = state.pokemonList,
                isLoading = state.isLoading,
                endReached = state.endReached,
                isSearching = state.isSearching,
                loadPokemonPaginated = viewModel::loadPokemonPaginated
            )
        }
    }
}

@Composable
private fun TopAppBar(
    onSearchQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(85.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.pokeball),
            contentDescription = null,
            modifier = Modifier.size(44.dp)
        )

        CustomSearchBar(
            modifier = Modifier,
            placeholderText = "Search",
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFFCE2020)
                )
            }
        ) {
            onSearchQueryChange(it)
        }
    }
}

@Composable
private fun PokemonListSection(
    listState: LazyGridState,
    paddingValues: PaddingValues,
    pokemonList: List<Pokemon>,
    isLoading: Boolean = false,
    endReached: Boolean = false,
    isSearching: Boolean = false,
    loadPokemonPaginated: () -> Unit
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
                .padding(paddingValues),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            val scope = LocalSharedElementsRootScope.current!!

            LazyVerticalGrid(
                state = listState,
                contentPadding = PaddingValues(horizontal = 13.dp),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val itemCount = if (pokemonList.size % 2 == 0) {
                    pokemonList.size
                } else {
                    pokemonList.size + 1
                }
                items(count = pokemonList.size, key = { it }) {item ->
                    if (item >= itemCount - 1 && !endReached && !isLoading) {
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
                                Text(
                                    text = item.name.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.ROOT
                                        ) else
                                            it.toString()
                                    },
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
fun CustomDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .pointerInput(Unit) { detectTapGestures { } }
                        .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                        .width(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            MaterialTheme.colorScheme.surface,
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    content()
                }

            }
        }
    }
}

