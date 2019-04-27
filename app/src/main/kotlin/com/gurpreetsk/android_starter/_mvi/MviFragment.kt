package com.gurpreetsk.android_starter._mvi

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

private const val KEY_MODEL_STATE = "model_state"

abstract class MviFragment<T : Parcelable> : Fragment() {
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
  protected open fun setup(savedInstanceState: Bundle?) {}

  /**
   * This method is called before [MviActivity.bind]. Use it to setup your UI, get
   * extras or do anything that has to be done before the Activity binds to your model.
   */
  protected open fun preBind() {}

  /**
   * This function is called after [MviActivity.bind]. Use it to send events to your model binding.
   */
  protected open fun postBind() {}

  /**
   * The binder method that's called to bind the model with it's intention and the view.
   * @param states An observable backed by a [BehaviorRelay].
   */
  protected abstract fun bind(states: Observable<T>): Observable<T>

  /**
   * Free any resources that were acquired in your Activity.
   */
  protected open fun unbind() {}

  /**
   * This method receives state emissions from the bound model.
   * @param state The state that was recently emitted by the model.
   */
  protected abstract fun effects(state: T)

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    injectSelf()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater.inflate(getLayoutRes(), container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    restoreLastKnownState(savedInstanceState)

    lifecycleEvent = if (savedInstanceState == null) MviLifecycle.CREATED else MviLifecycle.RESTORED

    setup(savedInstanceState)
    preBind()
  }

  override fun onStart() {
    super.onStart()
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

  private fun restoreLastKnownState(savedInstanceState: Bundle?) {
    savedInstanceState?.let {
      stateRelay.accept(savedInstanceState.getParcelable(KEY_MODEL_STATE))
    }
  }
}
