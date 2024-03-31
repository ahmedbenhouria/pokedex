package com.pokedex.presentation.parentScaffold

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pokedex.navigation.NavGraph
import com.pokedex.navigation.Screen
import com.pokedex.presentation.parentScaffold.components.CustomTopAppBar
import kotlinx.coroutines.delay

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ParentScaffold(
    viewModel: ParentScaffoldViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val color by viewModel.bgColor.collectAsStateWithLifecycle()

    var typeId by remember { mutableStateOf("") }

    val bgColor by animateColorAsState(
        targetValue = color,
        label = "bgColor",
        animationSpec = tween(
            durationMillis = 20,
            easing = LinearEasing
        )
    )

    SideEffect {
        systemUiController.setStatusBarColor(
            bgColor,
            darkIcons = true
        )
        systemUiController.setNavigationBarColor(
            Color.White,
            darkIcons = true
        )
    }

    var isBtnVisible by remember { mutableStateOf(false) }

    if (searchQuery.isNotEmpty()) {
        if (navController.currentDestination?.route == Screen.Filter.route) {
            LaunchedEffect(Unit) {
                delay(100)
                navController.popBackStack()
                isBtnVisible = false
            }
        }
    }

    if (navController.currentDestination?.route == Screen.PokemonDetails.route) {
        isBtnVisible = true
    }

    Scaffold(
        containerColor = bgColor,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            CustomTopAppBar(
                searchQuery = searchQuery,
                isVisible = isBtnVisible,
                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                onSearchBarClick = {
                    if (typeId.isEmpty()) {
                        when (navController.currentDestination?.route) {
                            Screen.PokemonList.route -> {
                                isBtnVisible = it
                                navController.navigate(Screen.Filter.route)
                            }
                            Screen.PokemonDetails.route -> {
                                isBtnVisible = false
                                viewModel.onBgColorChanged(Color.White)
                                navController.popBackStack()
                            }
                        }
                    }

                },
                onBackBtnClick = {
                    viewModel.onBgColorChanged(Color.White)
                    viewModel.onSearchQueryChanged("")
                    navController.popBackStack()

                    val type = navController
                        .currentBackStackEntry!!
                        .arguments!!
                        .getString("typeId") ?: ""

                    if (type.isEmpty() && navController.currentDestination?.route != Screen.Filter.route) {
                        isBtnVisible = false
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            NavGraph(
                navController = navController,
                onColorChange = { viewModel.onBgColorChanged(it) },
                onTypeIdChange = { typeId = it },
                searchQuery = searchQuery
            )
        }
    }
}