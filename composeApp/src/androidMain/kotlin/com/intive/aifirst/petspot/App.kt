
package com.intive.aifirst.petspot

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.intive.aifirst.petspot.ui.navigation.MainScaffold

/**
 * Main application composable.
 * Sets up tab navigation with Home tab as primary entry point.
 */
@Composable
fun App() {
    MaterialTheme {
        MainScaffold()
    }
}
