package com.novandiramadhan.petster.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.model.Shelter
import com.novandiramadhan.petster.domain.model.ShelterAuthResult
import com.novandiramadhan.petster.domain.model.ShelterForm
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.domain.model.VolunteerAuthResult
import com.novandiramadhan.petster.domain.model.VolunteerForm
import com.novandiramadhan.petster.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import com.novandiramadhan.petster.R

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): AuthRepository {
    override fun loginVolunteer(email: String, password: String): Flow<Resource<VolunteerAuthResult>> {
        return flow {
            emit(Resource.Loading())
            val response = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = response.user?.uid ?: throw IllegalStateException("Failed to get user ID")

            val userDoc = firestore.collection(FirebaseKeys.VOLUNTEER_COLLECTION)
                .document(userId).get().await()

            val volunteer = if (userDoc.exists()) {
                userDoc.toObject(Volunteer::class.java)?.copy(email = email)
                    ?: Volunteer(uuid = userId, email = email)
            } else {
                Volunteer(uuid = userId, email = email)
            }

            val result = VolunteerAuthResult(
                volunteer = volunteer,
                isLoginType = true
            )

            firestore.collection(FirebaseKeys.VOLUNTEER_COLLECTION)
                .document(userId)
                .set(volunteer, SetOptions.merge())
                .await()

            emit(Resource.Success(result))
        }.catch { e ->
            val errorResource: Resource.Error<VolunteerAuthResult> = when (e) {
                is FirebaseAuthInvalidUserException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_login_invalid_credentials
                    )
                is FirebaseAuthInvalidCredentialsException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_login_invalid_credentials
                    )
                is FirebaseAuthException -> {
                    if (e.cause is IOException) {
                        Resource.Error(
                            message = e.message.toString(),
                            messageResId = R.string.internet_error
                        )
                    } else {
                        val firebaseMessage = e.localizedMessage
                        if (firebaseMessage != null) {
                            Resource.Error(message = firebaseMessage)
                        } else {
                            Resource.Error(
                                message = e.message.toString(),
                                messageResId = R.string.login_volunteer_failed
                            )
                        }
                    }
                }
                is IllegalStateException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_login_internal
                    )
                else -> {
                    val genericMessage = e.localizedMessage
                    if (genericMessage != null) {
                        Resource.Error(message = genericMessage)
                    } else {
                        Resource.Error(
                            message = e.message.toString(),
                            messageResId = R.string.error_unknown
                        )
                    }
                }
            }
            emit(errorResource)
        }
    }

    override fun loginShelter(email: String, password: String): Flow<Resource<ShelterAuthResult>> {
        return flow {
            emit(Resource.Loading())
            val response = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = response.user?.uid ?: throw IllegalStateException("Failed to get user ID")

            val userDoc = firestore.collection(FirebaseKeys.SHELTER_COLLECTION)
                .document(userId).get().await()

            val shelter = if (userDoc.exists()) {
                userDoc.toObject(Shelter::class.java)?.copy(email = email)
                    ?: Shelter(uuid = userId, email = email)
            } else {
                Shelter(uuid = userId, email = email)
            }

            val result = ShelterAuthResult(
                shelter = shelter,
                isLoginType = true
            )

            firestore.collection(FirebaseKeys.SHELTER_COLLECTION)
                .document(userId)
                .set(shelter, SetOptions.merge())
                .await()

            emit(Resource.Success(result))
        }.catch { e ->
            val errorResource: Resource.Error<ShelterAuthResult> = when (e) {
                is FirebaseAuthInvalidUserException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_login_invalid_credentials
                    )
                is FirebaseAuthInvalidCredentialsException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_login_invalid_credentials
                    )
                is FirebaseAuthException -> {
                    if (e.cause is IOException) {
                        Resource.Error(
                            message = e.message.toString(),
                            messageResId = R.string.internet_error
                        )
                    } else {
                        val firebaseMessage = e.localizedMessage
                        if (firebaseMessage != null) {
                            Resource.Error(message = firebaseMessage)
                        } else {
                            Resource.Error(
                                message = e.message.toString(),
                                messageResId = R.string.login_shelter_failed
                            )
                        }
                    }
                }
                is IllegalStateException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_login_internal
                    )
                else -> {
                    val genericMessage = e.localizedMessage
                    if (genericMessage != null) {
                        Resource.Error(message = genericMessage)
                    } else {
                        Resource.Error(
                            message = e.message.toString(),
                            messageResId = R.string.error_unknown
                        )
                    }
                }
            }
            emit(errorResource)
        }
    }

    override fun registerVolunteer(form: VolunteerForm): Flow<Resource<VolunteerAuthResult>> {
        return flow {
            emit(Resource.Loading())
            val response = firebaseAuth.createUserWithEmailAndPassword(form.email, form.password).await()
            val userId = response.user?.uid ?: throw IllegalStateException("Failed to get user ID")

            val volunteerData = Volunteer(
                uuid = userId,
                email = form.email,
                name = form.name,
                phoneNumber = form.phoneNumber,
                address = form.address
            )

            val result = VolunteerAuthResult(
                volunteer = volunteerData,
                isLoginType = false
            )

            firestore.collection(FirebaseKeys.VOLUNTEER_COLLECTION)
                .document(userId).set(volunteerData).await()
            emit(Resource.Success(result))
        }.catch { e -> // Tangkap semua error dari Auth atau Firestore
            val errorResource: Resource.Error<VolunteerAuthResult> = when (e) {
                is FirebaseAuthUserCollisionException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_volunteer_already_exists
                    )
                is FirebaseAuthInvalidCredentialsException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.email_invalid
                    )
                is FirebaseAuthException -> {
                    if (e.cause is IOException) {
                        Resource.Error(
                            message = e.message.toString(),
                            messageResId = R.string.internet_error
                        )
                    } else {
                        Resource.Error(
                            messageResId = R.string.register_volunteer_failed,
                            message = e.message.toString()
                        )
                    }
                }

                is FirebaseFirestoreException ->
                    Resource.Error(
                        messageResId = R.string.register_volunteer_failed,
                        message = e.message.toString()
                    )

                is IllegalStateException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_register_internal
                    )

                else -> {
                    val genericMessage = e.message.toString()
                    Resource.Error(messageResId = R.string.error_unknown, message = genericMessage)
                }
            }
            emit(errorResource)
        }
    }

    override fun registerShelter(form: ShelterForm): Flow<Resource<ShelterAuthResult>> {
        return flow {
            emit(Resource.Loading())
            val response = firebaseAuth.createUserWithEmailAndPassword(form.email, form.password).await()
            val userId = response.user?.uid ?: throw IllegalStateException("Failed to get user ID")

            val shelterData = Shelter(
                uuid = userId,
                email = form.email,
                name = form.name,
                phoneNumber = form.phoneNumber,
                address = form.address
            )

            val result = ShelterAuthResult(
                shelter = shelterData,
                isLoginType = false
            )

            firestore.collection(FirebaseKeys.SHELTER_COLLECTION)
                .document(userId).set(shelterData).await()
            emit(Resource.Success(result))
        }.catch { e -> // Tangkap semua error dari Auth atau Firestore
            val errorResource: Resource.Error<ShelterAuthResult> = when (e) {
                is FirebaseAuthUserCollisionException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_shelter_already_exists
                    )
                is FirebaseAuthInvalidCredentialsException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.email_invalid
                    )
                is FirebaseAuthException -> {
                    if (e.cause is IOException) {
                        Resource.Error(
                            message = e.message.toString(),
                            messageResId = R.string.internet_error
                        )
                    } else {
                        Resource.Error(
                            messageResId = R.string.register_shelter_failed,
                            message = e.message.toString()
                        )
                    }
                }

                is FirebaseFirestoreException ->
                    Resource.Error(
                        messageResId = R.string.register_shelter_failed,
                        message = e.message.toString()
                    )

                is IllegalStateException ->
                    Resource.Error(
                        message = e.message.toString(),
                        messageResId = R.string.error_register_internal
                    )

                else -> {
                    val genericMessage = e.message.toString()
                    Resource.Error(messageResId = R.string.error_unknown, message = genericMessage)
                }
            }
            emit(errorResource)
        }
    }

    override fun deleteVolunteer(id: String): Flow<Resource<Result>> {
        return flow {
            emit(Resource.Loading())
            firestore.collection(FirebaseKeys.VOLUNTEER_COLLECTION)
                .document(id).delete().await()
            val user = firebaseAuth.currentUser
            if (user != null && user.uid == id) {
                user.delete().await()
            } else {
                throw IllegalStateException("User not found or not authorized to delete")
            }
            emit(Resource.Success(Result("Volunteer deleted successfully")))
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown error occurred"))
        }
    }

    override fun deleteShelter(id: String): Flow<Resource<Result>> {
        return flow {
            emit(Resource.Loading())
            firestore.collection(FirebaseKeys.SHELTER_COLLECTION)
                .document(id).delete().await()
            val user = firebaseAuth.currentUser
            if (user != null && user.uid == id) {
                user.delete().await()
            } else {
                throw IllegalStateException("User not found or not authorized to delete")
            }
            emit(Resource.Success(Result("Shelter deleted successfully")))
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown error occurred"))
        }
    }

    override fun changeEmailVolunteer(id: String, email: String): Flow<Resource<Result>> {
        return flow {
            emit(Resource.Loading())
            val user = firebaseAuth.currentUser
            if (user != null && user.uid == id) {
                // (sends verification email)
                user.verifyBeforeUpdateEmail(email).await()
                firestore.collection(FirebaseKeys.VOLUNTEER_COLLECTION)
                    .document(id)
                    .update("email", email)
                    .await()

                emit(Resource.Success(Result("Verification email sent to $email")))
            } else {
                emit(Resource.Error("User not found or not authorized to change email"))
            }
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown error occurred"))
        }
    }

    override fun changeEmailShelter(id: String, email: String): Flow<Resource<Result>> {
        return flow {
            emit(Resource.Loading())
            val user = firebaseAuth.currentUser
            if (user != null && user.uid == id) {
                // (sends verification email)
                user.verifyBeforeUpdateEmail(email).await()
                firestore.collection(FirebaseKeys.SHELTER_COLLECTION)
                    .document(id)
                    .update("email", email)
                    .await()

                emit(Resource.Success(Result("Verification email sent to $email")))
            } else {
                emit(Resource.Error("User not found or not authorized to change email"))
            }
        }.catch {
            emit(Resource.Error(it.message ?: "Unknown error occurred"))
        }
    }

    override fun getUser(
        uuid: String,
        userType: UserType
    ): Flow<Resource<UserResult>> {
        return flow {
            emit(Resource.Loading())
            if (uuid.isBlank()) {
                emit(Resource.Error("User not logged in or UUID is missing."))
                return@flow
            }

            try {
                val collectionPath = when (userType) {
                    UserType.VOLUNTEER -> FirebaseKeys.VOLUNTEER_COLLECTION
                    UserType.SHELTER -> FirebaseKeys.SHELTER_COLLECTION
                    UserType.NONE -> {
                        emit(Resource.Error("Invalid user type: NONE"))
                        return@flow
                    }
                }

                val document = firestore.collection(collectionPath).document(uuid).get().await()

                if (document.exists()) {
                    val userResult: UserResult? = when (userType) {
                        UserType.VOLUNTEER -> document.toObject(Volunteer::class.java)?.let {
                            UserResult.VolunteerResult(it)
                        }
                        UserType.SHELTER -> document.toObject(Shelter::class.java)?.let {
                            UserResult.ShelterResult(it)
                        }
                        else -> null
                    }

                    if (userResult != null) {
                        emit(Resource.Success(userResult))
                    } else {
                        emit(Resource.Error("Failed to parse user data."))
                    }
                } else {
                    emit(Resource.Error("User profile not found"))
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Failed to get user profile", e)
                emit(Resource.Error(e.message ?: "Failed to fetch user profile."))
            }
        }
    }

    override fun updateVolunteer(
        form: VolunteerForm,
        uuid: String
    ): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())

            if (uuid.isBlank()) {
                throw IllegalArgumentException("Volunteer UUID cannot be blank for update.")
            }

            val volunteerDocRef = firestore.collection(FirebaseKeys.VOLUNTEER_COLLECTION)
                .document(uuid)

            val updates = mapOf<String, Any>(
                "name" to form.name,
                "phoneNumber" to form.phoneNumber,
                "address" to form.address
            )

            volunteerDocRef.update(updates).await()

            emit(Resource.Success(Unit))
        }.catch { e ->
            val errorResource: Resource.Error<Unit> = when (e) {
                is FirebaseFirestoreException -> {
                    Resource.Error(
                        messageResId = R.string.update_profile_failed,
                        message = e.message.toString()
                    )
                }
                is IllegalArgumentException -> {
                    Resource.Error(message = e.message ?: "Invalid data provided for update.")
                }
                else -> {
                    Resource.Error(
                        messageResId = R.string.error_unknown,
                        message = e.message.toString()
                    )
                }
            }
            emit(errorResource)
        }
    }

    override fun updateShelter(
        form: ShelterForm,
        uuid: String
    ): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())

            if (uuid.isBlank()) {
                throw IllegalArgumentException("Shelter UUID cannot be blank for update.")
            }

            val shelterDocRef = firestore.collection(FirebaseKeys.SHELTER_COLLECTION)
                .document(uuid)

            val updates = mapOf<String, Any>(
                "name" to form.name,
                "phoneNumber" to form.phoneNumber,
                "address" to form.address
            )

            shelterDocRef.update(updates).await()

            emit(Resource.Success(Unit))
        }.catch { e ->
            val errorResource: Resource.Error<Unit> = when (e) {
                is FirebaseFirestoreException -> {
                    Resource.Error(
                        messageResId = R.string.update_profile_failed,
                        message = e.message.toString()
                    )
                }
                is IllegalArgumentException -> {
                    Resource.Error(message = e.message ?: "Invalid data provided for update.")
                }
                else -> {
                    Resource.Error(
                        messageResId = R.string.error_unknown,
                        message = e.message.toString()
                    )
                }
            }
            emit(errorResource)
        }
    }
}