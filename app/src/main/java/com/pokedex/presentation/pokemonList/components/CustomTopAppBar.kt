package com.pokedex.presentation.pokemonList.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pokedex.R

@Composable
fun CustomTopAppBar(
    bgColor: Color = Color.White,
    searchQuery: String = "",
    isSearchByTypeVisible: Boolean = false,
    pokemonTypeId: String = "",
    onSearchQueryChange: (String) -> Unit,
    onSearchBarClick: (Boolean) -> Unit,
    onSearchByTypeVisible: (Boolean) -> Unit,
    onPokemonTypeChange: (String) -> Unit
) {
    val localFocusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(top = 15.dp)
            .height(85.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        AnimatedContent(
            targetState = isSearchByTypeVisible,
            label = "",
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 300, easing = EaseIn))
                    .togetherWith(fadeOut(animationSpec = tween(durationMillis = 300, easing = EaseOut)))
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
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                if (pokemonTypeId.isNotEmpty()) {
                                    onSearchByTypeVisible(true)
                                    onPokemonTypeChange("")
                                    onSearchQueryChange("")
                                } else {
                                    onSearchByTypeVisible(false)
                                }
                                localFocusManager.clearFocus()
                            }
                    )
                }
                false -> {
                    Image(
                        painter = painterResource(id = R.drawable.pokeball),
                        contentDescription = null,
                        modifier = Modifier.size(43.dp)
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