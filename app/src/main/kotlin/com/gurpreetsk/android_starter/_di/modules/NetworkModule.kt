package com.gurpreetsk.android_starter._di.modules

import android.content.Context
import com.gurpreetsk.android_starter.BuildConfig
import com.gurpreetsk.android_starter._http.BigDecimalJsonAdapter
import com.gurpreetsk.android_starter._http.RxSchedulersCallAdapterFactory
import com.gurpreetsk.android_starter._http.StarterApi
import com.gurpreetsk.android_starter._http.utils.enableTls12OnPreLollipop
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.math.BigDecimal
import java.util.Date
import javax.inject.Singleton

private const val HTTP_DISK_CACHE_SIZE = 50 * 1000L // 50 MB

@Module object NetworkModule {
  @JvmStatic @Provides fun provideBaseUrl(): String =
      BuildConfig.BASE_URL

  @JvmStatic @Provides @Singleton
  fun provideHttpCache(context: Context): Cache {
    val cacheDir = File(context.cacheDir, "http")
    return Cache(cacheDir, HTTP_DISK_CACHE_SIZE)
  }

  @JvmStatic @Provides @Singleton
  fun provideOkHttpClient(
      context: Context,
      cache: Cache
  ): OkHttpClient {
    val okHttpBuilder = OkHttpClient
        .Builder()
        .cache(cache)
        .addInterceptor(ChuckInterceptor(context))

    return okHttpBuilder
        .enableTls12OnPreLollipop()
        .build()
  }

  @JvmStatic @Provides fun provideCallAdapterFactory(): CallAdapter.Factory =
      RxJava2CallAdapterFactory.create()

  @JvmStatic @Provides fun provideMoshi(): Moshi =
      Moshi.Builder()
          .add(BigDecimal::class.java, BigDecimalJsonAdapter())
          .add(Date::class.java, Rfc3339DateJsonAdapter())
          .build()

  @JvmStatic @Provides @Singleton
  fun provideRetrofit(
      baseUrl: String,
      okHttpClient: OkHttpClient,
      callAdapterFactory: CallAdapter.Factory,
      moshi: Moshi): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(RxSchedulersCallAdapterFactory())
        .addCallAdapterFactory(callAdapterFactory)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
  }

  @JvmStatic @Provides @Singleton
  fun provideStarterApi(retrofit: Retrofit): StarterApi =
      retrofit.create(StarterApi::class.java)
}
