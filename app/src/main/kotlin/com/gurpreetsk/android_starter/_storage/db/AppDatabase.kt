package com.gurpreetsk.android_starter._storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gurpreetsk.android_starter.BuildConfig

@Database(
    entities = [Dummy::class],
    version = BuildConfig.DB_VERSION
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): DummyDao
}
