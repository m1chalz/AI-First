package com.intive.aifirst.petspot.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.intive.aifirst.petspot.features.lostPetsTeaser.ui.LostPetsTeaser
import com.intive.aifirst.petspot.navigation.NavRoute.AnimalDetail
import com.intive.aifirst.petspot.navigation.navigateToLostPetTab

/**
 * Home screen - main landing page of the app.
 * Implemented as a scrollable LazyColumn containing various components.
 *
 * Currently displays:
 * - Lost Pets Teaser (up to 5 recent lost pets)
 *
 * Future components can be added as items in the LazyColumn.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .testTag("home.container"),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Lost Pets Teaser Component
        item {
            LostPetsTeaser(
                onNavigateToPetDetails = { petId ->
                    // Switch to Lost Pet tab and navigate to pet details
                    navController.navigateToLostPetTab()
                    navController.navigate(AnimalDetail(petId))
                },
                onNavigateToLostPetsList = {
                    // Switch to Lost Pet tab
                    navController.navigateToLostPetTab()
                },
            )
        }

        // Future components can be added here as additional items
        // item { FoundPetsTeaser(...) }
        // item { NewsSection(...) }
        // item { Footer(...) }
    }
}
