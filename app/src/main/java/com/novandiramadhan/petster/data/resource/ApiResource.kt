package com.novandiramadhan.petster.data.resource

sealed class ApiResource<out R> {
    data class Success<out T>(val data: T) : ApiResource<T>()
    data class Error(val errorMessage: String) : ApiResource<Nothing>()
    data object Empty : ApiResource<Nothing>()
}