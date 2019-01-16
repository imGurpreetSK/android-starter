package com.gurpreetsk.android_starter._http;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * A call adapter factory that churns out Observables that always observe on Android's main thread.
 **/
// TODO - Convert this to Kotlin
public class RxSchedulersCallAdapterFactory extends CallAdapter.Factory {
  private final RxJava2CallAdapterFactory adapterFactory = RxJava2CallAdapterFactory
      .createWithScheduler(Schedulers.io());

  @Override public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
    final CallAdapter callAdapter = adapterFactory.get(returnType, annotations, retrofit);

    return new CallAdapter<Observable<?>, Observable<?>>() {
      @Override public Type responseType() {
        return callAdapter.responseType();
      }

      @Override public Observable<?> adapt(@NonNull Call<Observable<?>> call) {
        return ((Observable) callAdapter.adapt(call))
            .observeOn(AndroidSchedulers.mainThread());
      }
    };
  }
}
