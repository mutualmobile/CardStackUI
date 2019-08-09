package com.mutualmobile.cardstack.utils

import android.content.Context
import android.util.TypedValue
import android.view.View

object Units {
  fun dpToPx(
    context: Context,
    dp: Int
  ): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)
        .toInt()
  }

  fun getAbsoluteTop(view: View): Int {
    val rect = IntArray(2)
    view.getLocationInWindow(rect)
    return rect[1]
  }
}