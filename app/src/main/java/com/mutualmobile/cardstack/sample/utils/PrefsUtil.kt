package com.mutualmobile.cardstack.sample.utils

import android.content.Context

import com.mutualmobile.cardstack.CardStackLayout
import com.mutualmobile.cardstack.sample.R.dimen
import com.tramsun.libs.prefcompat.Pref

object PrefsUtil {

  const val SHOW_INIT_ANIMATION = "showInitAnimation"
  const val PARALLAX_ENABLED = "parallaxEnabled"
  const val PARALLAX_SCALE = "parallaxScale"
  const val CARD_GAP = "cardGap"
  const val CARD_GAP_BOTTOM = "cardGapBottom"
  private const val REVERSE_CLICK_ANIMATION_ENABLED = "reverseClickAnimationEnabled"
  private const val REVERSE_CLICK_ANIMATION_ENABLED_DEFAULT = false

  val isShowInitAnimationEnabled: Boolean
    get() = Pref.getBoolean(SHOW_INIT_ANIMATION, CardStackLayout.SHOW_INIT_ANIMATION_DEFAULT)

  val isParallaxEnabled: Boolean
    get() = Pref.getBoolean(PARALLAX_ENABLED, CardStackLayout.PARALLAX_ENABLED_DEFAULT)

  var isReverseClickAnimationEnabled: Boolean
    get() = Pref.getBoolean(
        REVERSE_CLICK_ANIMATION_ENABLED,
        REVERSE_CLICK_ANIMATION_ENABLED_DEFAULT
    )
    set(b) = Pref.putBoolean(REVERSE_CLICK_ANIMATION_ENABLED, b)

  fun getParallaxScale(context: Context): Int {
    return Pref.getInt(PARALLAX_SCALE, context.resources.getInteger(com.mutualmobile.cardstack.R.integer.parallax_scale_default))
  }

  fun getCardGap(context: Context): Int {
    val cardGapDimenInDp = (context.resources.getDimension(dimen.card_gap) / context.resources.displayMetrics.density).toInt()
    return Pref.getInt(CARD_GAP, cardGapDimenInDp)
  }

  fun getCardGapBottom(context: Context): Int {
    val cardGapBottomDimenInDp = (context.resources.getDimension(dimen.card_gap_bottom) / context.resources.displayMetrics.density).toInt()
    return Pref.getInt(CARD_GAP_BOTTOM, cardGapBottomDimenInDp)
  }

  fun resetDefaults(context: Context) {
    val cardGapDimenInDp = (context.resources.getDimension(dimen.card_gap) / context.resources.displayMetrics.density).toInt()
    val cardGapBottomDimenInDp = (context.resources.getDimension(dimen.card_gap_bottom) / context.resources.displayMetrics.density).toInt()

    Pref.putBoolean(SHOW_INIT_ANIMATION, CardStackLayout.SHOW_INIT_ANIMATION_DEFAULT)
    Pref.putBoolean(PARALLAX_ENABLED, CardStackLayout.PARALLAX_ENABLED_DEFAULT)
    isReverseClickAnimationEnabled =
      REVERSE_CLICK_ANIMATION_ENABLED_DEFAULT
    Pref.putInt(PARALLAX_SCALE, context.resources.getInteger(com.mutualmobile.cardstack.R.integer.parallax_scale_default))
    Pref.putInt(CARD_GAP, cardGapDimenInDp)
    Pref.putInt(CARD_GAP_BOTTOM, cardGapBottomDimenInDp)
  }
}