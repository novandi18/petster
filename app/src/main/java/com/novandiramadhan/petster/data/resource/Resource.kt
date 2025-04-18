package com.novandiramadhan.petster.data.resource

sealed class Resource<T>(val data: T? = null, val message: String? = null, val messageResId: Int? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(
        message: String, messageResId: Int? = null, data: T? = null
    ) : Resource<T>(data, message, messageResId)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}