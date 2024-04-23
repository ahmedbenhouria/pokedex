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

    val state by viewModel.state.collectAsStateWithLifecycle()

    val bgColor by animateColorAsState(
        targetValue = state.bgColor,
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

    if (state.searchQuery.isNotEmpty()) {
        if (navController.currentDestination?.route == Screen.Filter.route) {
            LaunchedEffect(Unit) {
                delay(100)
                navController.popBackStack()
                viewModel.onEvent(ScaffoldUiEvent.BackBtnVisibility(false))
            }
        }
    }

    if (navController.currentDestination?.route == Screen.PokemonDetails.route) {
        viewModel.onEvent(ScaffoldUiEvent.BackBtnVisibility(true))
    }

    Scaffold(
        containerColor = bgColor,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            CustomTopAppBar(
                searchQuery = state.searchQuery,
                isVisible = state.isBtnVisible,
                onSearchQueryChange = {
                    viewModel.onEvent(ScaffoldUiEvent.SearchQueryChanged(it))
                },
                onSearchBarClick = {
                    if (state.typeId.isEmpty()) {
                        when (navController.currentDestination?.route) {
                            Screen.PokemonList.route -> {
                                viewModel.onEvent(ScaffoldUiEvent.BackBtnVisibility(it))
                                navController.navigate(Screen.Filter.route)
                            }
                            Screen.PokemonDetails.route -> {
                                viewModel.onEvent(ScaffoldUiEvent.BackBtnVisibility(false))
                                viewModel.onEvent(ScaffoldUiEvent.BgColorChanged(Color.White))
                                navController.popBackStack()
                            }
                        }
                    }

                },
                onBackBtnClick = {
                    viewModel.onEvent(ScaffoldUiEvent.BgColorChanged(Color.White))
                    viewModel.onEvent(ScaffoldUiEvent.SearchQueryChanged(""))
                    navController.popBackStack()

                    val type = navController
                        .currentBackStackEntry!!
                        .arguments!!
                        .getString("typeId") ?: ""

                    if (type.isEmpty() && navController.currentDestination?.route != Screen.Filter.route) {
                        viewModel.onEvent(ScaffoldUiEvent.BackBtnVisibility(false))
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
                onColorChange = {
                    viewModel.onEvent(ScaffoldUiEvent.BgColorChanged(it))
                },
                onTypeIdChange = {
                    viewModel.onEvent(ScaffoldUiEvent.PokemonTypeIdChanged(it))
                },
                searchQuery = state.searchQuery
            )
        }
    }
}