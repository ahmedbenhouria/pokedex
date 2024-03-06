package com.pokedex.presentation.pokemonList.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokedex.ui.theme.PokedexAppTheme
import com.pokedex.ui.theme.interFont

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: (Boolean) -> Unit = {},
    searchQuery: String = "",
    onSearch: (String) -> Unit = {}
) {
    val localFocusManager = LocalFocusManager.current

    BasicTextField(modifier = modifier
        .shadow(1.5.dp, RoundedCornerShape(18.dp))
        .background(Color.White, RoundedCornerShape(18.dp))
        .padding(vertical = 9.dp, horizontal = 17.dp)
        .fillMaxWidth(),
        value = searchQuery,
        onValueChange = {
            onSearch(it)
        },
        singleLine = true,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            onClick(true)
                        }
                    }
                }
            },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions {
            localFocusManager.clearFocus()
        },
        textStyle = LocalTextStyle.current.copy(
            fontFamily = interFont,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) Text(
                        placeholderText,
                        style = LocalTextStyle.current.copy(
                            color = Color.LightGray,
                            fontFamily = interFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CustomSearchBarPreview() {
    PokedexAppTheme {
        CustomSearchBar(
            placeholderText = "Search",
            trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFFCE2020)
            )
        })
    }
}