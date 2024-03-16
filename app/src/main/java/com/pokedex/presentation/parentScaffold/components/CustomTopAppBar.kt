package com.pokedex.presentation.parentScaffold.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pokedex.R
import kotlinx.coroutines.delay

@Composable
fun CustomTopAppBar(
    searchQuery: String = "",
    isVisible: Boolean = false,
    onSearchQueryChange: (String) -> Unit,
    onSearchBarClick: (Boolean) -> Unit,
    onBackBtnClick: () -> Unit,
) {
    val localFocusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp)
            .height(85.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        var isRotated by rememberSaveable { mutableStateOf(false) }
        var isEnabled by remember { mutableStateOf(true) }

        val rotationAngle by animateFloatAsState(
            targetValue = if (isRotated) 360F else 0f,
            animationSpec = tween(durationMillis = 1800),
            label = ""
        )

        LaunchedEffect(isEnabled) {
            delay(200)
            isEnabled = true
        }

        AnimatedContent(
            targetState = isVisible,
            label = "",
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 400))
                    .togetherWith(fadeOut(animationSpec = tween(durationMillis = 400)))
            }
        ) { targetState ->
            when (targetState) {
                true -> {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null,
                        tint = Color(0xFFCE2020),
                        modifier = Modifier
                            .width(43.dp)
                            .height(34.dp)
                            .clickable(
                                enabled = isEnabled,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onBackBtnClick()
                                isEnabled = false
                                isRotated = !isRotated
                                localFocusManager.clearFocus()
                            }
                    )
                }
                false -> {
                    Image(
                        painter = painterResource(id = R.drawable.pokeball),
                        contentDescription = null,
                        modifier = Modifier
                            .size(43.dp)
                            .rotate(rotationAngle)
                    )
                }
            }
        }

        CustomSearchBar(
            modifier = Modifier,
            placeholderText = "Search",
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFFCE2020)
                )
            },
            searchQuery = searchQuery,
            onClick = {
                onSearchBarClick(it)
            },
            onSearch = {
                onSearchQueryChange(it)
            }
        )
    }
}