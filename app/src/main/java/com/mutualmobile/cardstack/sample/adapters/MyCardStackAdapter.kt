package com.mutualmobile.cardstack.sample.adapters

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import com.mutualmobile.cardstack.CardStackAdapter
import com.mutualmobile.cardstack.sample.MainActivity
import com.mutualmobile.cardstack.sample.R.color
import com.mutualmobile.cardstack.sample.R.id
import com.mutualmobile.cardstack.sample.R.layout
import com.mutualmobile.cardstack.sample.R.string
import com.mutualmobile.cardstack.sample.interfaces.OnRestartRequest
import com.mutualmobile.cardstack.sample.utils.PrefsUtil
import com.tramsun.libs.prefcompat.Pref
import timber.log.Timber

class MyCardStackAdapter(activity: MainActivity) : CardStackAdapter(activity), CompoundButton.OnCheckedChangeListener {
  private val mInflater: LayoutInflater
  private val mContext: Context
  private val mCallback: OnRestartRequest
  private var updateSettingsView: Runnable? = null
  private var bgColorIds: IntArray

  init {
    mContext = activity
    mInflater = LayoutInflater.from(activity)
    mCallback = activity
    bgColorIds = intArrayOf(
        color.card1_bg, color.card2_bg,
        color.card3_bg, color.card4_bg,
        color.card5_bg, color.card6_bg,
        color.card7_bg,
        color.card8_bg, color.card9_bg,
        color.card10_bg, color.card11_bg,
        color.card12_bg, color.card1_bg,
        color.card2_bg
    )
  }

  override fun getCount(): Int {
    return bgColorIds.size
  }

  override fun onCheckedChanged(
    buttonView: CompoundButton,
    isChecked: Boolean
  ) {
    Timber.d("onCheckedChanged() called with: buttonView = [$buttonView], isChecked = [$isChecked]")
    when (buttonView.id) {
      id.parallax_enabled -> Pref.putBoolean(PrefsUtil.PARALLAX_ENABLED, isChecked)
      id.reverse_click_animation -> PrefsUtil.isReverseClickAnimationEnabled = isChecked
      id.show_init_animation -> Pref.putBoolean(PrefsUtil.SHOW_INIT_ANIMATION, isChecked)
    }
    updateSettingsView?.run()
  }

  override fun createView(
    position: Int,
    container: ViewGroup
  ): View {
    if (position == 0) return getSettingsView(container)

    val root = mInflater.inflate(layout.card, container, false) as CardView
    root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[position]))
    val cardTitle = root.findViewById<View>(id.card_title) as TextView
    cardTitle.text = mContext.resources.getString(string.card_title, position)
    return root
  }

  override fun getAnimatorForView(
    view: View,
    currentCardPosition: Int,
    selectedCardPosition: Int
  ): Animator {
    return if (PrefsUtil.isReverseClickAnimationEnabled) {

      val offsetTop = scrollOffset

      if (currentCardPosition > selectedCardPosition) {
        ObjectAnimator.ofFloat(view, View.Y, view.y, offsetTop + getCardFinalY(currentCardPosition))
      } else {
        ObjectAnimator.ofFloat(view, View.Y, view.y, offsetTop.toFloat() + getCardOriginalY(0) + currentCardPosition * cardGapBottom)
      }
    } else {
      super.getAnimatorForView(view, currentCardPosition, selectedCardPosition)
    }
  }

  private fun getSettingsView(container: ViewGroup): View {
    val root = mInflater.inflate(layout.settings_card, container, false) as CardView
    root.setCardBackgroundColor(ContextCompat.getColor(mContext, color.colorPaleGrey))

    val showInitAnimation = root.findViewById<View>(id.show_init_animation) as Switch
    val parallaxEnabled = root.findViewById<View>(id.parallax_enabled) as Switch
    val reverseClickAnimation = root.findViewById<View>(id.reverse_click_animation) as Switch
    val parallaxScale = root.findViewById<View>(id.parallax_scale) as EditText
    val cardGap = root.findViewById<View>(id.card_gap) as EditText
    val cardGapBottom = root.findViewById<View>(id.card_gap_bottom) as EditText

    updateSettingsView = Runnable {
      showInitAnimation.isChecked = PrefsUtil.isShowInitAnimationEnabled
      showInitAnimation.setOnCheckedChangeListener(this@MyCardStackAdapter)

      reverseClickAnimation.isChecked = PrefsUtil.isReverseClickAnimationEnabled
      reverseClickAnimation.setOnCheckedChangeListener(this@MyCardStackAdapter)

      val isParallaxEnabled = PrefsUtil.isParallaxEnabled
      parallaxEnabled.isChecked = isParallaxEnabled
      parallaxEnabled.setOnCheckedChangeListener(this@MyCardStackAdapter)

      parallaxScale.setText("${PrefsUtil.getParallaxScale(mContext)}")
      parallaxScale.isEnabled = isParallaxEnabled

      cardGap.setText("${PrefsUtil.getCardGap(mContext)}")

      cardGapBottom.setText("${PrefsUtil.getCardGapBottom(mContext)}")
    }

    updateSettingsView?.run()

    val restartActivityButton = root.findViewById<View>(id.restart_activity) as Button
    restartActivityButton.setOnClickListener {
      updatePrefsIfRequired(parallaxScale)
      updatePrefsIfRequired(cardGap)
      updatePrefsIfRequired(cardGapBottom)
      mCallback.requestRestart()
    }

    val resetDefaultsButton = root.findViewById<View>(id.reset_defaults) as Button
    resetDefaultsButton.setOnClickListener {
      PrefsUtil.resetDefaults(mContext)
      updateSettingsView?.run()
    }

    return root
  }

  private fun updatePrefsIfRequired(view: EditText) {
    val text = view.text.toString()
    val value: Int

    value = try {
      Integer.parseInt(text)
    } catch (e: Exception) {
      Integer.MIN_VALUE
    }

    if (value == Integer.MIN_VALUE) {
      Timber.e("Invalid value for ${view.resources.getResourceName(view.id)}")
      return
    }

    when (view.id) {
      id.parallax_scale -> {
        Timber.d("parallax_scale now is ${Integer.parseInt(text)}")
        Pref.putInt(PrefsUtil.PARALLAX_SCALE, Integer.parseInt(text))
      }
      id.card_gap -> {
        Timber.d("card_gap now is ${Integer.parseInt(text)}")
        Pref.putInt(PrefsUtil.CARD_GAP, Integer.parseInt(text))
      }
      id.card_gap_bottom -> {
        Timber.d("card_gap_bottom now is ${Integer.parseInt(text)}")
        Pref.putInt(PrefsUtil.CARD_GAP_BOTTOM, Integer.parseInt(text))
      }
    }
  }
}