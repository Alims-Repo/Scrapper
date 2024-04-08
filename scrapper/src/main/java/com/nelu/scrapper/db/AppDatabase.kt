package com.nelu.scrapper.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nelu.scrapper.data.model.ModelDownload
import com.nelu.scrapper.db.dao.DaoDownloads

@Database(entities = [ModelDownload::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): DaoDownloads
}