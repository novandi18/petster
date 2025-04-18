package com.novandiramadhan.petster.common.dummy

import android.content.Context
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.PetImage

class PetDummy(
    val context: Context
) {
    val pets = listOf(
        Pet(
            id = "1",
            name = "Max",
            breed = "Labrador Retriever",
            category = context.resources.getStringArray(R.array.pet_categories)[0], // Dog
            color = context.resources.getStringArray(R.array.pet_colors)[0], // Black
            age = 2,
            gender = "Male",
            weight = "12.5",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[11], context.resources.getStringArray(R.array.pet_behaviors)[1]), // Calm, Friendly
            size = "Medium",
            adoptionFee = 500,
            specialDiet = context.resources.getStringArray(R.array.pet_special_diets)[1], // High Protein
            disabilities = null,
            isFavorite = false,
            isAdopted = false,
            isVaccinated = true,
            volunteer = ""
        ),
        Pet(
            id = "2",
            name = "Luna",
            breed = "Persian",
            category = context.resources.getStringArray(R.array.pet_categories)[1], // Cat
            color = context.resources.getStringArray(R.array.pet_colors)[1], // White
            age = 1,
            gender = "Female",
            weight = "4.0",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[0], context.resources.getStringArray(R.array.pet_behaviors)[10]), // Playful, Shy
            size = "Small",
            adoptionFee = 300,
            specialDiet = null,
            disabilities = listOf(context.resources.getStringArray(R.array.pet_disabilities)[0]), // Blind
            isFavorite = false,
            isAdopted = false,
            isVaccinated = true,
            volunteer = ""
        ),
        Pet(
            id = "3",
            name = "Charlie",
            breed = "Beagle",
            category = context.resources.getStringArray(R.array.pet_categories)[0], // Dog
            color = context.resources.getStringArray(R.array.pet_colors)[2], // Brown
            age = 3,
            gender = "Male",
            weight = "15.0",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[1], context.resources.getStringArray(R.array.pet_behaviors)[13]), // Friendly, Intelligent
            size = "Medium",
            adoptionFee = 550,
            specialDiet = context.resources.getStringArray(R.array.pet_special_diets)[0], // Grain-Free
            disabilities = null,
            isFavorite = false,
            isAdopted = false,
            isVaccinated = false,
            volunteer = ""
        ),
        Pet(
            id = "4",
            name = "Bella",
            breed = "Calico",
            category = context.resources.getStringArray(R.array.pet_categories)[1], // Cat
            color = context.resources.getStringArray(R.array.pet_colors)[10], // Calico
            age = 2,
            gender = "Female",
            weight = "3.5",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[11], context.resources.getStringArray(R.array.pet_behaviors)[13]), // Calm, Intelligent
            size = "Small",
            adoptionFee = 350,
            specialDiet = context.resources.getStringArray(R.array.pet_special_diets)[3], // Allergen-Free
            disabilities = null,
            isFavorite = false,
            isAdopted = false,
            isVaccinated = true,
            volunteer = ""
        ),
        Pet(
            id = "5",
            name = "Rocky",
            breed = "Golden Retriever",
            category = context.resources.getStringArray(R.array.pet_categories)[0], // Dog
            color = context.resources.getStringArray(R.array.pet_colors)[6], // Golden
            age = 4,
            gender = "Male",
            weight = "20.0",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[11], context.resources.getStringArray(R.array.pet_behaviors)[0]), // Calm, Playful
            size = "Large",
            adoptionFee = 600,
            specialDiet = null,
            disabilities = listOf(context.resources.getStringArray(R.array.pet_disabilities)[2]), // Other
            isFavorite = false,
            isAdopted = false,
            isVaccinated = true,
            volunteer = ""
        ),
        Pet(
            id = "6",
            name = "Milo",
            breed = "Tabby",
            category = context.resources.getStringArray(R.array.pet_categories)[1], // Cat
            color = context.resources.getStringArray(R.array.pet_colors)[11], // Tabby
            age = 1,
            gender = "Male",
            weight = "4.2",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[0], context.resources.getStringArray(R.array.pet_behaviors)[1]), // Playful, Friendly
            size = "Small",
            adoptionFee = 320,
            specialDiet = context.resources.getStringArray(R.array.pet_special_diets)[2], // Low Calorie
            disabilities = null,
            isFavorite = false,
            isAdopted = false,
            isVaccinated = false,
            volunteer = ""
        ),
        Pet(
            id = "7",
            name = "Coco",
            breed = "Poodle",
            category = context.resources.getStringArray(R.array.pet_categories)[0], // Dog
            color = context.resources.getStringArray(R.array.pet_colors)[1], // White
            age = 5,
            gender = "Female",
            weight = "18",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[11], context.resources.getStringArray(R.array.pet_behaviors)[13]), // Calm, Intelligent
            size = "Medium",
            adoptionFee = 580,
            specialDiet = null,
            disabilities = listOf(context.resources.getStringArray(R.array.pet_disabilities)[2]), // Paralyzed
            isFavorite = false,
            isAdopted = false,
            isVaccinated = true,
            volunteer = ""
        ),
        Pet(
            id = "8",
            name = "Simba",
            breed = "Maine Coon",
            category = context.resources.getStringArray(R.array.pet_categories)[1], // Cat
            color = context.resources.getStringArray(R.array.pet_colors)[14], // Orange
            age = 2,
            gender = "Male",
            weight = "4.5",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[11], context.resources.getStringArray(R.array.pet_behaviors)[13]), // Calm, Intelligent
            size = "Medium",
            adoptionFee = 340,
            specialDiet = null,
            disabilities = null,
            isFavorite = false,
            isAdopted = false,
            isVaccinated = true,
            volunteer = ""
        ),
        Pet(
            id = "9",
            name = "Lily",
            breed = "Tortoiseshell",
            category = context.resources.getStringArray(R.array.pet_categories)[1], // Cat
            color = context.resources.getStringArray(R.array.pet_colors)[12], // Tortoiseshell
            age = 3,
            gender = "Female",
            weight = "3.8",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[10], context.resources.getStringArray(R.array.pet_behaviors)[11]), // Shy, Calm
            size = "Small",
            adoptionFee = 330,
            specialDiet = context.resources.getStringArray(R.array.pet_special_diets)[3], // Allergen-Free
            disabilities = null,
            isFavorite = false,
            isAdopted = false,
            isVaccinated = false,
            volunteer = ""
        ),
        Pet(
            id = "10",
            name = "Oscar",
            breed = "Bernese Mountain Dog",
            category = context.resources.getStringArray(R.array.pet_categories)[0], // Dog
            color = context.resources.getStringArray(R.array.pet_colors)[18], // Tricolor
            age = 6,
            gender = "Male",
            weight = "22.0",
            image = PetImage(),
            behaviours = listOf(context.resources.getStringArray(R.array.pet_behaviors)[11], context.resources.getStringArray(R.array.pet_behaviors)[1]), // Calm, Friendly
            size = "Large",
            adoptionFee = 620,
            specialDiet = context.resources.getStringArray(R.array.pet_special_diets)[6], // Senior Diet
            disabilities = listOf(context.resources.getStringArray(R.array.pet_disabilities)[14]), // Cognitive Dysfunction
            isFavorite = false,
            isAdopted = false,
            isVaccinated = true,
            volunteer = ""
        )
    )
}
