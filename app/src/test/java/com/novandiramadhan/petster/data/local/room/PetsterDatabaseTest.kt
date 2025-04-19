package com.novandiramadhan.petster.data.local.room

import androidx.room.InvalidationTracker
import com.novandiramadhan.petster.common.RoomConstants
import com.novandiramadhan.petster.data.local.room.dao.AssistantDao
import com.novandiramadhan.petster.data.local.room.entity.AssistantEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class PetsterDatabaseTest {

    @Mock
    private lateinit var assistantDao: AssistantDao

    private lateinit var database: PetsterDatabase

    private val testShelterId = "shelter123"
    private val testMessageId = UUID.randomUUID().toString()
    private val testTimestamp = System.currentTimeMillis()

    @BeforeEach
    fun setup() {
        database = object : PetsterDatabase() {
            override fun assistantDao(): AssistantDao = assistantDao
            override fun createInvalidationTracker(): InvalidationTracker {
                return MockInvalidationTracker()
            }
            override fun clearAllTables() {}
        }
    }

    @Test
    @DisplayName("Database should provide AssistantDao instance")
    fun database_shouldProvideAssistantDao() {
        val dao = database.assistantDao()
        assertEquals(assistantDao, dao, "Should return the expected DAO instance")
    }

    @Nested
    @DisplayName("Database Operations Tests")
    inner class DatabaseOperationsTests {

        @Test
        @DisplayName("Database should store messages through DAO")
        fun database_shouldStoreMessages() = runTest {
            val chatMessage = AssistantEntity(
                id = testMessageId,
                userId = testShelterId,
                message = "Test message",
                role = "user",
                direction = true,
                userPromptId = null,
                timestamp = testTimestamp
            )

            database.assistantDao().insertMessage(chatMessage)
            verify(assistantDao).insertMessage(chatMessage)
        }

        @Test
        @DisplayName("Database should retrieve messages through DAO")
        fun database_shouldRetrieveMessages() = runTest {
            val messagesList = listOf(
                AssistantEntity(
                    id = testMessageId,
                    userId = testShelterId,
                    message = "Test message",
                    role = "user",
                    direction = true,
                    userPromptId = null,
                    timestamp = testTimestamp
                )
            )

            `when`(assistantDao.getAllMessages(testShelterId)).thenReturn(flowOf(messagesList))

            val messagesFlow = database.assistantDao().getAllMessages(testShelterId)
            messagesFlow.collect { messages ->
                assertEquals(1, messages.size, "Should retrieve 1 message")
                assertEquals(testMessageId, messages[0].id, "Message ID should match")
            }

            verify(assistantDao).getAllMessages(testShelterId)
        }

        @Test
        @DisplayName("Database should update messages through DAO")
        fun database_shouldUpdateMessages() = runTest {
            val chatMessage = AssistantEntity(
                id = testMessageId,
                userId = testShelterId,
                message = "Updated message",
                role = "user",
                direction = true,
                userPromptId = null,
                timestamp = testTimestamp
            )

            database.assistantDao().updateMessage(chatMessage)
            verify(assistantDao).updateMessage(chatMessage)
        }

        @Test
        @DisplayName("Database should clear messages through DAO")
        fun database_shouldClearMessages() = runTest {
            database.assistantDao().clearHistory(testShelterId)

            verify(assistantDao).clearHistory(testShelterId)
        }
    }

    private inner class MockInvalidationTracker : InvalidationTracker(database, RoomConstants.CHAT_TABLE_NAME) {
        override fun addObserver(observer: Observer) {}
        override fun removeObserver(observer: Observer) {}
    }
}