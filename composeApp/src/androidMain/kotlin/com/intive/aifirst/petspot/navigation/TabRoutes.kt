package com.intive.aifirst.petspot.navigation

import kotlinx.serialization.Serializable

/**
 * Top-level tab routes for bottom navigation.
 * Each tab is a sealed interface that can contain nested routes.
 */
@Serializable
sealed interface TabRoute {
    @Serializable
    data object Home : TabRoute

    @Serializable
    data object LostPet : TabRoute

    @Serializable
    data object FoundPet : TabRoute

    @Serializable
    data object Contact : TabRoute

    @Serializable
    data object Account : TabRoute
}

/**
 * Nested routes within Home tab.
 */
@Serializable
sealed interface HomeRoute {
    @Serializable
    data object Root : HomeRoute
}

/**
 * Nested routes within Lost Pet tab.
 */
@Serializable
sealed interface LostPetRoute {
    @Serializable
    data object List : LostPetRoute
}

/**
 * Nested routes within Found Pet tab.
 */
@Serializable
sealed interface FoundPetRoute {
    @Serializable
    data object List : FoundPetRoute
}

/**
 * Nested routes within Contact Us tab.
 */
@Serializable
sealed interface ContactRoute {
    @Serializable
    data object Root : ContactRoute
}

/**
 * Nested routes within Account tab.
 */
@Serializable
sealed interface AccountRoute {
    @Serializable
    data object Root : AccountRoute
}
