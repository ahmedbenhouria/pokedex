package com.pokemon.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mxalbert.sharedelements.FadeMode
import com.mxalbert.sharedelements.LocalSharedElementsRootScope
import com.mxalbert.sharedelements.MaterialArcMotionFactory
import com.mxalbert.sharedelements.MaterialContainerTransformSpec
import com.mxalbert.sharedelements.ProgressThresholds
import com.mxalbert.sharedelements.SharedElement
import com.mxalbert.sharedelements.SharedElementsRoot
import com.mxalbert.sharedelements.SharedElementsRootScope
import com.mxalbert.sharedelements.SharedElementsTransitionSpec
import com.mxalbert.sharedelements.SharedMaterialContainer
import com.pokemon.app.data.models.local.PokemonListItem
import com.pokemon.app.data.models.remote.Pokemon
import com.pokemon.app.destinations.PokemonInfoDestination
import com.pokemon.app.ui.theme.PokemonAppTheme
import com.pokemon.app.ui.theme.clashDisplayFont
import com.pokemon.app.ui.theme.interFont
import com.pokemon.app.util.Resource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.Locale

private var selectedPokemon: Int by mutableIntStateOf(-1)
private var previousSelectedPokemon: Int = -1

private const val ListScreen = "list"
private const val DetailsScreen = "details"

private const val TransitionDurationMillis = 400

private val CrossFadeTransitionSpec = SharedElementsTransitionSpec(
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.Cross,
    fadeProgressThresholds = ProgressThresholds(0.10f, 0.40f)
)
private val MaterialFadeInTransitionSpec = MaterialContainerTransformSpec(
    pathMotionFactory = MaterialArcMotionFactory,
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.In
)
private val MaterialFadeOutTransitionSpec = MaterialContainerTransformSpec(
    pathMotionFactory = MaterialArcMotionFactory,
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.Out
)

@Destination (start = true)
@Composable
fun PokemonListRoot(
    destinationsNavigator: DestinationsNavigator,
    viewModel: MainScreenViewModel = hiltViewModel(),
) {
    val systemUiController = rememberSystemUiController()
    val pokemonList by remember { viewModel.pokemonList }

    SideEffect {
        systemUiController.setStatusBarColor(Color(0xFF373737))
        systemUiController.setNavigationBarColor(Color.Transparent)
    }

    SharedElementsRoot {
        BackHandler(enabled = selectedPokemon >= 0) {
            changePokemon(-1, pokemonList)
        }

        val listState = rememberLazyGridState()
        Crossfade(
            targetState = selectedPokemon,
            animationSpec = tween(durationMillis = TransitionDurationMillis),
            label = "crossFade anim"
        ) { item ->
            when {
                item < 0 -> PokemonListScreen(listState)
                else -> PokemonDetailsScreen(destinationsNavigator, pokemonList[item])
            }
        }
    }
}


