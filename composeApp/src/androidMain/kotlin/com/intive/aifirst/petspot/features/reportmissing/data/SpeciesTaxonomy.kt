package com.intive.aifirst.petspot.features.reportmissing.data

/**
 * Represents a selectable animal species option.
 *
 * @property id Unique identifier matching backend/web format (uppercase)
 * @property displayName Human-readable name for UI display
 */
data class SpeciesTaxonomyOption(
    val id: String,
    val displayName: String,
)

/**
 * Bundled static taxonomy of animal species.
 * No network dependency - aligned with Web platform's ANIMAL_SPECIES constant.
 */
object SpeciesTaxonomy {
    val species: List<SpeciesTaxonomyOption> =
        listOf(
            SpeciesTaxonomyOption("DOG", "Dog"),
            SpeciesTaxonomyOption("CAT", "Cat"),
            SpeciesTaxonomyOption("BIRD", "Bird"),
            SpeciesTaxonomyOption("RABBIT", "Rabbit"),
            SpeciesTaxonomyOption("OTHER", "Other"),
        )
}
