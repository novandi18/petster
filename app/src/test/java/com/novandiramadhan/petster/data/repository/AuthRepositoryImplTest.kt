package com.novandiramadhan.petster.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.ShelterForm
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.domain.model.VolunteerAuthResult
import com.novandiramadhan.petster.domain.model.VolunteerForm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class AuthRepositoryImplTest {
    @Mock
    private lateinit var mockFirebaseAuth: FirebaseAuth

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockAuthResult: AuthResult

    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser

    @Mock
    private lateinit var mockCollectionReference: CollectionReference

    @Mock
    private lateinit var mockDocumentReference: DocumentReference

    private lateinit var authRepository: AuthRepositoryImpl

    private val testEmail = "register.success@example.com"
    private val testPassword = "password123"
    private val testUserId = "registerSuccessUser123"
    private val testVolunteerForm = VolunteerForm(
        email = testEmail,
        password = testPassword,
        name = "Register Success User",
        phoneNumber = "1234567890",
        address = "123 Success Street"
    )

    @BeforeEach
    fun setUp() {
        authRepository = AuthRepositoryImpl(mockFirebaseAuth, mockFirestore)
    }

    @Nested
    @DisplayName("Register Volunteer Tests")
    inner class RegisterVolunteerTests {

        @Test
        @DisplayName("registerVolunteer - Success - Should emit Loading then Success")
        fun registerVolunteer_success_emitsLoadingAndSuccess() = runTest {
            whenever(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
            whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)

            whenever(mockFirebaseUser.uid).thenReturn(testUserId)
            whenever(mockAuthResult.user).thenReturn(mockFirebaseUser)
            whenever(mockFirebaseAuth.createUserWithEmailAndPassword(testVolunteerForm.email, testVolunteerForm.password))
                .thenReturn(Tasks.forResult(mockAuthResult))
            whenever(mockDocumentReference.set(any<Volunteer>()))
                .thenReturn(Tasks.forResult(null as Void?))

            val emissions = authRepository.registerVolunteer(testVolunteerForm).toList()

            assertEquals(2, emissions.size, "Should emit Loading and Success")
            assertTrue(emissions[0] is Resource.Loading, "First emission should be Loading")
            assertTrue(emissions[1] is Resource.Success, "Second emission should be Success")

            val successResult = emissions[1] as Resource.Success<VolunteerAuthResult>
            assertNotNull(successResult.data, "Success data should not be null")

            val resultData = successResult.data
            val registeredVolunteer = resultData.volunteer

            assertEquals(testUserId, registeredVolunteer?.uuid, "Volunteer UUID should match")
            assertEquals(testVolunteerForm.email, registeredVolunteer?.email, "Volunteer email should match form")
            assertEquals(testVolunteerForm.name, registeredVolunteer?.name, "Volunteer name should match form")
            assertEquals(testVolunteerForm.phoneNumber, registeredVolunteer?.phoneNumber, "Volunteer phone should match form")
            assertEquals(testVolunteerForm.address, registeredVolunteer?.address, "Volunteer address should match form")
            assertFalse(resultData.isLoginType, "isLoginType should be false for registration")

            verify(mockFirebaseAuth).createUserWithEmailAndPassword(testVolunteerForm.email, testVolunteerForm.password)
            verify(mockFirestore).collection(FirebaseKeys.VOLUNTEER_COLLECTION)
            verify(mockCollectionReference).document(testUserId)
            verify(mockDocumentReference).set(argThat { vol: Volunteer ->
                vol.uuid == testUserId &&
                        vol.email == testVolunteerForm.email &&
                        vol.name == testVolunteerForm.name &&
                        vol.phoneNumber == testVolunteerForm.phoneNumber &&
                        vol.address == testVolunteerForm.address
            })
            verifyNoMoreInteractions(mockFirebaseAuth, mockFirestore, mockCollectionReference, mockDocumentReference)
        }

        @Test
        @DisplayName("registerVolunteer - Invalid Email - Should emit Loading then Error")
        fun registerVolunteer_invalidEmail_emitsLoadingAndError() = runTest {
            val exception = mock<FirebaseAuthInvalidCredentialsException> {
                on { message } doReturn "The email address is badly formatted."
            }

            whenever(mockFirebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword))
                .thenReturn(Tasks.forException(exception))

            val emissions = authRepository.registerVolunteer(testVolunteerForm).toList()

            assertEquals(2, emissions.size, "Should emit Loading and Error")
            assertTrue(emissions[0] is Resource.Loading, "First emission should be Loading")
            assertTrue(emissions[1] is Resource.Error, "Second emission should be Error")

            val errorResult = emissions[1] as Resource.Error<VolunteerAuthResult>
            assertEquals(exception.message, errorResult.message, "Error message should match exception message")
            assertNotNull(errorResult.messageResId, "Error should have a resource ID")
            assertEquals(R.string.email_invalid, errorResult.messageResId, "Error resource ID should be for invalid email")

            verify(mockFirebaseAuth).createUserWithEmailAndPassword(testEmail, testPassword)
            verifyNoInteractions(mockFirestore)
        }

        @Test
        @DisplayName("registerVolunteer - Null User - Should emit Loading then Error")
        fun registerVolunteer_nullUser_emitsLoadingAndError() = runTest {
            val authResultWithNullUser = mock<AuthResult> {
                on { user } doReturn null
            }

            whenever(mockFirebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword))
                .thenReturn(Tasks.forResult(authResultWithNullUser))

            val emissions = authRepository.registerVolunteer(testVolunteerForm).toList()

            assertEquals(2, emissions.size, "Should emit Loading and Error")
            assertTrue(emissions[0] is Resource.Loading, "First emission should be Loading")
            assertTrue(emissions[1] is Resource.Error, "Second emission should be Error")

            val errorResult = emissions[1] as Resource.Error<VolunteerAuthResult>
            assertEquals("Failed to get user ID", errorResult.message, "Error message should match expected message")
            assertEquals(R.string.error_register_internal, errorResult.messageResId, "Error resource ID should be for internal error")

            verify(mockFirebaseAuth).createUserWithEmailAndPassword(testEmail, testPassword)
            verifyNoInteractions(mockFirestore)
        }
    }

    @Nested
    @DisplayName("Login Volunteer Tests")
    inner class LoginVolunteerTests {

        @Test
        @DisplayName("loginVolunteer - Success - Should emit Loading then Success")
        fun loginVolunteer_success_emitsLoadingAndSuccess() = runTest {
            val documentSnapshot = mock<com.google.firebase.firestore.DocumentSnapshot> {
                on { exists() } doReturn true
                on { toObject(Volunteer::class.java) } doReturn Volunteer(
                    uuid = testUserId,
                    name = "Test Volunteer",
                    phoneNumber = "1234567890",
                    address = "123 Test Street"
                )
            }

            whenever(mockFirebaseUser.uid).thenReturn(testUserId)
            whenever(mockAuthResult.user).thenReturn(mockFirebaseUser)
            whenever(mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword))
                .thenReturn(Tasks.forResult(mockAuthResult))
            whenever(mockFirestore.collection(FirebaseKeys.VOLUNTEER_COLLECTION)).thenReturn(mockCollectionReference)
            whenever(mockCollectionReference.document(testUserId)).thenReturn(mockDocumentReference)
            whenever(mockDocumentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
            whenever(mockDocumentReference.set(any(), any<com.google.firebase.firestore.SetOptions>()))
                .thenReturn(Tasks.forResult(null))

            val emissions = authRepository.loginVolunteer(testEmail, testPassword).toList()

            assertEquals(2, emissions.size, "Should emit Loading and Success")
            assertTrue(emissions[0] is Resource.Loading, "First emission should be Loading")
            assertTrue(emissions[1] is Resource.Success, "Second emission should be Success")

            val successResult = emissions[1] as Resource.Success<VolunteerAuthResult>
            val volunteerResult = successResult.data?.volunteer

            assertEquals(testUserId, volunteerResult?.uuid, "UUID should match")
            assertEquals(testEmail, volunteerResult?.email, "Email should be updated to login email")
            assertEquals("Test Volunteer", volunteerResult?.name, "Name should match retrieved data")
            assertTrue(successResult.data?.isLoginType == true, "isLoginType should be true for login")

            verify(mockFirebaseAuth).signInWithEmailAndPassword(testEmail, testPassword)
            verify(mockFirestore, org.mockito.Mockito.times(2)).collection(FirebaseKeys.VOLUNTEER_COLLECTION)
            verify(mockCollectionReference, org.mockito.Mockito.times(2)).document(testUserId)
            verify(mockDocumentReference).get()
            verify(mockDocumentReference).set(any(), any<com.google.firebase.firestore.SetOptions>())
        }

        @Test
        @DisplayName("loginVolunteer - Invalid Credentials - Should emit Loading then Error")
        fun loginVolunteer_invalidCredentials_emitsLoadingAndError() = runTest {
            val exception = mock<FirebaseAuthInvalidCredentialsException> {
                on { message } doReturn "The password is invalid or the user does not have a password."
            }

            whenever(mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword))
                .thenReturn(Tasks.forException(exception))

            val emissions = authRepository.loginVolunteer(testEmail, testPassword).toList()

            assertEquals(2, emissions.size, "Should emit Loading and Error")
            assertTrue(emissions[0] is Resource.Loading, "First emission should be Loading")
            assertTrue(emissions[1] is Resource.Error, "Second emission should be Error")

            val errorResult = emissions[1] as Resource.Error<VolunteerAuthResult>
            assertEquals(exception.message, errorResult.message, "Error message should match exception message")
            assertEquals(R.string.error_login_invalid_credentials, errorResult.messageResId,
                "Error resource ID should be for invalid credentials")

            verify(mockFirebaseAuth).signInWithEmailAndPassword(testEmail, testPassword)
            verifyNoInteractions(mockFirestore)
        }
    }

    @Nested
    @DisplayName("Register Shelter Tests")
    inner class RegisterShelterTests {

        private val testShelterForm = ShelterForm(
            email = testEmail,
            password = testPassword,
            name = "Test Shelter",
            phoneNumber = "1234567890",
            address = "456 Shelter Street"
        )

        @Test
        @DisplayName("registerShelter - Success - Should emit Loading then Success")
        fun registerShelter_success_emitsLoadingAndSuccess() = runTest {
            whenever(mockFirestore.collection(FirebaseKeys.SHELTER_COLLECTION)).thenReturn(mockCollectionReference)
            whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
            whenever(mockFirebaseUser.uid).thenReturn(testUserId)
            whenever(mockAuthResult.user).thenReturn(mockFirebaseUser)
            whenever(mockFirebaseAuth.createUserWithEmailAndPassword(testShelterForm.email, testShelterForm.password))
                .thenReturn(Tasks.forResult(mockAuthResult))
            whenever(mockDocumentReference.set(any()))
                .thenReturn(Tasks.forResult(null as Void?))

            val emissions = authRepository.registerShelter(testShelterForm).toList()

            assertEquals(2, emissions.size, "Should emit Loading and Success")
            assertTrue(emissions[0] is Resource.Loading, "First emission should be Loading")
            assertTrue(emissions[1] is Resource.Success, "Second emission should be Success")

            val successResult = emissions[1] as Resource.Success
            assertNotNull(successResult.data, "Success data should not be null")

            val resultData = successResult.data
            val registeredShelter = resultData.shelter

            assertEquals(testUserId, registeredShelter?.uuid, "Shelter UUID should match")
            assertEquals(testShelterForm.email, registeredShelter?.email, "Shelter email should match form")
            assertEquals(testShelterForm.name, registeredShelter?.name, "Shelter name should match form")
            assertEquals(testShelterForm.phoneNumber, registeredShelter?.phoneNumber, "Shelter phone should match form")
            assertEquals(testShelterForm.address, registeredShelter?.address, "Shelter address should match form")
            assertFalse(resultData.isLoginType, "isLoginType should be false for registration")

            verify(mockFirebaseAuth).createUserWithEmailAndPassword(testShelterForm.email, testShelterForm.password)
            verify(mockFirestore).collection(FirebaseKeys.SHELTER_COLLECTION)
            verify(mockCollectionReference).document(testUserId)
            verify(mockDocumentReference).set(any())
            verifyNoMoreInteractions(mockFirebaseAuth, mockFirestore, mockCollectionReference, mockDocumentReference)
        }
    }
}