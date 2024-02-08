package com.pokedex.ui.screens.pokemonList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mxalbert.sharedelements.LocalSharedElementsRootScope
import com.mxalbert.sharedelements.SharedElement
import com.mxalbert.sharedelements.SharedMaterialContainer
import com.pokedex.data.models.local.PokemonListItem
import com.pokedex.data.models.remote.Pokemon
import com.pokedex.ui.screens.CrossFadeTransitionSpec
import com.pokedex.ui.screens.ListScreen
import com.pokedex.ui.screens.MaterialFadeInTransitionSpec
import com.pokedex.ui.screens.changePokemon
import com.pokedex.ui.screens.previousSelectedPokemon
import com.pokedex.ui.theme.clashDisplayFont
import com.pokedex.util.Resource
import com.pokedex.util.parseTypeToColor
import com.pokedex.util.parseTypeToDrawable

@Composable
fun PokemonListScreen(
    listState: LazyGridState,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }

    LaunchedEffect(listState) {
        val previousIndex = previousSelectedPokemon.coerceAtLeast(0)
        if (!listState.layoutInfo.visibleItemsInfo.any { it.index == previousIndex }) {
            listState.scrollToItem(previousIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        val scope = LocalSharedElementsRootScope.current!!

        LazyVerticalGrid(
            state = listState,
            contentPadding = PaddingValues(13.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val itemCount = if(pokemonList.size % 2 == 0) {
                pokemonList.size / 2
            } else {
                pokemonList.size / 2 + 1
            }

            itemsIndexed(items = pokemonList) { index, item ->
                if(index >= itemCount - 1 && !endReached) {
                    viewModel.loadPokemonPaginated()
                }
                PokemonItem(
                    item = item,
                    modifier = Modifier.clickable(
                        enabled = !scope.isRunningTransition,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        scope.changePokemon(index, pokemonList)
                    }
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if(isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = Color(0xFFE4A121),
                        strokeWidth = 5.dp,
                    )
                }
            }

            /*         if(loadError.isNotEmpty()) {
                         RetrySection(error = loadError) {
                             viewModel.loadPokemonPaginated()
                         }
                     }*/
        }
    }
}

@Composable
fun PokemonItem(
    item: PokemonListItem,
    modifier: Modifier = Modifier,
    viewModel: PokemonViewModel = hiltViewModel(),
) {
    val defaultDominantColor = Color.Gray

    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    var dominantDarkerColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    val softerColor = ColorUtils.blendARGB(dominantColor.toArgb(), Color.White.toArgb(), 0.5f)


    val pokemonInfo by produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(item.pokemonName.lowercase())
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
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
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = item.number.toString().padStart(3, '0'),
                        color = dominantDarkerColor,
                        fontFamily = clashDisplayFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp
                    )
                    SharedElement(
                        key = item.pokemonName,
                        screenKey = ListScreen,
                        transitionSpec = CrossFadeTransitionSpec
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .fillMaxWidth()
                                .height(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AutoResizedText(
                                text = item.pokemonName,
                                color = Color.Black,
                                style = TextStyle(
                                    fontFamily = clashDisplayFont,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                            )
                        }

                    }

                    when (pokemonInfo) {
                        is Resource.Success -> {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for(type in pokemonInfo.data!!.types) {
                                    Box(
                                        modifier = Modifier
                                            .size(17.dp)
                                            .clip(CircleShape)
                                            .background(parseTypeToColor(type)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = parseTypeToDrawable(type)),
                                            contentDescription = type.type.name,
                                            modifier = Modifier.size(10.5.dp)
                                        )
                                    }
                                }
                            }
                        }
                        is Resource.Error -> {
                            Text(
                                text = pokemonInfo.message!!,
                                color = Color.Red,
                                modifier = modifier
                            )
                        }
                        is Resource.Loading -> {
                            CircularProgressIndicator(
                                color = Color(0xFFE4A121),
                                strokeWidth = 4.dp,
                                modifier = Modifier.padding(40.dp)
                            )
                        }
                    }
                }
            }


            SharedMaterialContainer(
                key = item.imageUrl,
                screenKey = ListScreen,
                color = Color.Transparent,
                transitionSpec = MaterialFadeInTransitionSpec
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .size(92.dp)
                        .align(Alignment.CenterVertically),
                    contentScale = ContentScale.Crop,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.pokemonName,
                    onSuccess = {
                        viewModel.calcDominantColor(it.result.drawable) { color, darkerColor ->
                            dominantColor = color
                            dominantDarkerColor = darkerColor
                        }
                    },
                    loading = {
                        CircularProgressIndicator(
                            color = Color(0xFFE4A121),
                            strokeWidth = 4.dp,
                            modifier = Modifier.padding(25.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AutoResizedText(
    text: String,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    modifier: Modifier = Modifier,
    color: Color = style.color
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = MaterialTheme.typography.headlineMedium.fontSize

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        softWrap = false,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                if (style.fontSize.isUnspecified) {
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = defaultFontSize
                    )
                }
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedTextStyle.fontSize * 0.95
                )
            } else {
                shouldDraw = true
            }
        }
    )
}