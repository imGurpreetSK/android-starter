package com.gurpreetsk.android_starter.di.modules

import android.content.Context
import com.gurpreetsk.android_starter._http.BigDecimalJsonAdapter
import com.gurpreetsk.android_starter._http.RxSchedulersCallAdapterFactory
import com.gurpreetsk.android_starter._http.StarterApi
import com.gurpreetsk.android_starter._http.utils.enableTls12OnPreLollipop
import com.gurpreetsk.android_starter.storage.prefs.AppSettings
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.math.BigDecimal
import java.util.Date

private const val HTTP_DISK_CACHE_SIZE = 50 * 1000L // 50 MB
const val BASE_URL = "app_base_url"

@Module class NetworkModule {
  @Provides fun provideBaseUrl(appSettings: AppSettings): String =
      appSettings.getString(BASE_URL, "")

  @Provides fun provideHttpCache(context: Context): Cache {
    val cacheDir = File(context.cacheDir, "http")
    return Cache(cacheDir, HTTP_DISK_CACHE_SIZE)
  }

  @Provides fun provideOkHttpClient(
      cache: Cache,
      jwtInterceptor: Interceptor
  ): OkHttpClient {
    val okHttpBuilder = OkHttpClient
        .Builder()
        .addInterceptor(jwtInterceptor)
        .cache(cache)

    return okHttpBuilder
        .enableTls12OnPreLollipop()
        .build()
  }

  @Provides fun provideCallAdapterFactory(): CallAdapter.Factory =
      RxJava2CallAdapterFactory.create()

  @Provides fun provideMoshi(): Moshi =
      Moshi.Builder()
          .add(BigDecimal::class.java, BigDecimalJsonAdapter())
          .add(Date::class.java, Rfc3339DateJsonAdapter())
          .build()

  @Provides fun provideRetrofit(
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

  @Provides fun provideStarterApi(retrofit: Retrofit): StarterApi =
      retrofit.create(StarterApi::class.java)
}
