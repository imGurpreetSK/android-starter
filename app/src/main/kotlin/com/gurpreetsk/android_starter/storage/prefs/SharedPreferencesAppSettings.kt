package com.gurpreetsk.android_starter.storage.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SharedPreferencesAppSettings(private val context: Context) : AppSettings {
  private val preferences: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

  override fun putString(key: String, value: String) =
      preferences.edit()
          .putString(key, value)
          .apply()

  override fun getString(key: String, defaultValue: String): String =
      preferences.getString(key, defaultValue)
}
