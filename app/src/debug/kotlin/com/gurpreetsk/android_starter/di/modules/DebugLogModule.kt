package com.gurpreetsk.android_starter.di.modules

import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Module class DebugLogModule {
  @Provides @Singleton fun provideLoggingTree(): Timber.Tree = Timber.DebugTree()
}
