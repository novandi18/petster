package com.novandiramadhan.petster.presentation.navigation

import com.novandiramadhan.petster.domain.model.Volunteer
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
    data class VolunteerMapsUpdate(
        val volunteer: Volunteer
    ): Destinations(false)

    @Serializable
    data object Community: Destinations(true)

    @Serializable
    data object Assistant: Destinations(true)

    @Serializable
    data class CommunityPost(
        val postId: String
    ): Destinations(false)

    @Serializable
    data object CommunityNewPost: Destinations(false)

    @Serializable
    data class CommunityUpdatePost(
        val postId: String,
        val content: String
    ): Destinations(false)

    companion object {
        val allDestinations = listOf(Welcome, Home, Profile, Settings, Favorite, YourPets,
            Explore, Assistant, Community)
    }
}