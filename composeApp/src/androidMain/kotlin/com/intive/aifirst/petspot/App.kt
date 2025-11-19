package com.intive.aifirst.petspot

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.intive.aifirst.petspot.features.animallist.ui.AnimalListScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Main application composable.
 * Sets AnimalListScreen as the primary entry point per FR-010.
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        AnimalListScreen()
    }
}