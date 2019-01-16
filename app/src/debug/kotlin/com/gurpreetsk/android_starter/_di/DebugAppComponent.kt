package com.gurpreetsk.android_starter._di

import com.gurpreetsk.android_starter._di.modules.AppModule
import com.gurpreetsk.android_starter._di.modules.DebugLogModule
import com.gurpreetsk.android_starter._di.modules.NetworkModule
import com.gurpreetsk.android_starter._di.modules.StorageModule
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
