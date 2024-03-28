package com.pokedex.presentation.pokemonDetails

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.pokedex.R
import com.pokedex.domain.model.PokemonDetails
import com.pokedex.ui.theme.clashDisplayFont
import com.pokedex.ui.theme.interFont
import com.pokedex.ui.theme.sfProFont
import com.pokedex.util.parseTypeToColor
import java.util.Locale

@Composable
fun PokemonDetailsScreen(
    onNavigate: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    viewModel: PokemonDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.pokemonDetailsState.collectAsStateWithLifecycle()
    val pokemonDetails = state.pokemon

    var offsetX by remember { mutableFloatStateOf(0f) }
    val SWIPE_THRESHOLD = (LocalContext.current.resources.displayMetrics.widthPixels * 0.1).toFloat()

    if (state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(95.dp),
                color = Color(0xFFE4A121),
                strokeWidth = 5.dp
            )
        }
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.Transparent).pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        offsetX = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        val (x) = dragAmount
                        when {
                            x > 0 -> {
                                offsetX += dragAmount.x
                                if (offsetX > SWIPE_THRESHOLD) {
                                    onNavigate("previous")
                                    offsetX = 0f
                                }
                            }
                            x < 0 -> {
                                offsetX += dragAmount.x
                                if (offsetX < -SWIPE_THRESHOLD) {
                                    onNavigate("next")
                                    offsetX = 0f
                                }
                            }
                        }
                    }
                )
            }
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pokemonDetails.name.replaceFirstChar { char ->
                            if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString()
                        },
                        color = Color.Black,
                        fontFamily = sfProFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 28.sp
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pokemonDetails.id.toString().padStart(3, '0'),
                            color = Color.White,
                            fontFamily = clashDisplayFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 115.sp,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Gray,
                                    offset = Offset(x = 2f, y = 3f),
                                    blurRadius = 0.8f
                                )
                            ),
                            modifier = Modifier
                                .alpha(0.9f)
                                .align(Alignment.TopCenter)
                        )

                        Box(
                            modifier = Modifier
                                .padding(top = 81.dp)
                                .fillMaxSize()
                                .zIndex(-1f),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.pokeball_bg),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(top = 54.dp)
                                    .fillMaxWidth()
                                    .height(263.dp)
                                    .alpha(0.7f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .padding(top = 82.dp)
                                .fillMaxSize()
                                .zIndex(2f),
                            contentAlignment = Alignment.TopCenter
                        ) {

                            SubcomposeAsyncImage(
                                modifier = Modifier
                                    .padding(top = 15.dp)
                                    .size(259.dp)
                                    .align(Alignment.TopCenter),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(pokemonDetails.imageUrl)
                                    .build(),
                                contentDescription = null,
                                onSuccess = {
                                    viewModel.calcDominantColor(it.result.drawable) { color, _ ->
                                        val softerColor = ColorUtils.blendARGB(color.toArgb(), Color.White.toArgb(), 0.3f)
                                        onColorChange(Color(softerColor))
                                    }
                                }
                            )
                        }

                        Box(
                            contentAlignment = Alignment.BottomCenter,
                            modifier = Modifier
                                .padding(top = 332.dp)
                                .matchParentSize()
                                .background(
                                    Color.White,
                                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                )
                                .zIndex(0f)
                        ) {
                            PokemonDetailsSection(pokemonDetails)
                        }

                    }
                }
            }
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
fun PokemonDetailsSection(
    pokemonDetails: PokemonDetails
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        PokemonTypeSection(types = pokemonDetails.type)

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = addLineBreakToCategory(pokemonDetails.category),
                color = Color.Black,
                fontFamily = interFont,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )

            Box(modifier = Modifier
                .width(2.5.dp)
                .height(65.dp)
                .background(Color(0x4FD3D3D3))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "${pokemonDetails.weight} lbs.",
                    color = Color.Black,
                    fontFamily = interFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp
                )

                Text(
                    text = "Weight",
                    color = Color(0xFF7E7E7E),
                    fontFamily = interFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            }

            Box(modifier = Modifier
                .width(2.5.dp)
                .height(65.dp)
                .background(Color(0x4FD3D3D3))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "${pokemonDetails.height}'",
                    color = Color.Black,
                    fontFamily = interFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp
                )

                Text(
                    text = "Height",
                    color = Color(0xFF7E7E7E),
                    fontFamily = interFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 19.dp, vertical = 16.dp)
        ) {
            val description = pokemonDetails.flavorText.replace("\n", " ")

            Text(
                text = description,
                color = Color(0xFF7E7E7E),
                fontFamily = interFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )

            Icon(
                painter = painterResource(id = R.drawable.down_arrow),
                contentDescription = null,
                tint = Color(0xEEA5A5A5),
                modifier = Modifier
                    .size(33.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun PokemonTypeSection(
    types: List<String?>
) {
    Row(
        modifier = Modifier
            .padding(start = if (types.size == 1) 20.dp else 10.dp)
            .padding(top = 30.dp)
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 11.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        for (type in types) {
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(30.dp)
                    .background(color = parseTypeToColor(type!!), shape = CircleShape)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type.uppercase(),
                    color = Color.White,
                    fontFamily = sfProFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
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

private fun addLineBreakToCategory(category: String): String {
    val words = category.split(" ")
    val wordCount = words.size
    return if (wordCount <= 2) {
        category.replaceFirst(" ", "\n")
    } else {
        val index = category.lastIndexOf(" ")
        category.substring(0, index) + "\n" + category.substring(index + 1)
    }
}


