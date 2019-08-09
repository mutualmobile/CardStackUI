package com.mutualmobile.cardstack

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView

/**
 * Displays a list of cards as a stack on the screen.
 *
 *
 * **XML attributes**
 *
 *
 * See [CardStackLayout Attributes][R.styleable.CardStackLayout]
 *
 *
 * [R.styleable.CardStackLayout_showInitAnimation]
 * [R.styleable.CardStackLayout_card_gap]
 * [R.styleable.CardStackLayout_card_gap_bottom]
 * [R.styleable.CardStackLayout_parallax_enabled]
 * [R.styleable.CardStackLayout_parallax_scale]
 */
class CardStackLayout : ScrollView {
  companion object {
    const val PARALLAX_ENABLED_DEFAULT = false
    const val SHOW_INIT_ANIMATION_DEFAULT = true
  }

  /**
   * @return gap between the two consecutive cards when collapsed to the bottom of the screen
   */
  var cardGapBottom: Float = 0.toFloat()
  /**
   * @return the gap (in pixels) between two consecutive cards
   */
  /**
   * Set the gap (in pixels) between two consecutive cards
   */
  var cardGap: Float = 0.toFloat()
  var isShowInitAnimation: Boolean = false
  var isParallaxEnabled: Boolean = false
  /**
   * @return currently set parallax scale value.
   */
  /**
   * Sets the value of parallax scale. Parallax scale is the factor which decides how much
   * distance a card will scroll when the user drags it down.
   */
  var parallaxScale: Int = 0
  /**
   * package restricted
   */
  /**
   * Listen on card selection events for [CardStackLayout]. Sends clicked view and it's
   * corresponding position in the callback.
   *
   * @param onCardSelectedListener listener
   */
  internal var onCardSelectedListener: OnCardSelected? = null

  var isScrollable = true
    private set

  private var mAdapter: CardStackAdapter? = null
  var frame: FrameLayout? = null
    private set

  /**
   * @return adapter of type [CardStackAdapter] that is set for this view.
   */
  /**
   * Set the adapter for this [CardStackLayout]
   *
   * @param adapter Should extend [CardStackAdapter]
   */
  var adapter: CardStackAdapter?
    get() = mAdapter
    set(adapter) {
      this.mAdapter = adapter
      mAdapter?.setAdapterParams(this)
      mAdapter?.count?.let { count ->
        for (i in 0 until count) {
          mAdapter?.addView(i)
        }
      }

      if (isShowInitAnimation) {
        postDelayed({ restoreCards() }, 500)
      }
    }

  /**
   * @return true if a card is selected, false otherwise
   */
  val isCardSelected: Boolean
    get() = mAdapter?.isCardSelected ?: false

  constructor(context: Context) : super(context) {
    init(context, null, 0, 0)
  }

  constructor(
    context: Context,
    attrs: AttributeSet
  ) : super(context, attrs) {
    init(context, attrs, 0, 0)
  }

  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr) {
    init(context, attrs, defStyleAttr, 0)
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int,
    defStyleRes: Int
  ) : super(context, attrs, defStyleAttr, defStyleRes) {
    init(context, attrs, defStyleAttr, defStyleRes)
  }

  private fun init(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
  ) {
    resetDefaults()

    if (attrs != null) {
      val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CardStackLayout, defStyleAttr, defStyleRes)
      isParallaxEnabled = a.getBoolean(R.styleable.CardStackLayout_parallax_enabled, PARALLAX_ENABLED_DEFAULT)
      isShowInitAnimation = a.getBoolean(R.styleable.CardStackLayout_showInitAnimation, SHOW_INIT_ANIMATION_DEFAULT)
      parallaxScale = a.getInteger(R.styleable.CardStackLayout_parallax_scale, resources.getInteger(R.integer.parallax_scale_default))
      cardGap = a.getDimension(R.styleable.CardStackLayout_card_gap, resources.getDimension(R.dimen.card_gap))
      cardGapBottom = a.getDimension(R.styleable.CardStackLayout_card_gap_bottom, resources.getDimension(R.dimen.card_gap_bottom))
      a.recycle()
    } else {
      isParallaxEnabled = PARALLAX_ENABLED_DEFAULT
      isShowInitAnimation = SHOW_INIT_ANIMATION_DEFAULT
      parallaxScale = resources.getInteger(R.integer.parallax_scale_default)
      cardGap = resources.getDimension(R.dimen.card_gap)
      cardGapBottom = resources.getDimension(R.dimen.card_gap_bottom)
    }

    isFillViewport = true
    isVerticalScrollBarEnabled = false

    frame = CardFrameLayout(this)

    addView(frame, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
  }

  fun setScrollingEnabled(enabled: Boolean) {
    isScrollable = enabled
  }

  override fun onTouchEvent(ev: MotionEvent): Boolean {
    return when (ev.action) {
      MotionEvent.ACTION_DOWN -> {
        // if we can scroll pass the event to the superclass
        if (isScrollable) super.onTouchEvent(ev) else false

        // only continue to handle the touch event if scrolling enabled
        // mScrollable is always false at this point
      }
      else -> super.onTouchEvent(ev)
    }
  }

  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    // Don't do anything with intercepted touch events if
    // we are not scrollable
    return isScrollable && super.onInterceptTouchEvent(ev)
  }

  private fun resetDefaults() {
    onCardSelectedListener = null
  }

  /**
   * Removes the adapter that was previously set using [.setAdapter]
   */
  fun removeAdapter() {
    frame?.childCount?.let {
      if (it > 0) {
        frame?.removeAllViews()
      }
    }

    mAdapter = null
    onCardSelectedListener = null
  }

  /**
   * Animates the cards to their initial position in the layout.
   */
  @JvmOverloads
  fun restoreCards(r: Runnable? = null) {
    mAdapter?.resetCards(r)
  }

  /**
   * Intimates the implementing class about the selection of a card
   */
  interface OnCardSelected {
    fun onCardSelected(
      v: View,
      position: Int
    )
  }
}