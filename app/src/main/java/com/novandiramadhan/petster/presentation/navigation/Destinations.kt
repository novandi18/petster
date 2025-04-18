package com.novandiramadhan.petster.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Destinations(val showBottomBar: Boolean) {
    @Serializable
    data object Welcome: Destinations(false)

    @Serializable
    data object Home: Destinations(true)

    @Serializable
    data object Profile: Destinations(false)

    @Serializable
    data object Settings: Destinations(false)

    @Serializable
    data object Favorite: Destinations(true)

    @Serializable
    data object Notification: Destinations(true)

    @Serializable
    data class PetDetail(
        val petId: String
    ): Destinations(false)

    @Serializable
    data object VolunteerConnect: Destinations(false)

    @Serializable
    data object ShelterConnect: Destinations(false)

    @Serializable
    data object YourPets: Destinations(true)

    @Serializable
    data object NewPet: Destinations(false)

    @Serializable
    data object Explore: Destinations(true)

    @Serializable
    data class UpdatePet(
        val petId: String
    ): Destinations(false)

    @Serializable
    data object Article: Destinations(true)

    @Serializable
    data class ArticleDetail(
        val id: Int
    ): Destinations(false)

    @Serializable
    data object Assistant: Destinations(true)

    companion object {
        val allDestinations = listOf(Welcome, Home, Profile, Settings, Favorite, Notification, YourPets,
            Explore, Article, Assistant)
    }
}