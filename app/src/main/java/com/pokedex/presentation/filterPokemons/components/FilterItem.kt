package com.pokedex.presentation.filterPokemons.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokedex.R
import com.pokedex.presentation.filterPokemons.Filter
import com.pokedex.ui.theme.sfProFont
import com.pokedex.util.parseTypeToColor
import com.pokedex.util.parseTypeToDrawable
import java.util.Locale

@Composable
fun FilterItem(
    modifier: Modifier = Modifier,
    filter: Filter,
    isSortBy: Boolean = false,
    onItemClick: (String) -> Unit
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .run {
                if (isSortBy)
                    this.coloredShadow(
                        color = Color.Gray,
                        blurRadius = 2.dp,
                        borderRadius = 90.dp,
                        offsetX = 1.dp,
                        offsetY = 1.dp
                    )
                else
                    this
            }
            .fillMaxWidth()
            .height(if (isSortBy) 65.dp else 60.dp)
            .clip(CircleShape)
            .then(modifier)
            .clickable {
                onItemClick(filter.id)
            }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .padding(end = if (isSortBy) 4.dp else 0.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = filter.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else
                        it.toString()
                },
                color = Color.Black,
                fontFamily = sfProFont,
                fontWeight = FontWeight.Normal,
                fontSize = if (isSortBy) 16.sp else 17.sp
            )

            if (isSortBy) {
                Icon(
                    painter = painterResource(id = R.drawable.sort_by),
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier
                        .size(24.dp)
                        .alpha(0.9f)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(parseTypeToColor(filter.name)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = parseTypeToDrawable(filter.name)),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

