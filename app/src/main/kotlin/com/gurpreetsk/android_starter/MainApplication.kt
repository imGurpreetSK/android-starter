package com.gurpreetsk.android_starter

import android.app.Application
import com.gurpreetsk.android_starter.di.AppComponent
import com.gurpreetsk.android_starter.di.DaggerAppComponent
import com.gurpreetsk.android_starter.di.modules.AppModule
import timber.log.Timber

class MainApplication : Application() {
  private lateinit var component: AppComponent

  override fun onCreate() {
    super.onCreate()

    component = setupDependencyInjection()
    setupLibraries()

    Timber.i("Open sesame! Application initialized…")
  }

  private fun setupLibraries() {
    Timber.plant(component.timberTree())
  }

  fun component(): AppComponent = component

  private fun setupDependencyInjection() : AppComponent = DaggerAppComponent.builder()
      .appModule(AppModule(this))
      .build()
}
