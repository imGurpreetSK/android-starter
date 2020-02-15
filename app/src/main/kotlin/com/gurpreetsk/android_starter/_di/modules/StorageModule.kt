package com.gurpreetsk.android_starter._di.modules

import android.content.Context
import com.gurpreetsk.android_starter.BuildConfig.DB_NAME
import com.gurpreetsk.android_starter.Database
import com.gurpreetsk.android_starter._storage.Repository
import com.gurpreetsk.android_starter._storage.RepositoryImpl
import com.gurpreetsk.android_starter._storage.db.AppDatabase
import com.gurpreetsk.android_starter._storage.db.SqlDatabase
import com.gurpreetsk.android_starter._storage.prefs.AppSettings
import com.gurpreetsk.android_starter._storage.prefs.AppPrefs
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object StorageModule {
    @JvmStatic
    @Provides
    @Singleton
    fun provideDatabaseDriver(context: Context): SqlDriver =
            AndroidSqliteDriver(Database.Schema, context, DB_NAME)

    @JvmStatic
    @Provides
    @Singleton
    fun provideAppDatabase(driver: SqlDriver): AppDatabase =
            SqlDatabase(Database(driver))

    @JvmStatic
    @Provides
    @Singleton
    fun appSettings(context: Context): AppSettings =
            AppPrefs(context)

    @JvmStatic
    @Provides
    @Singleton
    fun repository(): Repository = RepositoryImpl()
}
