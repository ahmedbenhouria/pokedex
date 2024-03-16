package com.pokedex.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pokedex.presentation.pokemonList.PokemonListScreen
import com.pokedex.presentation.searchByType.SearchByTypeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    onColorChange: (Color) -> Unit,
    onTypeIdChange: (String) -> Unit,
    searchQuery: String
) {
    NavHost(
        navController = navController,
        startDestination = Routes.POKEMON_LIST + "?typeId={typeId}",
        enterTransition = {
            fadeIn(animationSpec = tween(400)).plus(
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(400),
                    initialOffset = { 400 }
                ))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)).plus(
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(400),
                    targetOffset = { 400 }
                ))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(400)).plus(
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(400),
                    initialOffset = { 400 }
                ))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)).plus(
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(400),
                    targetOffset = { 400 }
                ))
        }
    ) {
        composable(
            route = Routes.POKEMON_LIST + "?typeId={typeId}",
            arguments = listOf(
                navArgument(name = "typeId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            BackHandler(true) {}

            val typeId = backStackEntry.arguments?.getString("typeId")
            onTypeIdChange(typeId!!)

            PokemonListScreen(
                onColorChange = { onColorChange(it) },
                searchQuery = searchQuery
            )
        }

        composable(Routes.SEARCH_BY_TYPE) {
            BackHandler(true) {}
            SearchByTypeScreen { typeId ->
                navController.navigate(Routes.POKEMON_LIST + "?typeId=${typeId}")
            }
        }

    }
}