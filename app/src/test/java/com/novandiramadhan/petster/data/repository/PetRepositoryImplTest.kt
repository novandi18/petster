package com.novandiramadhan.petster.data.repository

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.PetResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class PetRepositoryImplTest {

    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var collectionReference: CollectionReference

    @Mock
    private lateinit var documentReference: DocumentReference

    private lateinit var repository: PetRepositoryImpl
    private lateinit var mockPet: Pet

    @BeforeEach
    fun setup() {
        repository = PetRepositoryImpl(context, firestore)

        mockPet = Pet(
            name = "Buddy",
            category = "Dog",
            gender = "Male",
            breed = "Golden Retriever",
            age = 3,
            ageUnit = "Years",
            weight = "25",
            weightUnit = "Kilogram"
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("addPet returns success when pet is added successfully")
    fun addPet_returnsSuccessResult() = runTest {
        val documentId = "pet123"
        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document()).thenReturn(documentReference)
        whenever(documentReference.id).thenReturn(documentId)
        whenever(documentReference.set(any())).thenReturn(Tasks.forResult(null))

        val results = repository.addPet(mockPet).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)

        val success = results[1] as Resource.Success
        val petResult = success.data as PetResult

        assertEquals("Pet added successfully", petResult.message)
        assertEquals(documentId, petResult.pet.id)
        assertEquals("Buddy", petResult.pet.name)
        assertEquals("Dog", petResult.pet.category)
        assertEquals("Golden Retriever", petResult.pet.breed)
        assertNotNull(petResult.pet.createdAt, "Timestamp should be set")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("addPet returns error when pet addition fails")
    fun addPet_returnsErrorResultOnFailure() = runTest {
        val exception = RuntimeException("Network failure")
        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document()).thenReturn(documentReference)
        whenever(documentReference.set(any())).thenReturn(Tasks.forException(exception))

        val results = repository.addPet(mockPet).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)

        val error = results[1] as Resource.Error
        assertEquals("Network failure", error.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("deletePet returns success when pet is deleted successfully")
    fun deletePet_returnsSuccessResult() = runTest {
        val petId = "pet123"
        val documentReference = Mockito.mock(DocumentReference::class.java)
        val collectionReference = Mockito.mock(CollectionReference::class.java)
        val querySnapshot = Mockito.mock(QuerySnapshot::class.java)
        val query = Mockito.mock(Query::class.java)

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document(petId)).thenReturn(documentReference)
        whenever(documentReference.delete()).thenReturn(Tasks.forResult(null))

        val favoritedCollectionRef = Mockito.mock(CollectionReference::class.java)
        whenever(firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)).thenReturn(favoritedCollectionRef)
        whenever(favoritedCollectionRef.whereEqualTo("petId", petId)).thenReturn(query)
        whenever(query.get()).thenReturn(Tasks.forResult(querySnapshot))
        whenever(querySnapshot.documents).thenReturn(emptyList())
        whenever(context.getString(R.string.pet_delete_success)).thenReturn("Pet deleted successfully")

        val results = repository.deletePet(petId).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)

        val success = results[1] as Resource.Success
        assertEquals("Pet deleted successfully", success.data?.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("deletePet returns error when deletion fails")
    fun deletePet_returnsErrorResultOnFailure() = runTest {
        val petId = "pet123"
        val documentReference = Mockito.mock(DocumentReference::class.java)
        val collectionReference = Mockito.mock(CollectionReference::class.java)
        val exception = FirebaseFirestoreException(
            "Database connection error",
            FirebaseFirestoreException.Code.UNAVAILABLE
        )

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document(petId)).thenReturn(documentReference)
        whenever(documentReference.delete()).thenReturn(Tasks.forException(exception))

        val results = repository.deletePet(petId).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)

        val error = results[1] as Resource.Error
        assertEquals("Database connection error", error.message)
    }
}