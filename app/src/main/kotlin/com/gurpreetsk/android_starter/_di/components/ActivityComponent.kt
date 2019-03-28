package com.gurpreetsk.android_starter._di.components

import android.content.Context
import com.gurpreetsk.android_starter.MainApplication
import com.gurpreetsk.android_starter._di.AppComponent
import com.gurpreetsk.android_starter._di.scopes.ActivityScope
import dagger.Component

@ActivityScope
@Component(dependencies = [AppComponent::class])
interface ActivityComponent {
  // TODO: Define contract

  companion object {
    fun obtain(context: Context): ActivityComponent =
        (context.applicationContext as MainApplication).activityComponent()
  }
}
