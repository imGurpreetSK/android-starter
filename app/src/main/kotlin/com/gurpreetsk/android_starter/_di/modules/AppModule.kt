package com.gurpreetsk.android_starter._di.modules

import android.app.Application
import android.content.Context
import com.gurpreetsk.android_starter._schedulers.AppSchedulers
import com.gurpreetsk.android_starter._schedulers.CoroutineSchedulers
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {
    @Provides
    @Singleton
    fun provideAppContext(): Context = application

    @Provides
    fun providesSchedulers(): AppSchedulers = CoroutineSchedulers()
}
