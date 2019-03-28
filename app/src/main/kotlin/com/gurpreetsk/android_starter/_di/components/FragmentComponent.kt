package com.gurpreetsk.android_starter._di.components

import android.content.Context
import com.gurpreetsk.android_starter.MainApplication
import com.gurpreetsk.android_starter._di.AppComponent
import com.gurpreetsk.android_starter._di.scopes.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class])
interface FragmentComponent {
  // TODO: Define contract

  companion object {
    fun obtain(context: Context): FragmentComponent =
        (context.applicationContext as MainApplication).fragmentComponent()
  }
}
