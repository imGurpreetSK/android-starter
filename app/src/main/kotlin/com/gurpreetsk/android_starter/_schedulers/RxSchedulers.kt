package com.gurpreetsk.android_starter._schedulers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RxSchedulers : AppSchedulers {
  override val computation: Scheduler
    get() = Schedulers.computation()

  override val io: Scheduler
    get() = Schedulers.io()

  override val ui: Scheduler
    get() = AndroidSchedulers.mainThread()
}
