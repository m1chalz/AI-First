@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase

package com.intive.aifirst.petspot

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.intive.aifirst.petspot.navigation.PetSpotNavGraph

/**
 * Main application composable.
 * Sets up navigation with AnimalListScreen as primary entry point per FR-010.
 */
@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        PetSpotNavGraph(navController = navController)
    }
}
