package com.novandiramadhan.petster.common.utils

import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.Shelter
import com.novandiramadhan.petster.domain.model.Volunteer
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WhatsappUtil {
    fun generateWhatsAppMessage(pet: Pet, shelter: Shelter, volunteer: Volunteer): String {
        val petName = pet.name ?: "Hewan ini"
        val shelterName = shelter.name ?: "Shelter"
        val volunteerName = volunteer.name ?: ""
        val shelterPhone = shelter.phoneNumber?.let { "($it)" } ?: ""
        val petCategory = pet.category ?: "N/A"

        val validPetBreed = pet.breed?.takeIf { it.isNotBlank() }
        val typeBreedLine = if (validPetBreed != null) {
            "* Jenis/Ras: $petCategory / $validPetBreed"
        } else {
            "* Jenis: $petCategory"
        }

        val message = """
    Halo Kak $volunteerName,

    Perkenalkan, saya $shelterName. Saya menemukan profil hewan adopsi ini melalui aplikasi Petster dan sangat tertarik.

    Detail Hewan:
    * Nama: $petName
    $typeBreedLine
    Apakah $petName saat ini masih tersedia untuk diadopsi?

    Mohon informasinya ya. Terima kasih banyak!

    Salam hangat,
    $shelterName
    $shelterPhone
    """.trimIndent()

        return message
    }

    fun createWhatsAppUrl(volunteerPhoneNumber: String?, message: String): String? {
        var cleanedNumber = volunteerPhoneNumber?.replace(Regex("[\\s+]"), "")
        if (cleanedNumber?.startsWith("0") == true) {
            cleanedNumber = "62" + cleanedNumber.substring(1)
        }

        if (cleanedNumber.isNullOrBlank()) return null

        val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
        return "https://api.whatsapp.com/send?phone=$cleanedNumber&text=$encodedMessage"
    }
}