package com.mutualmobile.cardstack

import android.annotation.SuppressLint
import android.view.View
import android.widget.FrameLayout

/**
 * Created by tushar on 7/18/16.
 */
@SuppressLint("ViewConstructor")
internal class CardFrameLayout(private val parent: CardStackLayout) : FrameLayout(parent.context) {

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    var heightMeasureSpecs = heightMeasureSpec
    val mAdapter = parent.adapter
    if (mAdapter != null) {
      val height = (mAdapter.fullCardHeight + (mAdapter.count - 1) * parent.cardGap).toInt()
      heightMeasureSpecs = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpecs)
  }
}