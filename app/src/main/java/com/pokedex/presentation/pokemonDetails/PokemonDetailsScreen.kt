package com.pokedex.presentation.pokemonDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
import com.pokedex.presentation.CrossFadeTransitionSpec
import com.pokedex.presentation.DetailsScreen
import com.pokedex.presentation.MaterialFadeOutTransitionSpec
import com.pokedex.presentation.changePokemon
import com.pokedex.presentation.pokemonList.PokemonViewModel
import com.pokedex.ui.theme.clashDisplayFont
import com.pokedex.ui.theme.interFont
import com.pokedex.util.parseTypeToDrawable

@Composable
fun PokemonDetailsScreen(
    item: Pokemon,
    viewModel: PokemonViewModel = hiltViewModel(),
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface

    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    val softerColor = ColorUtils.blendARGB(dominantColor.toArgb(), Color.White.toArgb(), 0.4f)

    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val pokemonTypeId by viewModel.pokemonTypeId.collectAsStateWithLifecycle()

    val pokemonList = state.data

    var isInfoClicked by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .background(Color(softerColor))
    ) {
        Column(
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
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                SharedElement(
                    key = item.name,
                    screenKey = DetailsScreen,
                    transitionSpec = CrossFadeTransitionSpec
                ) {
                    Text(
                        text = item.name,
                        color = Color.Black,
                        fontFamily = clashDisplayFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 33.sp
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.pokeball_bg),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(330.dp)
                            .padding(top = 65.dp)
                            .alpha(0.6f)
                    )

                    SharedMaterialContainer(
                        key = item.imageUrl,
                        screenKey = DetailsScreen,
                        shape = RoundedCornerShape(25.dp),
                        color = Color.Transparent,
                        transitionSpec = MaterialFadeOutTransitionSpec
                    ) {
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .size(250.dp)
                                .align(Alignment.Center),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.imageUrl)
                                .build(),
                            contentDescription = null,
                            onSuccess = {
                                viewModel.calcDominantColor(it.result.drawable) { color, _ ->
                                    dominantColor = color
                                }
                            }
                        )
                    }
                }

                AnimatedContent(
                    targetState = isInfoClicked,
                    label = "Animated Content"
                ) { isClicked ->
                    when (isClicked) {
                        false -> {
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
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            isInfoClicked = true
                                        },
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Pulsating {
                                    /*    Image(
                                            painter = painterResource(id = R.drawable.pokeball),
                                            contentDescription = null,
                                            modifier = Modifier.size(65.dp)
                                        )*/
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

                        true -> {
//                            PokemonDetailsStateWrapper(pokemonInfo = pokemonInfo)
                        }

                    }
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

}

/*@Composable
fun PokemonDetailsSection(
    pokemonInfo: Pokemon
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        PokemonTypeSection(types = pokemonInfo.type)
//        PokemonBaseStats(pokemonInfo = pokemonInfo)
    }

}*/

@Composable
fun PokemonTypeSection(
    types: List<String?>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .background(
                color = Color(0x28FFFFFF),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(bottom = 15.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        for(type in types) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = parseTypeToDrawable(type!!)),
                    contentDescription = type,
                    modifier = Modifier.size(65.dp)
                )

                Text(
                    text = type.uppercase(),
                    fontFamily = clashDisplayFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PokemonStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 28.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercent = animateFloatAsState(
        targetValue = if(animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        ), label = ""
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(Color.LightGray)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = statName,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = (curPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/*
@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelayPerItem: Int = 100
) {
    val maxBaseStat = remember {
        pokemonInfo.stats.maxOf { it.baseStat }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        Text(
            text = "Base stats:",
            fontSize = 20.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))

        for(i in pokemonInfo.stats.indices) {
            val stat = pokemonInfo.stats[i]
            PokemonStat(
                statName = parseStatToAbbr(stat),
                statValue = stat.baseStat,
                statMaxValue = maxBaseStat,
                statColor = parseStatToColor(stat),
                animDelay = i * animDelayPerItem
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
*/


/*
@Composable
fun PokemonDetailsStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    when(pokemonInfo) {
        is Resource.Success -> {
            PokemonDetailsSection(pokemonInfo = pokemonInfo.data!!)
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
*/



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


