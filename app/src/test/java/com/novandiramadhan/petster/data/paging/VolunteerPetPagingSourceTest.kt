package com.novandiramadhan.petster.data.paging

import androidx.paging.PagingSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.domain.model.Pet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
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
class VolunteerPetPagingSourceTest {
    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Mock
    private lateinit var collectionReference: CollectionReference

    @Mock
    private lateinit var query: Query

    @Mock
    private lateinit var querySnapshot: QuerySnapshot

    @Mock
    private lateinit var documentSnapshot1: DocumentSnapshot

    @Mock
    private lateinit var documentSnapshot2: DocumentSnapshot

    private lateinit var pagingSource: VolunteerPetPagingSource
    private lateinit var mockPet1: Pet
    private lateinit var mockPet2: Pet
    private val volunteerId = "volunteer123"

    @BeforeEach
    fun setup() {
        pagingSource = VolunteerPetPagingSource(firestore, volunteerId)
        mockPet1 = Pet(
            id = "pet1",
            name = "Buddy",
            category = "Dog",
            gender = "Male"
        )
        mockPet2 = Pet(
            id = "pet2",
            name = "Whiskers",
            category = "Cat",
            gender = "Female"
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("VolunteerPetPagingSource load returns successfully with pet data")
    fun volunteerPetPagingSource_load_returnsSuccessResult() = runTest {
        setupSuccessMocks()

        val loadParams = PagingSource.LoadParams.Refresh<DocumentSnapshot>(
            key = null,
            loadSize = 10,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(loadParams)

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(2, page.data.size)
        assertEquals("Buddy", page.data[0].name)
        assertEquals("Whiskers", page.data[1].name)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("VolunteerPetPagingSource returns error result when Firestore throws exception")
    fun volunteerPetPagingSource_load_returnsErrorResultOnException() = runTest {
        setupErrorMocks()

        val loadParams = PagingSource.LoadParams.Refresh<DocumentSnapshot>(
            key = null,
            loadSize = 10,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(loadParams)

        assertTrue(result is PagingSource.LoadResult.Error)
        val errorResult = result as PagingSource.LoadResult.Error
        assertTrue(errorResult.throwable is PetPagingError)
        assertEquals(
            "Network error. Please check your connection and try again.",
            (errorResult.throwable as PetPagingError).message
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("VolunteerPetPagingSource returns empty page when volunteerId is null")
    fun volunteerPetPagingSource_load_returnsEmptyPageWhenVolunteerIdIsNull() = runTest {
        pagingSource = VolunteerPetPagingSource(firestore, null)

        val loadParams = PagingSource.LoadParams.Refresh<DocumentSnapshot>(
            key = null,
            loadSize = 10,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(loadParams)

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.prevKey)
        assertNull(page.nextKey)
    }

    private fun setupSuccessMocks() {
        val volunteerPath = "${FirebaseKeys.VOLUNTEER_COLLECTION}/$volunteerId"

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.whereEqualTo("volunteer", volunteerPath)).thenReturn(query)
        whenever(query.orderBy(any<String>(), any())).thenReturn(query)
        whenever(query.limit(any())).thenReturn(query)
        whenever(query.get()).thenReturn(Tasks.forResult(querySnapshot))

        val documentsList = listOf(documentSnapshot1, documentSnapshot2)
        whenever(querySnapshot.documents).thenReturn(documentsList)
        whenever(documentSnapshot1.id).thenReturn("pet1")
        whenever(documentSnapshot2.id).thenReturn("pet2")
        whenever(documentSnapshot1.toObject(Pet::class.java)).thenReturn(mockPet1.copy(id = null))
        whenever(documentSnapshot2.toObject(Pet::class.java)).thenReturn(mockPet2.copy(id = null))

        val emptyQuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
        whenever(emptyQuerySnapshot.documents).thenReturn(emptyList())

        val viewsCollectionRef = Mockito.mock(CollectionReference::class.java)
        val viewsQuery = Mockito.mock(Query::class.java)
        whenever(firestore.collection(FirebaseKeys.PET_VIEWS_COLLECTION)).thenReturn(viewsCollectionRef)
        whenever(viewsCollectionRef.whereIn(any<String>(), any())).thenReturn(viewsQuery)
        whenever(viewsQuery.get()).thenReturn(Tasks.forResult(emptyQuerySnapshot))
    }

    private fun setupErrorMocks() {
        val volunteerPath = "${FirebaseKeys.VOLUNTEER_COLLECTION}/$volunteerId"

        whenever(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        whenever(collectionReference.whereEqualTo("volunteer", volunteerPath)).thenReturn(query)
        whenever(query.orderBy(any<String>(), any())).thenReturn(query)
        whenever(query.limit(any())).thenReturn(query)

        val firestoreException = FirebaseFirestoreException(
            "Network unavailable",
            FirebaseFirestoreException.Code.UNAVAILABLE
        )
        whenever(query.get()).thenReturn(Tasks.forException(firestoreException))
    }
}