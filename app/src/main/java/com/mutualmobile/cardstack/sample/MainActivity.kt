package com.mutualmobile.cardstack.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import com.mutualmobile.cardstack.CardStackLayout
import com.mutualmobile.cardstack.sample.adapters.MyCardStackAdapter
import com.mutualmobile.cardstack.sample.interfaces.OnRestartRequest
import com.mutualmobile.cardstack.sample.utils.PrefsUtil
import com.mutualmobile.cardstack.utils.Units
import timber.log.Timber

class MainActivity : AppCompatActivity(), OnRestartRequest {

  private var mCardStackLayout: CardStackLayout? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    mCardStackLayout = findViewById<View>(R.id.cardStack) as CardStackLayout
    setupAdapter()
  }

  private fun setupAdapter() {

    mCardStackLayout?.isShowInitAnimation = PrefsUtil.isShowInitAnimationEnabled

    mCardStackLayout?.isParallaxEnabled = PrefsUtil.isParallaxEnabled
    mCardStackLayout?.parallaxScale = PrefsUtil.getParallaxScale(this)

    mCardStackLayout?.cardGap = Units.dpToPx(this, PrefsUtil.getCardGap(this))
        .toFloat()
    mCardStackLayout?.cardGapBottom = Units.dpToPx(this, PrefsUtil.getCardGapBottom(this))
        .toFloat()

    mCardStackLayout?.adapter = MyCardStackAdapter(this)
  }

  override fun onBackPressed() {
    mCardStackLayout?.let {
      if (it.isCardSelected) {
        mCardStackLayout?.restoreCards()
      } else {
        super.onBackPressed()
      }
    }
  }

  override fun requestRestart() {
    Timber.d("Restarting MainActivity..")
    mCardStackLayout?.removeAdapter()

    mCardStackLayout?.postDelayed({ setupAdapter() }, 200)
  }
}