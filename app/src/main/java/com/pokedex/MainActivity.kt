package com.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.pokedex.ui.screens.NavGraphs
import com.pokedex.ui.theme.PokemonAppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PokemonAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val navHostEngine = rememberNavHostEngine(
                        rootDefaultAnimations = RootNavGraphDefaultAnimations(
                            enterTransition = { fadeIn(animationSpec = tween(400)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) }
                        ))

                    DestinationsNavHost(navGraph = NavGraphs.root, engine = navHostEngine)
                }
            }
        }
    }
}


