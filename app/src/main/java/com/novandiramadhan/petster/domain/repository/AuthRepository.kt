package com.novandiramadhan.petster.domain.repository

import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.model.ShelterAuthResult
import com.novandiramadhan.petster.domain.model.ShelterForm
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.domain.model.VolunteerAuthResult
import com.novandiramadhan.petster.domain.model.VolunteerForm
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginVolunteer(email: String, password: String): Flow<Resource<VolunteerAuthResult>>
    fun loginShelter(email: String, password: String): Flow<Resource<ShelterAuthResult>>
    fun registerVolunteer(form: VolunteerForm): Flow<Resource<VolunteerAuthResult>>
    fun registerShelter(form: ShelterForm): Flow<Resource<ShelterAuthResult>>
    fun deleteVolunteer(id: String): Flow<Resource<Result>>
    fun deleteShelter(id: String): Flow<Resource<Result>>
    fun changeEmailVolunteer(id: String, email: String): Flow<Resource<Result>>
    fun changeEmailShelter(id: String, email: String): Flow<Resource<Result>>
    fun getUser(uuid: String, userType: UserType): Flow<Resource<UserResult>>
    fun updateVolunteer(form: VolunteerForm, uuid: String): Flow<Resource<Unit>>
    fun updateShelter(form: ShelterForm, uuid: String): Flow<Resource<Unit>>
}