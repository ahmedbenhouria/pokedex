package com.pokedex.presentation.searchByType

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.pokedex.ui.theme.sfProFont
import com.pokedex.util.parseTypeToColor
import com.pokedex.util.parseTypeToDrawable
import java.util.Locale

@Composable
fun SearchByTypeScreen(
    onTypeClick: (String) -> Unit = {}
) {
    val localFocusManager = LocalFocusManager.current

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
            fontFamily = sfProFont,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(typesList) { type ->
                TypeItem(type) { typeId ->
                    onTypeClick(typeId)
                    localFocusManager.clearFocus()
                }
            }
        }
    }
}

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
                fontFamily = sfProFont,
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


