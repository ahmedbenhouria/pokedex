package com.pokedex.presentation.filterPokemons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.pokedex.presentation.filterPokemons.components.FilterItem
import com.pokedex.ui.theme.sfProFont
import com.pokedex.util.parseTypeToColor

@Composable
fun FilterScreen(
    onItemClick: (String) -> Unit = {}
) {
    val localFocusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 21.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Search by type",
                color = Color.Black,
                fontFamily = sfProFont,
                fontWeight = FontWeight.Medium,
                fontSize = 21.sp
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(typeFilterList) { typeFilter ->
                    val softerColor = ColorUtils.blendARGB(parseTypeToColor(typeFilter.name).toArgb(), Color.White.toArgb(), 0.65f)

                    FilterItem(
                        modifier = Modifier.background(Color(softerColor)),
                        filter = typeFilter,
                        isSortBy = false,
                        onItemClick = { typeId ->
                            onItemClick(typeId)
                            localFocusManager.clearFocus()
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Sort by",
                color = Color.Black,
                fontFamily = sfProFont,
                fontWeight = FontWeight.Medium,
                fontSize = 21.sp
            )

            LazyVerticalGrid(
                contentPadding = PaddingValues(top = 8.dp),
                modifier = Modifier.fillMaxHeight(),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(sortFilterList) { sortFilter ->
                    FilterItem(
                        modifier = Modifier.background(Color.White),
                        filter = sortFilter,
                        isSortBy = true,
                        onItemClick = {}
                    )
                }
            }
        }

    }
}