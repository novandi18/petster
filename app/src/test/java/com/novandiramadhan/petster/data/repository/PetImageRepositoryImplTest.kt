package com.novandiramadhan.petster.data.repository

import com.novandiramadhan.petster.data.remote.RemoteDataSource
import com.novandiramadhan.petster.data.remote.request.ImgBBRequest
import com.novandiramadhan.petster.data.remote.response.ImgBBResponse
import com.novandiramadhan.petster.data.remote.response.ImgBBResponseData
import com.novandiramadhan.petster.data.resource.ApiResource
import com.novandiramadhan.petster.data.resource.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PetImageRepositoryImplTest {
    @Mock
    private lateinit var remoteDataSource: RemoteDataSource

    private lateinit var repository: PetImageRepositoryImpl

    private val testRequest = ImgBBRequest(
        key = "test-key",
        image = "base64-image-data"
    )

    private val successResponse = ImgBBResponse(
        data = ImgBBResponseData(
            id = "image123",
            url = "https://example.com/image.jpg"
        ),
        success = true,
        status = 200
    )

    @BeforeEach
    fun setup() {
        repository = PetImageRepositoryImpl(remoteDataSource)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("Upload image returns success when remote call succeeds")
    fun uploadImage_whenSuccessful_returnsSuccessResource() = runTest {
        `when`(remoteDataSource.uploadPetImage(testRequest))
            .thenReturn(flowOf(ApiResource.Success(successResponse)))

        val results = repository.upload(testRequest).toList()
        verify(remoteDataSource).uploadPetImage(testRequest)

        val successResult = results.find { it is Resource.Success }
        assertTrue(
            successResult != null,
            "No Success result found in emissions: ${results.map { it.javaClass.simpleName }}"
        )

        val data = (successResult as Resource.Success).data
        assertEquals("image123", data?.id)
        assertEquals("https://example.com/image.jpg", data?.url)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("Upload image returns error when remote call fails")
    fun uploadImage_whenFailed_returnsErrorResource() = runTest {
        val errorMessage = "Upload failed due to network error"
        `when`(remoteDataSource.uploadPetImage(testRequest))
            .thenReturn(flowOf(ApiResource.Error(errorMessage)))

        val results = repository.upload(testRequest).toList()
        verify(remoteDataSource).uploadPetImage(testRequest)

        val errorResult = results.find { it is Resource.Error }
        assertTrue(
            errorResult != null,
            "No Error result found in emissions: ${results.map { it.javaClass.simpleName }}"
        )

        assertEquals(errorMessage, (errorResult as Resource.Error).message)
    }
}