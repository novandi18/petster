package com.novandiramadhan.petster.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.data.resource.Resource
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
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
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
        whenever(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
        whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    }

    @Nested
    @DisplayName("Register Volunteer Tests")
    inner class RegisterVolunteerTests {

        @Test
        @DisplayName("registerVolunteer - Success - Should emit Loading then Success")
        fun registerVolunteer_success_emitsLoadingAndSuccess() = runTest {
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
    }
}