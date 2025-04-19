package com.novandiramadhan.petster.data.paging

import androidx.paging.PagingSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
class PetPagingSourceTest {

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

    private lateinit var pagingSource: PetPagingSource

    private val mockPet1 = Pet(
        id = "pet1",
        name = "Buddy",
        category = "Dog",
        createdAt = Timestamp.now(),
        gender = "Male",
        age = 2,
        ageUnit = "Years"
    )

    private val mockPet2 = Pet(
        id = "pet2",
        name = "Whiskers",
        category = "Cat",
        createdAt = Timestamp.now(),
        gender = "Female",
        age = 3,
        ageUnit = "Years"
    )

    @BeforeEach
    fun setup() {
        pagingSource = PetPagingSource(firestore)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("PetPagingSource load returns successfully with pet data")
    fun petPagingSource_load_returnsSuccessResult() = runTest {
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
    @DisplayName("PetPagingSource returns error result when Firestore throws exception")
    fun petPagingSource_load_returnsErrorResultOnException() = runTest {
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

    private fun setupSuccessMocks() {
        `when`(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        `when`(collectionReference.orderBy(any<String>(), any())).thenReturn(query)
        `when`(query.limit(any())).thenReturn(query)
        `when`(query.get()).thenReturn(Tasks.forResult(querySnapshot))

        val documentsList = listOf(documentSnapshot1, documentSnapshot2)
        `when`(querySnapshot.documents).thenReturn(documentsList)
        `when`(documentSnapshot1.id).thenReturn("pet1")
        `when`(documentSnapshot2.id).thenReturn("pet2")
        `when`(documentSnapshot1.toObject(Pet::class.java)).thenReturn(mockPet1.copy(id = null))
        `when`(documentSnapshot2.toObject(Pet::class.java)).thenReturn(mockPet2.copy(id = null))

        val emptyQuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
        `when`(emptyQuerySnapshot.documents).thenReturn(emptyList())

        val viewsCollectionRef = Mockito.mock(CollectionReference::class.java)
        val viewsQuery = Mockito.mock(Query::class.java)
        `when`(firestore.collection(FirebaseKeys.PET_VIEWS_COLLECTION)).thenReturn(viewsCollectionRef)
        `when`(viewsCollectionRef.whereIn(any<String>(), any())).thenReturn(viewsQuery)
        `when`(viewsQuery.get()).thenReturn(Tasks.forResult(emptyQuerySnapshot))
    }

    private fun setupErrorMocks() {
        `when`(firestore.collection(FirebaseKeys.PET_COLLECTION)).thenReturn(collectionReference)
        `when`(collectionReference.orderBy(any<String>(), any())).thenReturn(query)
        `when`(query.limit(any())).thenReturn(query)

        val firestoreException = FirebaseFirestoreException(
            "Network unavailable",
            FirebaseFirestoreException.Code.UNAVAILABLE
        )
        `when`(query.get()).thenReturn(Tasks.forException(firestoreException))
    }
}