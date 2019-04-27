package com.gurpreetsk.android_starter._di.modules

import android.content.Context
import androidx.room.Room
import com.gurpreetsk.android_starter.BuildConfig.DB_NAME
import com.gurpreetsk.android_starter._storage.Repository
import com.gurpreetsk.android_starter._storage.db.AppDatabase
import com.gurpreetsk.android_starter._storage.prefs.AppSettings
import com.gurpreetsk.android_starter._storage.RepositoryImpl
import com.gurpreetsk.android_starter._storage.prefs.SharedPreferencesAppSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module object StorageModule {
  @JvmStatic @Provides @Singleton
  fun provideAppDatabase(context: Context): AppDatabase =
      Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()

  @JvmStatic @Provides @Singleton
  fun appSettings(context: Context): AppSettings =
      SharedPreferencesAppSettings(context)

  @JvmStatic @Provides @Singleton
  fun repository(): Repository =
      RepositoryImpl()
}
