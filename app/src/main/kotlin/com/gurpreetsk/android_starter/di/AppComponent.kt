package com.gurpreetsk.android_starter.di

import android.content.Context
import com.gurpreetsk.android_starter.MainApplication
import com.gurpreetsk.android_starter.di.modules.AppModule
import com.gurpreetsk.android_starter.di.modules.LogModule
import dagger.Component
import timber.log.Timber
import javax.inject.Singleton

@Singleton
@Component(modules = [
  AppModule::class,
  LogModule::class
]) interface AppComponent {
  fun timberTree(): Timber.Tree

  companion object {
    fun obtain(context: Context): AppComponent =
        (context.applicationContext as MainApplication).component()
  }
}
