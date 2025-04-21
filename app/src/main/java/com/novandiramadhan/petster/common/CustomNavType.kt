package com.novandiramadhan.petster.common

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.novandiramadhan.petster.domain.model.Volunteer
import kotlinx.serialization.json.Json

object CustomNavType {
    val VolunteerType = object : NavType<Volunteer>(
        isNullableAllowed = false
    ) {
        override fun get(
            bundle: Bundle,
            key: String
        ): Volunteer? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): Volunteer {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: Volunteer): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(
            bundle: Bundle,
            key: String,
            value: Volunteer
        ) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}