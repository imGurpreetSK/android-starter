package com.gurpreetsk.android_starter._di.modules

import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Module object LogModule {
  @JvmStatic @Provides @Singleton fun provideLoggingTree(): Timber.Tree = object : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) { /* No-Op */ }
  }
}
