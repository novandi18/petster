package com.novandiramadhan.petster.domain.interactor

import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.model.ShelterAuthResult
import com.novandiramadhan.petster.domain.model.ShelterForm
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.domain.model.VolunteerAuthResult
import com.novandiramadhan.petster.domain.model.VolunteerForm
import com.novandiramadhan.petster.domain.model.VolunteerLocation
import com.novandiramadhan.petster.domain.repository.AuthRepository
import com.novandiramadhan.petster.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthInteractor @Inject constructor(
    private val authRepository: AuthRepository
): AuthUseCase {
    override fun loginVolunteer(
        email: String,
        password: String
    ): Flow<Resource<VolunteerAuthResult>> = authRepository.loginVolunteer(email, password)

    override fun loginShelter(
        email: String,
        password: String
    ): Flow<Resource<ShelterAuthResult>> = authRepository.loginShelter(email, password)

    override fun registerVolunteer(
        form: VolunteerForm
    ): Flow<Resource<VolunteerAuthResult>> = authRepository.registerVolunteer(form)

    override fun registerShelter(
        form: ShelterForm
    ): Flow<Resource<ShelterAuthResult>> = authRepository.registerShelter(form)

    override fun deleteVolunteer(id: String): Flow<Resource<Result>> =
        authRepository.deleteVolunteer(id)

    override fun deleteShelter(id: String): Flow<Resource<Result>> =
        authRepository.deleteShelter(id)

    override fun changeEmailVolunteer(id: String, email: String): Flow<Resource<Result>> =
        authRepository.changeEmailVolunteer(id, email)

    override fun changeEmailShelter(id: String, email: String): Flow<Resource<Result>> =
        authRepository.changeEmailShelter(id, email)

    override fun getUser(
        uuid: String,
        userType: UserType
    ): Flow<Resource<UserResult>> = authRepository.getUser(uuid, userType)

    override fun updateVolunteer(
        form: VolunteerForm,
        uuid: String
    ): Flow<Resource<Unit>> = authRepository.updateVolunteer(form, uuid)

    override fun updateShelter(
        form: ShelterForm,
        uuid: String
    ): Flow<Resource<Unit>> = authRepository.updateShelter(form, uuid)

    override fun updateVolunteerLocation(
        uuid: String,
        location: VolunteerLocation
    ): Flow<Resource<Unit>> = authRepository.updateVolunteerLocation(uuid, location)

    override fun reAuthenticate(password: String): Flow<Resource<Result>> =
        authRepository.reAuthenticate(password)
}