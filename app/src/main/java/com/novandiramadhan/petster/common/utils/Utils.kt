package com.novandiramadhan.petster.common.utils

import android.location.LocationManager
import android.util.Patterns
import java.text.NumberFormat
import java.util.Locale

fun Int.toRupiah(): String {
    return "Rp${String.format(Locale("id", "ID"), "%,d", this).replace(',', '.')}"
}

fun validateEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validatePassword(password: String): Boolean {
    return password.length >= 6
}

fun validatePhoneNumber(phoneNumber: String): Boolean {
    val phoneRegex = Regex("^08\\d{8,11}$")
    return phoneRegex.matches(phoneNumber)
}

fun formatRupiah(value: String): String {
    if (value.isEmpty()) return ""

    val number = value.toLongOrNull() ?: 0L
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    formatter.maximumFractionDigits = 0
    return formatter.format(number).replace("Rp", "Rp ")
}

fun rupiahToNumber(rupiahString: String): Long {
    val numericString = rupiahString.replace(Regex("\\D"), "")
    return numericString.toLongOrNull() ?: 0L
}

fun formatIbbUrl(originalUrl: String): String {
    return originalUrl.replace(".co/", ".co.com/")
}

fun isLocationEnabled(locationManager: LocationManager): Boolean {
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}