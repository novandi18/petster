package com.novandiramadhan.petster.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.novandiramadhan.petster.common.RoomConstants
import com.novandiramadhan.petster.data.local.room.dao.AssistantDao
import com.novandiramadhan.petster.data.local.room.entity.AssistantEntity

@Database(
    entities = [AssistantEntity::class],
    version = RoomConstants.DATABASE_VERSION,
    exportSchema = true
)
abstract class PetsterDatabase: RoomDatabase() {
    abstract fun assistantDao(): AssistantDao
}