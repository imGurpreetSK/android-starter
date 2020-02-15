package com.gurpreetsk.android_starter._mvi

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

private const val KEY_MODEL_STATE = "model_state"

@FlowPreview
@ExperimentalCoroutinesApi
abstract class MviFragment<T : Parcelable> : Fragment() {
    protected val lifecycle: ConflatedBroadcastChannel<MviLifecycle> by lazy {
        ConflatedBroadcastChannel<MviLifecycle>()
    }

    private val stateRelay: ConflatedBroadcastChannel<T> by lazy {
        ConflatedBroadcastChannel<T>()
    }

    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val mainScope = CoroutineScope(Dispatchers.Main + job)

    protected val lastKnownState: T?
        get() = stateRelay.value

    private lateinit var lifecycleEvent: MviLifecycle

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
    protected abstract fun bind(states: Flow<T>): Flow<T>

    /**
     * Free any resources that were acquired in your Activity.
     */
    protected open fun unbind() {}

    /**
     * This method receives state emissions from the bound model.
     * @param state The state that was recently emitted by the model.
     */
    protected abstract fun effects(state: T)

    override fun onAttach(context: Context) {
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

    private fun restoreLastKnownState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            ioScope.launch {
                stateRelay.send(savedInstanceState.getParcelable(KEY_MODEL_STATE)!!)
            }
        }
    }
}
