package com.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.pokedex.presentation.pokemonList.NavGraphs
import com.pokedex.presentation.pokemonList.PokemonListRoot
import com.pokedex.presentation.pokemonList.PokemonViewModel
import com.pokedex.presentation.pokemonList.navDestination
import com.pokedex.ui.theme.PokedexAppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.utils.allDestinations
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PokedexAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val parentViewModel = hiltViewModel<PokemonViewModel>(this@MainActivity)
                    PokemonListRoot(viewModel = parentViewModel)
                 /*   val rootNavController = rememberNavController()

                    DestinationsNavHost(
                        navController = rootNavController,
                        navGraph = NavGraphs.root,
                        dependenciesContainerBuilder = {
                            val navDestination = navBackStackEntry.navDestination ?: return@DestinationsNavHost
                            if (NavGraphs.root.allDestinations.contains(navDestination)) {
                                val parentEntry = remember(navBackStackEntry) {
                                    navController.getBackStackEntry(NavGraphs.root.route)
                                }
                                val parentViewModel = hiltViewModel<PokemonViewModel>(parentEntry)
                                dependency(parentViewModel)
                            }
                        }
                    )*/
                }
            }
        }
    }
}


