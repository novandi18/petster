package com.novandiramadhan.petster.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.novandiramadhan.petster.data.local.room.entity.AssistantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssistantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(chatEntity: AssistantEntity)

    @Update
    suspend fun updateMessage(chatEntity: AssistantEntity)

    @Query("SELECT * FROM chat_history WHERE userId = :shelterId ORDER BY timestamp ASC")
    fun getAllMessages(shelterId: String): Flow<List<AssistantEntity>>

    @Query("DELETE FROM chat_history WHERE userId = :shelterId")
    suspend fun clearHistory(shelterId: String)
}