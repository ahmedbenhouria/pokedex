package com.pokedex.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pokedex.presentation.pokemonDetails.PokemonDetailsScreen
import com.pokedex.presentation.pokemonList.PokemonListScreen
import com.pokedex.presentation.filterPokemons.FilterScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    onColorChange: (Color) -> Unit,
    onTypeIdChange: (String) -> Unit,
    searchQuery: String
) {
    val localFocusManager = LocalFocusManager.current
    var pokemonByTypeListIds  by remember { mutableStateOf(emptyList<String>()) }

    NavHost(
        navController = navController,
        startDestination = Screen.PokemonList.route,
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
            route = Screen.PokemonList.route,
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
                navController = navController,
                onColorChange = { onColorChange(it) },
                searchQuery = searchQuery,
                onItemClick = { pokemonId, listIds ->
                    localFocusManager.clearFocus()
                    pokemonByTypeListIds = listIds
                    navController.navigate(Screen.PokemonDetails.passPokemonId(pokemonId))
                }
            )
        }

        composable(Screen.Filter.route) {
            BackHandler(true) {}
            onColorChange(Color.White)

            FilterScreen { typeId ->
                navController.navigate(Screen.PokemonList.passTypeId(typeId))
            }
        }

        composable(
            route = Screen.PokemonDetails.route,
            arguments = listOf(
                navArgument(name = "pokemonId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(
                        durationMillis = 280,
                        easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = LinearEasing
                    )
                )
            }
        ) { backStackEntry ->
            BackHandler(true) {}
            val pokemonId = backStackEntry.arguments?.getString("pokemonId")!!

            PokemonDetailsScreen(
                onNavigate = {
                    when (it) {
                        "previous" -> {
                            val previousIndex = if (pokemonByTypeListIds.isEmpty()) {
                                (pokemonId.toInt() - 1).coerceAtLeast(1).toString()
                            } else {
                                val index = pokemonByTypeListIds.indexOf(pokemonByTypeListIds.find { id -> id == pokemonId })
                                pokemonByTypeListIds.getOrNull(index - 1) ?: index.toString()
                            }

                            navController.navigate(Screen.PokemonDetails.passPokemonId(previousIndex)) {
                                popUpTo(Screen.PokemonDetails.route) {
                                    inclusive = true
                                }
                            }
                        }
                        "next" -> {
                            val nextIndex = if (pokemonByTypeListIds.isEmpty()) {
                                (pokemonId.toInt() + 1).toString()
                            } else {
                                val index = pokemonByTypeListIds.indexOf(pokemonByTypeListIds.find { id -> id == pokemonId })
                                pokemonByTypeListIds.getOrNull(index + 1) ?: index.toString()
                            }
                            navController.navigate(Screen.PokemonDetails.passPokemonId(nextIndex)) {
                                popUpTo(Screen.PokemonDetails.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                },
                onColorChange = {
                    onColorChange(it)
                }
            )
        }
    }
}