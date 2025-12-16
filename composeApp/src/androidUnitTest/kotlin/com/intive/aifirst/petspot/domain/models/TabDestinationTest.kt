package com.intive.aifirst.petspot.domain.models

import com.intive.aifirst.petspot.navigation.TabRoute
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit tests for TabDestination enum.
 * Follows Given-When-Then structure.
 */
class TabDestinationTest {
    @Test
    fun `given TabDestination-HOME, when toRoute called, then returns TabRoute-Home`() {
        // given
        val destination = TabDestination.HOME

        // when
        val route = destination.toRoute()

        // then
        assertEquals(TabRoute.Home, route)
    }

    @Test
    fun `given TabDestination-LOST_PET, when toRoute called, then returns TabRoute-LostPet`() {
        // given
        val destination = TabDestination.LOST_PET

        // when
        val route = destination.toRoute()

        // then
        assertEquals(TabRoute.LostPet, route)
    }

    @Test
    fun `given TabDestination-FOUND_PET, when toRoute called, then returns TabRoute-FoundPet`() {
        // given
        val destination = TabDestination.FOUND_PET

        // when
        val route = destination.toRoute()

        // then
        assertEquals(TabRoute.FoundPet, route)
    }

    @Test
    fun `given TabDestination-CONTACT_US, when toRoute called, then returns TabRoute-Contact`() {
        // given
        val destination = TabDestination.CONTACT_US

        // when
        val route = destination.toRoute()

        // then
        assertEquals(TabRoute.Contact, route)
    }

    @Test
    fun `given TabDestination-ACCOUNT, when toRoute called, then returns TabRoute-Account`() {
        // given
        val destination = TabDestination.ACCOUNT

        // when
        val route = destination.toRoute()

        // then
        assertEquals(TabRoute.Account, route)
    }

    @Test
    fun `given TabDestination entries, when ordered, then matches spec order`() {
        // given
        val entries = TabDestination.entries

        // when / then
        assertEquals(TabDestination.HOME, entries[0])
        assertEquals(TabDestination.LOST_PET, entries[1])
        assertEquals(TabDestination.FOUND_PET, entries[2])
        assertEquals(TabDestination.CONTACT_US, entries[3])
        assertEquals(TabDestination.ACCOUNT, entries[4])
    }

    @Test
    fun `given all TabDestination values, when checking count, then has exactly 5 tabs`() {
        // given / when
        val count = TabDestination.entries.size

        // then
        assertEquals(5, count)
    }
}
