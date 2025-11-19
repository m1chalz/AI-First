package com.intive.aifirst.petspot.features.animallist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListEffect
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListIntent
import com.intive.aifirst.petspot.features.animallist.presentation.viewmodels.AnimalListViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Main screen for displaying list of animals.
 * Follows MVI architecture with ViewModel managing state and effects.
 * 
 * Features:
 * - Scrollable list of animal cards (LazyColumn)
 * - Loading indicator
 * - Error message display
 * - Empty state message
 * - "Report a Missing Animal" button (fixed at bottom)
 * - Reserved space for future search component
 * 
 * Layout per FR-010: This is the primary entry point screen.
 * 
 * @param viewModel ViewModel injected via Koin
 * @param onNavigateToDetails Callback for navigating to animal details (mocked)
 * @param onNavigateToReportMissing Callback for navigating to report missing form (mocked)
 * @param onNavigateToReportFound Callback for navigating to report found form (mocked)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(
    viewModel: AnimalListViewModel = koinViewModel(),
    onNavigateToDetails: (String) -> Unit = {},
    onNavigateToReportMissing: () -> Unit = {},
    onNavigateToReportFound: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Collect effects for navigation
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is AnimalListEffect.NavigateToDetails -> {
                    // Mocked navigation - log for now
                    println("Navigate to details: ${effect.animalId}")
                    onNavigateToDetails(effect.animalId)
                }
                is AnimalListEffect.NavigateToReportMissing -> {
                    println("Navigate to report missing")
                    onNavigateToReportMissing()
                }
                is AnimalListEffect.NavigateToReportFound -> {
                    println("Navigate to report found")
                    onNavigateToReportFound()
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            // Top app bar with title
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Missing animals list",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF2D2D2D) // Primary text color
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFAFAFA) // Background color
                )
            )
        },
        bottomBar = {
            // Fixed button at bottom (per mobile design - single button)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFAFAFA), // Background color
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { viewModel.dispatchIntent(AnimalListIntent.ReportMissing) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("animalList.reportMissingButton"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D2D2D) // Primary button color
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Report a Missing Animal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        },
        containerColor = Color(0xFFFAFAFA) // Background color
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    // Loading indicator
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF2D2D2D)
                    )
                }
                state.error != null -> {
                    // Error message
                    Text(
                        text = "Error: ${state.error}",
                        fontSize = 16.sp,
                        color = Color(0xFFFF0000), // Red for errors
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                    )
                }
                state.isEmpty -> {
                    // Empty state
                    EmptyState()
                }
                else -> {
                    // Animal list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("animalList.list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp) // 8dp gap per Figma
                    ) {
                        // Reserved space for search component (FR-004)
                        item {
                            Spacer(modifier = Modifier.height(56.dp)) // 48-56dp height per spec
                        }
                        
                        // Animal cards
                        items(
                            items = state.animals,
                            key = { animal -> animal.id }
                        ) { animal ->
                            AnimalCard(
                                animal = animal,
                                onClick = {
                                    viewModel.dispatchIntent(AnimalListIntent.SelectAnimal(animal.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

