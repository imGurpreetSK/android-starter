package com.gurpreetsk.android_starter._mvi

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

private const val KEY_MODEL_STATE = "model_state"

abstract class MviActivity<T : Parcelable> : AppCompatActivity() {
  protected val disposables: CompositeDisposable by lazy { CompositeDisposable() }
  protected val lifecycle: BehaviorRelay<MviLifecycle> by lazy { BehaviorRelay.create<MviLifecycle>() }

  protected val lastKnownState: T?
    get() = stateRelay.value

  private val stateRelay: BehaviorRelay<T> by lazy { BehaviorRelay.create<T>() }
  private lateinit var lifecycleEvent: MviLifecycle

  /**
   * Inject self into Dagger graph.
   */
  open fun injectSelf() {}

  /**
   * Return a layout resource used in this [android.app.Activity].
   */
  @LayoutRes abstract fun getLayoutRes(): Int

  /**
   * This method is called during [onCreate], any code that has to run when the Activity is setup
   * can go here.
   */
  open fun setup(savedInstanceState: Bundle?) {}

  /**
   * This method is called before [MviActivity.bind]. Use it to setup your UI, get
   * extras or do anything that has to be done before the Activity binds to your model.
   */
  open fun preBind() {}

  /**
   * This function is called after [MviActivity.bind]. Use it to send events to your model binding.
   */
  open fun postBind() {}

  /**
   * The binder method that's called to bind the model with it's intention and the view.
   * @param states An observable backed by a [BehaviorRelay].
   */
  abstract fun bind(states: Observable<T>): Observable<T>

  /**
   * Free any resources that were acquired in your Activity.
   */
  open fun unbind() {}

  /**
   * This method receives state emissions from the bound model.
   * @param state The state that was recently emitted by the model.
   */
  abstract fun effects(state: T)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    injectSelf()
    setContentView(getLayoutRes())
    restoreLastKnownState(savedInstanceState)

    lifecycleEvent = if (savedInstanceState == null) MviLifecycle.CREATED else MviLifecycle.RESTORED

    setup(savedInstanceState)
  }

  override fun onStart() {
    super.onStart()
    preBind()
    val statesObservable = stateRelay
        .distinctUntilChanged()
        .toFlowable(BackpressureStrategy.LATEST)
        .toObservable()

    // Lifecycle event
    lifecycle.accept(lifecycleEvent)

    disposables += bind(statesObservable)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          stateRelay.accept(it)
          effects(it)
        }
    postBind()
  }

  override fun onStop() {
    lifecycle.accept(MviLifecycle.STOPPED)
    disposables.clear()
    unbind()
    super.onStop()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putParcelable(KEY_MODEL_STATE, stateRelay.value)
    lifecycleEvent = MviLifecycle.RESTORED
    super.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    lifecycleEvent = MviLifecycle.RESTORED
  }

  private fun restoreLastKnownState(savedInstanceState: Bundle?) {
    savedInstanceState?.let {
      stateRelay.accept(savedInstanceState.getParcelable(KEY_MODEL_STATE))
    }
  }
}
