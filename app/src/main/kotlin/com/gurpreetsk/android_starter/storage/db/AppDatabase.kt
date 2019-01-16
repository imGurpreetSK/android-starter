package com.gurpreetsk.android_starter.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gurpreetsk.android_starter.BuildConfig

@Database(
    entities = [User::class],
    version = BuildConfig.DB_VERSION
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
}
