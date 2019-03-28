package com.gurpreetsk.android_starter._di.modules

import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Module object DebugLogModule {
  @JvmStatic @Provides @Singleton
  fun provideLoggingTree(): Timber.Tree = Timber.DebugTree()
}
