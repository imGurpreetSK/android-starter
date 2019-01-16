package com.gurpreetsk.android_starter.di

import com.gurpreetsk.android_starter.di.modules.AppModule
import com.gurpreetsk.android_starter.di.modules.DebugLogModule
import com.gurpreetsk.android_starter.di.modules.NetworkModule
import com.gurpreetsk.android_starter.di.modules.StorageModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
  AppModule::class,
  DebugLogModule::class,
  NetworkModule::class,
  StorageModule::class
])
interface DebugAppComponent : AppComponent
