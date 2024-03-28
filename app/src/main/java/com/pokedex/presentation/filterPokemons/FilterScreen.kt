package com.pokedex.presentation.filterPokemons

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.pokedex.R
import com.pokedex.ui.theme.sfProFont
import com.pokedex.util.parseTypeToColor
import com.pokedex.util.parseTypeToDrawable
import java.util.Locale

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
                items(typesFilteringList) { type ->
                    val softerColor = ColorUtils.blendARGB(parseTypeToColor(type.name).toArgb(), Color.White.toArgb(), 0.65f)

                    FilterItem(
                        modifier = Modifier.background(Color(softerColor)),
                        type = type,
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
                items(sortFilteringList) { type ->
                    FilterItem(
                        modifier = Modifier.background(Color.White),
                        type = type,
                        isSortBy = true,
                        onItemClick = {}
                    )
                }
            }
        }

    }
}

@Composable
fun FilterItem(
    modifier: Modifier = Modifier,
    type: Filter,
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
                onItemClick(type.id)
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
                text = type.name.replaceFirstChar {
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
}

internal fun Modifier.coloredShadow(
    color: Color = Color.Black,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Float = 0f,
    modifier: Modifier = Modifier,
) = this.then(
    modifier.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.dp.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel)
            val bottomPixel =  (this.size.height + spreadPixel)

            if (blurRadius != 0.dp) {
                /*
                    The feature maskFilter used below to apply the blur effect only works
                    with hardware acceleration disabled.
                 */
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }

            frameworkPaint.color = color.toArgb()
            it.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint
            )
        }
    }
)