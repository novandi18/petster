package com.novandiramadhan.petster.domain.model

import com.novandiramadhan.petster.common.types.UserType

data class AuthState(
    val uuid: String? = null,
    val email: String? = null,
    val userType: UserType = UserType.NONE
)