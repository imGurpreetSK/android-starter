package com.gurpreetsk.android_starter._schedulers

import io.reactivex.Scheduler

interface AppSchedulers {
  fun computation(): Scheduler
  fun io(): Scheduler
  fun ui(): Scheduler
}
