package com.gurpreetsk.android_starter._storage.common

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

fun String.Companion.empty(): String = ""

/**
 * Transmits events from an [Observable] via a [PublishRelay].
 */
fun <T> Observable<T>.circuitBreaker(): Observable<T> {
  val relay = PublishRelay.create<T>()
  this.subscribe(relay)
  return relay
    .toFlowable(BackpressureStrategy.LATEST)
    .hide()
    .toObservable()
}
