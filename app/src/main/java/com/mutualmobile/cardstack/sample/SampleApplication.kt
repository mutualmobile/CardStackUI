package com.mutualmobile.cardstack.sample

import android.app.Application
import com.tramsun.libs.prefcompat.Pref
import timber.log.Timber
import timber.log.Timber.DebugTree

class SampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Pref.init(this)

    if (BuildConfig.DEBUG) {
      Timber.plant(DebugTree())
    }
  }
}