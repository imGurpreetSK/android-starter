package com.gurpreetsk.android_starter._mvi

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

private const val KEY_MODEL_STATE = "model_state"

@FlowPreview
@ExperimentalCoroutinesApi
abstract class MviActivity<T : Parcelable> : AppCompatActivity() {
    protected val lifecycle: ConflatedBroadcastChannel<MviLifecycle> by lazy {
        ConflatedBroadcastChannel<MviLifecycle>()
    }

    private val stateRelay: ConflatedBroadcastChannel<T> by lazy {
        ConflatedBroadcastChannel<T>()
    }

    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val mainScope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var lifecycleEvent: MviLifecycle

    protected val lastKnownState: T?
        get() = stateRelay.value

    /**
     * Inject self into Dagger graph.
     */
    open fun injectSelf() {}

    /**
     * Return a layout resource used in this [android.app.Activity].
     */
    @LayoutRes
    abstract fun getLayoutRes(): Int

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
     * @param states A flow backed by a [ConflatedBroadcastChannel].
     */
    abstract fun bind(states: Flow<T>): Flow<T>

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

        // Lifecycle event
        ioScope.launch {
            val statesObservable = stateRelay
                    .asFlow()
                    .distinctUntilChanged()

            lifecycle.send(lifecycleEvent)

            val bind = bind(statesObservable)
            mainScope.launch {
                bind.collect {
                    stateRelay.send(it)
                    effects(it)
                }
            }
        }

        postBind()
    }

    override fun onStop() {
        ioScope.launch {
            lifecycle.send(MviLifecycle.STOPPED)
        }
        unbind()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.coroutineContext.cancelChildren(CancellationException("ioScope children cancelled"))
        mainScope.coroutineContext.cancelChildren(CancellationException("mainScope children cancelled"))
        job.cancel(CancellationException("job cancelled"))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_MODEL_STATE, stateRelay.value)
        lifecycleEvent = MviLifecycle.RESTORED
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        lifecycleEvent = MviLifecycle.RESTORED
    }

    private fun restoreLastKnownState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            ioScope.launch {
                stateRelay.send(savedInstanceState.getParcelable(KEY_MODEL_STATE)!!)
            }
        }
    }
}
