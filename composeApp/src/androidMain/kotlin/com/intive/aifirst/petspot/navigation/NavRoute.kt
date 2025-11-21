package com.intive.aifirst.petspot.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization.
 * Each route represents a screen in the app.
 * 
 * Uses sealed interface pattern for compile-time safety and exhaustive when checks.
 * Routes are serializable for type-safe argument passing via Navigation Compose.
 */
sealed interface NavRoute {
    
    /**
     * Animal List screen - primary entry point per FR-010.
     * Route: "animal_list"
     */
    @Serializable
    data object AnimalList : NavRoute
    
    /**
     * Animal Detail screen - shows details for specific animal.
     * Route: "animal_detail/{animalId}"
     * 
     * @param animalId ID of the animal to display
     */
    @Serializable
    data class AnimalDetail(val animalId: String) : NavRoute
    
    /**
     * Report Missing Animal screen - form to report missing pet.
     * Route: "report_missing"
     */
    @Serializable
    data object ReportMissing : NavRoute
    
    /**
     * Report Found Animal screen - form to report found pet.
     * Route: "report_found"
     */
    @Serializable
    data object ReportFound : NavRoute
}