@Composable
private fun PokemonListScreen(
    listState: LazyGridState,
    viewModel: MainScreenViewModel = hiltViewModel()
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
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(start = 12.dp, top = 41.dp)
                .background(
                    color = Color(0xFF373737),
                    shape = RoundedCornerShape(12.dp)
                )

        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color(0xFFBABABA)
            )
        }
        Column(
            modifier = Modifier.padding(
                top = 45.dp,
                bottom = 14.dp,
                start = 15.dp
            )
        ) {
            Text(
                text = "Select your",
                color = Color.White,
                fontFamily = clashDisplayFont,
                fontWeight = FontWeight.Light,
                fontSize = 25.sp
            )
            Text(
                text = "Pokèmon",
                color = Color.White,
                fontFamily = clashDisplayFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 35.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        val scope = LocalSharedElementsRootScope.current!!

        LazyVerticalGrid(
            state = listState,
            contentPadding = PaddingValues(15.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    modifier = Modifier.clickable(enabled = !scope.isRunningTransition) {
                        scope.changePokemon(index, pokemonList)
                    }
                )
            }
        }

        Box(
            contentAlignment = Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if(isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFFE4A121),
                    strokeWidth = 4.dp,
                    modifier = Modifier.padding(bottom = 25.dp)
                )
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
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val defaultDominantColor = Color(0xFF1D1D1D)
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(190.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SharedMaterialContainer(
                key = item.imageUrl,
                screenKey = ListScreen,
                color = Color.Transparent,
                transitionSpec = MaterialFadeInTransitionSpec
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .size(145.dp)
                        .align(Alignment.CenterHorizontally),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.pokemonName,
                    onSuccess = {
                        viewModel.calcDominantColor(it.result.drawable) { color ->
                            dominantColor = color
                        }
                    },
                    loading = {
                        CircularProgressIndicator(
                            color = Color(0xFFE4A121),
                            strokeWidth = 4.dp,
                            modifier = Modifier.padding(45.dp)
                        )
                    }
                )
            }
            SharedElement(
                key = item.pokemonName,
                screenKey = ListScreen,
                transitionSpec = CrossFadeTransitionSpec
            ) {
                Text(
                    text = item.pokemonName,
                    color = Color.White,
                    fontFamily = clashDisplayFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
private fun PokemonDetailsScreen(
    destinationsNavigator: DestinationsNavigator,
    item: PokemonListItem,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val pokemonInfo by produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(item.pokemonName.lowercase(Locale.ROOT))
    }

    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    val pokemonList by remember { viewModel.pokemonList }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        val scope = LocalSharedElementsRootScope.current!!

        IconButton(
            onClick = {
                if (!scope.isRunningTransition) {
                scope.changePokemon(-1, pokemonList)
                } },
            modifier = Modifier
                .padding(start = 12.dp, top = 41.dp)
                .background(
                    color = Color(0xFF373737),
                    shape = RoundedCornerShape(12.dp)
                )

        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color(0xFFBABABA)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 70.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SharedElement(
                key = item.pokemonName,
                screenKey = DetailsScreen,
                transitionSpec = CrossFadeTransitionSpec
            ) {
                Text(
                    text = item.pokemonName,
                    color = Color.White,
                    fontFamily = clashDisplayFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 33.sp
                )
            }

            SharedMaterialContainer(
                key = item.imageUrl,
                screenKey = DetailsScreen,
                shape = RoundedCornerShape(25.dp),
                color = Color.Transparent,
                transitionSpec = MaterialFadeOutTransitionSpec
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .size(280.dp)
                        .align(Alignment.CenterHorizontally),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .build(),
                    contentDescription = null,
                    onSuccess = {
                        viewModel.calcDominantColor(it.result.drawable) { color ->
                            dominantColor = color
                        }
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable(
                            enabled = !scope.isRunningTransition,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            scope.changePokemon(pokemonList.indexOf(item) - 1, pokemonList)
                        }
                )

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 15.dp)
                        .clickable {
                            destinationsNavigator.navigate(PokemonInfoDestination())
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Pulsating {
                        Image(
                            painter = painterResource(id = R.drawable.pokeball),
                            contentDescription = null,
                            modifier = Modifier.size(65.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Click to View Info",
                        color = Color(0xFFE4A121),
                        fontFamily = interFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.next_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable(
                            enabled = !scope.isRunningTransition,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            scope.changePokemon(pokemonList.indexOf(item) + 1, pokemonList)
                        }
                )
            }


    }



/*        if(pokemonInfo is Resource.Success) {
            pokemonInfo.data?.sprites?.let { sprites ->
                SharedMaterialContainer(
                    key = sprites.frontDefault,
                    screenKey = DetailsScreen,
                    shape = RoundedCornerShape(25.dp),
                    color = Color.Transparent,
                    transitionSpec = FadeOutTransitionSpec
                ) {
                    val scope = LocalSharedElementsRootScope.current!!

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(sprites.frontDefault)
                            .build(),
                        contentDescription = pokemonInfo.data.name,
                        onSuccess = {
                            viewModel.calcDominantColor(it.result.drawable) { color ->
                                dominantColor = color
                            }
                        },
                        modifier = Modifier
                            .size(250.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable(enabled = !scope.isRunningTransition) {
                                scope.changeUser(-1, pokemonList)
                            }
                    )
                }
            }
        }*/
    }
}

@Destination
@Composable
fun PokemonInfo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_details),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    when(pokemonInfo) {
        is Resource.Success -> {

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
                color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier
            )
        }
    }
}

@Composable
fun Pulsating(pulseFraction: Float = 1.2f, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseFraction,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(modifier = Modifier.scale(scale)) {
        content()
    }
}

private fun SharedElementsRootScope.changePokemon(
    user: Int,
    pokemonList: List<PokemonListItem>
    ) {
    val currentPokemon = selectedPokemon
    if (currentPokemon != user) {
        val targetPokemon = if (user >= 0) user else currentPokemon
        if (targetPokemon >= 0) {
            pokemonList[targetPokemon].let {
                prepareTransition(it.pokemonName, it.imageUrl)
            }
        }
        previousSelectedPokemon = selectedPokemon
        selectedPokemon = user
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PokemonAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .paint(
                        painterResource(id = R.drawable.bg),
                        contentScale = ContentScale.FillBounds
                    )
            ){
//                PokemonListRoot()
            }
        }
    }
}
