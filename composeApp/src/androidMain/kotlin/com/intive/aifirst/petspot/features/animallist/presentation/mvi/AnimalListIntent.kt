package com.intive.aifirst.petspot.features.animallist.presentation.mvi

/**
 * Sealed interface for user intents in Animal List screen.
 * Represents all possible user actions in the MVI loop.
 */
sealed interface AnimalListIntent {
    /**
     * User wants to refresh the animal list.
     */
    data object Refresh : AnimalListIntent
    
    /**
     * User selected an animal card to view details.
     */
    data class SelectAnimal(val id: String) : AnimalListIntent
    
    /**
     * User tapped "Report a Missing Animal" button.
     */
    data object ReportMissing : AnimalListIntent
    
    /**
     * User tapped "Report Found Animal" button.
     */
    data object ReportFound : AnimalListIntent
}

