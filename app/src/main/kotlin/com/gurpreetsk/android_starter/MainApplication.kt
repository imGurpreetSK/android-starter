package com.gurpreetsk.android_starter

import android.annotation.SuppressLint
import android.app.Application
import com.gurpreetsk.android_starter._di.AppComponent
import com.gurpreetsk.android_starter._di.DaggerAppComponent
import com.gurpreetsk.android_starter._di.components.ActivityComponent
import com.gurpreetsk.android_starter._di.components.DaggerActivityComponent
import com.gurpreetsk.android_starter._di.components.DaggerFragmentComponent
import com.gurpreetsk.android_starter._di.components.FragmentComponent
import com.gurpreetsk.android_starter._di.modules.AppModule
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

@SuppressLint("Registered")
open class MainApplication : Application() {
  private lateinit var component: AppComponent
  private lateinit var activityComponent: ActivityComponent
  private lateinit var fragmentComponent: FragmentComponent

  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return
    }
    LeakCanary.install(this)

    setupDependencyInjection()
    setupLibraries()
    Timber.i("Open sesame! Application initialized…")
  }

  private fun setupLibraries() {
    Timber.plant(component.timberTree())
  }

  private fun setupDependencyInjection() {
    component = getAppComponent()
    activityComponent = getActivityComponent()
    fragmentComponent = getFragmentComponent()
  }

  open fun getAppComponent(): AppComponent {
    return DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()
  }

  private fun getActivityComponent(): ActivityComponent {
    return DaggerActivityComponent.builder()
        .appComponent(component)
        .build()
  }

  private fun getFragmentComponent(): FragmentComponent {
    return DaggerFragmentComponent.builder()
        .appComponent(component)
        .build()
  }

  fun component(): AppComponent = component
  fun activityComponent(): ActivityComponent = activityComponent
  fun fragmentComponent(): FragmentComponent = fragmentComponent
}
