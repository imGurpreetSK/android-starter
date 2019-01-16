package com.gurpreetsk.android_starter.di

import com.gurpreetsk.android_starter.di.modules.AppModule
import com.gurpreetsk.android_starter.di.modules.DebugLogModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
  AppModule::class,
  DebugLogModule::class
])
interface DebugAppComponent : AppComponent
