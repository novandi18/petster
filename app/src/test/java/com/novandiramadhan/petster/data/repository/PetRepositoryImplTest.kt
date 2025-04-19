package com.novandiramadhan.petster.data.repository

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.PetResult
import com.novandiramadhan.petster.domain.model.PetView
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
import org.mockito.kotlin.argThat
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("getPetById returns success with pet details when pet exists")
    fun getPetById_returnsSuccessResultWithPet() = runTest {
        val petId = "pet123"
        val shelterId = "shelter456"
        val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        val documentReference = Mockito.mock(DocumentReference::class.java)
        val collectionReference = Mockito.mock(CollectionReference::class.java)
        val query = Mockito.mock(Query::class.java)
        val querySnapshot = Mockito.mock(QuerySnapshot::class.java)

        val mockPet = mockPet.copy(id = petId)

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document(petId)).thenReturn(documentReference)
        whenever(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
        whenever(documentSnapshot.exists()).thenReturn(true)
        whenever(documentSnapshot.toObject(Pet::class.java)).thenReturn(mockPet.copy(id = null))
        whenever(documentSnapshot.id).thenReturn(petId)

        val favCollectionRef = Mockito.mock(CollectionReference::class.java)
        whenever(firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)).thenReturn(favCollectionRef)
        whenever(favCollectionRef.whereEqualTo("petId", petId)).thenReturn(query)
        whenever(query.whereEqualTo("shelterId", shelterId)).thenReturn(query)
        whenever(query.limit(1)).thenReturn(query)
        whenever(query.get()).thenReturn(Tasks.forResult(querySnapshot))
        whenever(querySnapshot.isEmpty).thenReturn(true)

        val results = repository.getPetById(petId, shelterId).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)

        val success = results[1] as Resource.Success
        val resultPet = success.data

        assertEquals(petId, resultPet?.id)
        assertEquals("Buddy", resultPet?.name)
        assertEquals("Dog", resultPet?.category)
        assertEquals(false, resultPet?.isFavorite)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("getPetById returns error when pet does not exist")
    fun getPetById_returnsErrorWhenPetNotFound() = runTest {
        val petId = "nonexistent123"
        val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        val documentReference = Mockito.mock(DocumentReference::class.java)
        val collectionReference = Mockito.mock(CollectionReference::class.java)

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document(petId)).thenReturn(documentReference)
        whenever(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
        whenever(documentSnapshot.exists()).thenReturn(false)

        val results = repository.getPetById(petId, null).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)

        val error = results[1] as Resource.Error
        assertEquals("Pet not found", error.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("addViewedPet creates new record when no existing view")
    fun addViewedPet_createsNewRecordWhenNoExistingView() = runTest {
        val petId = "pet123"
        val shelterId = "shelter456"
        val petView = PetView(petId = petId, shelterId = shelterId)

        val collectionReference = Mockito.mock(CollectionReference::class.java)
        val query = Mockito.mock(Query::class.java)
        val querySnapshot = Mockito.mock(QuerySnapshot::class.java)
        val documentReference = Mockito.mock(DocumentReference::class.java)

        whenever(firestore.collection(FirebaseKeys.PET_VIEWS_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.whereEqualTo("petId", petId)).thenReturn(query)
        whenever(query.whereEqualTo("shelterId", shelterId)).thenReturn(query)
        whenever(query.limit(1)).thenReturn(query)
        whenever(query.get()).thenReturn(Tasks.forResult(querySnapshot))
        whenever(querySnapshot.documents).thenReturn(emptyList())
        whenever(collectionReference.document()).thenReturn(documentReference)
        whenever(documentReference.set(any())).thenReturn(Tasks.forResult(null))

        val results = repository.addViewedPet(petView).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)

        Mockito.verify(documentReference).set(argThat { arg ->
            arg is PetView &&
                    arg.petId == petId &&
                    arg.shelterId == shelterId &&
                    arg.timestamp != null
        })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("addViewedPet returns error with empty parameters")
    fun addViewedPet_returnsErrorWithEmptyParameters() = runTest {
        val petView = PetView(petId = "", shelterId = null)
        val results = repository.addViewedPet(petView).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)

        val error = results[1] as Resource.Error
        assertEquals("Pet ID and Shelter ID cannot be empty", error.message)

        Mockito.verifyNoInteractions(firestore)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("getVolunteerDashboard returns success with correct dashboard data")
    fun getVolunteerDashboard_returnsSuccessWithDashboardData() = runTest {
        val volunteerId = "volunteer123"
        val volunteerPath = "${FirebaseKeys.VOLUNTEER_COLLECTION}/$volunteerId"

        val petsCollectionRef = Mockito.mock(CollectionReference::class.java)
        val petsQuery = Mockito.mock(Query::class.java)
        val adoptedPetsQuery = Mockito.mock(Query::class.java)
        val petsSnapshot = Mockito.mock(QuerySnapshot::class.java)
        val adoptedPetsSnapshot = Mockito.mock(QuerySnapshot::class.java)

        val petDoc1 = Mockito.mock(DocumentSnapshot::class.java)
        val petDoc2 = Mockito.mock(DocumentSnapshot::class.java)
        val petDoc3 = Mockito.mock(DocumentSnapshot::class.java)

        Mockito.lenient().`when`(petDoc1.id).thenReturn("pet1")
        Mockito.lenient().`when`(petDoc2.id).thenReturn("pet2")
        Mockito.lenient().`when`(petDoc3.id).thenReturn("pet3")

        val petDocs = listOf(petDoc1, petDoc2, petDoc3)
        val adoptedPetDocs = listOf(petDoc2)

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(petsCollectionRef)
        whenever(petsCollectionRef.whereEqualTo("volunteer", volunteerPath)).thenReturn(petsQuery)
        whenever(petsQuery.get()).thenReturn(Tasks.forResult(petsSnapshot))
        whenever(petsSnapshot.documents).thenReturn(petDocs)
        whenever(petsQuery.whereEqualTo("adopted", true)).thenReturn(adoptedPetsQuery)
        whenever(adoptedPetsQuery.get()).thenReturn(Tasks.forResult(adoptedPetsSnapshot))
        whenever(adoptedPetsSnapshot.documents).thenReturn(adoptedPetDocs)

        val viewsCollectionRef = Mockito.mock(CollectionReference::class.java)
        val viewsQuery = Mockito.mock(Query::class.java)
        val viewsSnapshot = Mockito.mock(QuerySnapshot::class.java)

        val viewDoc1 = Mockito.mock(DocumentSnapshot::class.java)
        val viewDoc2 = Mockito.mock(DocumentSnapshot::class.java)
        whenever(viewDoc1.getString("shelterId")).thenReturn("shelter1")
        whenever(viewDoc2.getString("shelterId")).thenReturn("shelter2")
        val viewDocs = listOf(viewDoc1, viewDoc2)

        whenever(firestore.collection(FirebaseKeys.PET_VIEWS_COLLECTION)).thenReturn(viewsCollectionRef)
        whenever(viewsCollectionRef.whereIn("petId", listOf("pet1", "pet2", "pet3"))).thenReturn(viewsQuery)
        whenever(viewsQuery.get()).thenReturn(Tasks.forResult(viewsSnapshot))
        whenever(viewsSnapshot.documents).thenReturn(viewDocs)

        val results = repository.getVolunteerDashboard(volunteerId).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)

        val success = results[1] as Resource.Success
        val dashboardResult = success.data

        assertEquals(3, dashboardResult?.totalPets)
        assertEquals(1, dashboardResult?.adoptedPets)
        assertEquals(2, dashboardResult?.totalViews)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("getVolunteerDashboard returns error when volunteerId is empty")
    fun getVolunteerDashboard_returnsErrorWhenVolunteerIdIsEmpty() = runTest {
        val emptyVolunteerId = ""
        val results = repository.getVolunteerDashboard(emptyVolunteerId).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)

        val error = results[1] as Resource.Error
        assertEquals("Volunteer ID cannot be empty", error.message)
        assertEquals(R.string.error_empty_volunteer_id, error.messageResId)

        Mockito.verifyNoInteractions(firestore)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("updatePet returns success when pet is updated successfully")
    fun updatePet_returnsSuccessResult() = runTest {
        val petId = "pet123"
        val updatedPet = mockPet.copy(
            id = petId,
            name = "Updated Buddy",
            breed = "Labrador Retriever"
        )

        val documentReference = Mockito.mock(DocumentReference::class.java)
        val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        val collectionReference = Mockito.mock(CollectionReference::class.java)

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document(petId)).thenReturn(documentReference)
        whenever(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
        whenever(documentSnapshot.exists()).thenReturn(true)
        whenever(documentReference.set(any())).thenReturn(Tasks.forResult(null))
        whenever(context.getString(R.string.pet_update_success)).thenReturn("Pet information updated successfully")

        val results = repository.updatePet(updatedPet).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)

        val success = results[1] as Resource.Success
        val petResult = success.data

        assertEquals("Pet information updated successfully", petResult?.message)
        assertEquals(petId, petResult?.pet?.id)
        assertEquals("Updated Buddy", petResult?.pet?.name)
        assertEquals("Labrador Retriever", petResult?.pet?.breed)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("updatePet returns error when pet is not found")
    fun updatePet_returnsErrorWhenPetNotFound() = runTest {
        val petId = "nonexistentPet123"
        val updatedPet = mockPet.copy(id = petId)

        val documentReference = Mockito.mock(DocumentReference::class.java)
        val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        val collectionReference = Mockito.mock(CollectionReference::class.java)

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document(petId)).thenReturn(documentReference)
        whenever(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
        whenever(documentSnapshot.exists()).thenReturn(false)

        Mockito.lenient().`when`(context.getString(R.string.pet_not_found)).thenReturn("Pet not found")

        val results = repository.updatePet(updatedPet).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)

        val error = results[1] as Resource.Error
        assertEquals("Pet not found", error.message)
        assertEquals(R.string.pet_not_found, error.messageResId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("updatePet returns error when pet ID is null")
    fun updatePet_returnsErrorWhenPetIdIsNull() = runTest {
        val updatedPet = mockPet.copy(id = null)
        val results = repository.updatePet(updatedPet).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)

        val error = results[1] as Resource.Error
        assertEquals("Pet ID cannot be null", error.message)

        Mockito.verifyNoInteractions(firestore)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("togglePetAdopted returns success when pet is set to adopted")
    fun togglePetAdopted_returnsSuccessWhenSetToAdopted() = runTest {
        val petId = "pet123"
        val isAdopted = true

        val documentReference = Mockito.mock(DocumentReference::class.java)
        val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        val collectionReference = Mockito.mock(CollectionReference::class.java)

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document(petId)).thenReturn(documentReference)
        whenever(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
        whenever(documentSnapshot.exists()).thenReturn(true)
        whenever(documentReference.update("adopted", isAdopted)).thenReturn(Tasks.forResult(null))
        whenever(context.getString(R.string.set_as_adopted)).thenReturn("Set as Adopted")

        val results = repository.togglePetAdopted(petId, isAdopted).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)

        val success = results[1] as Resource.Success
        assertEquals("Set as Adopted", success.data?.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("togglePetAdopted returns success when pet is set to available")
    fun togglePetAdopted_returnsSuccessWhenSetToAvailable() = runTest {
        val petId = "pet123"
        val isAdopted = false

        val documentReference = Mockito.mock(DocumentReference::class.java)
        val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        val collectionReference = Mockito.mock(CollectionReference::class.java)

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.document(petId)).thenReturn(documentReference)
        whenever(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
        whenever(documentSnapshot.exists()).thenReturn(true)
        whenever(documentReference.update("adopted", isAdopted)).thenReturn(Tasks.forResult(null))
        whenever(context.getString(R.string.set_as_available)).thenReturn("Set as Available")

        val results = repository.togglePetAdopted(petId, isAdopted).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)

        val success = results[1] as Resource.Success
        assertEquals("Set as Available", success.data?.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("togglePetAdopted returns error when petId is empty")
    fun togglePetAdopted_returnsErrorWhenPetIdIsEmpty() = runTest {
        val petId = ""
        val isAdopted = true

        val results = repository.togglePetAdopted(petId, isAdopted).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)

        val error = results[1] as Resource.Error
        assertEquals("Pet ID cannot be empty", error.message)
        assertEquals(R.string.error_empty_pet_id, error.messageResId)

        Mockito.verifyNoInteractions(firestore)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("togglePetAdopted returns error when pet is not found")
    fun togglePetAdopted_returnsErrorWhenPetNotFound() = runTest {
        val petId = "nonexistent-pet-id"
        val isAdopted = true

        val petCollectionRef = Mockito.mock(CollectionReference::class.java)
        val petDocRef = Mockito.mock(DocumentReference::class.java)
        val petDocSnapshot = Mockito.mock(DocumentSnapshot::class.java)

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(petCollectionRef)
        whenever(petCollectionRef.document(petId)).thenReturn(petDocRef)
        whenever(petDocRef.get()).thenReturn(Tasks.forResult(petDocSnapshot))
        whenever(petDocSnapshot.exists()).thenReturn(false)

        Mockito.lenient().`when`(context.getString(R.string.pet_not_found)).thenReturn("Pet not found")

        val results = repository.togglePetAdopted(petId, isAdopted).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)

        val error = results[1] as Resource.Error
        assertEquals("Pet not found", error.message)
        assertEquals(R.string.pet_not_found, error.messageResId)

        Mockito.verify(petDocRef, Mockito.never()).update(any<String>(), any<Boolean>())
    }
}