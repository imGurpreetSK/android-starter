package com.gurpreetsk.android_starter._di.modules

import android.content.Context
import androidx.room.Room
import com.gurpreetsk.android_starter.BuildConfig.DB_NAME
import com.gurpreetsk.android_starter._storage.CachedRepository
import com.gurpreetsk.android_starter._storage.LocalRepository
import com.gurpreetsk.android_starter._storage.RoomRepository
import com.gurpreetsk.android_starter._storage.db.AppDatabase
import com.gurpreetsk.android_starter._storage.prefs.AppSettings
import com.gurpreetsk.android_starter._storage.RoomRetrofitRepository
import com.gurpreetsk.android_starter._storage.prefs.SharedPreferencesAppSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class StorageModule {
  @Provides @Singleton
  fun provideAppDatabase(context: Context): AppDatabase =
      Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()

  @Provides @Singleton
  fun appSettings(context: Context): AppSettings =
      SharedPreferencesAppSettings(context)

  @Provides @Singleton
  fun localRepository(): LocalRepository =
      RoomRepository()

  @Provides @Singleton
  fun cachedRepository(): CachedRepository =
      RoomRetrofitRepository()
}
