package com.gurpreetsk.android_starter._mvi

import android.os.Parcelable

interface MviView<in T : Parcelable> {
  fun render(state: T)
}
