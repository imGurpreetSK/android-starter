package com.gurpreetsk.android_starter.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class AppModule(private val application: Application) {
  @Provides @Singleton fun provideAppContext(): Context =
      application
}
