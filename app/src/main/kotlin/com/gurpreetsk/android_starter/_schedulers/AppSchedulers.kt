package com.gurpreetsk.android_starter._schedulers

import io.reactivex.Scheduler

interface AppSchedulers {
  val computation: Scheduler
  val io: Scheduler
  val ui: Scheduler
}
