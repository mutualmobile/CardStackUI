package com.mutualmobile.cardstack

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import java.util.ArrayList

/**
 * This class acts as an adapter for the [CardStackLayout] view. This adapter is intentionally
 * made an abstract class with following abstract methods -
 *
 *
 *
 *
 * [.getCount] - Decides the number of views present in the view
 *
 *
 * [.createView] - Creates the view for all positions in range [0, [.getCount])
 *
 *
 * Contains the logic for touch events in [.onTouch]
 */
abstract class CardStackAdapter(context: Context) : View.OnTouchListener, View.OnClickListener {
  companion object {
    // Animation constants
    const val ANIM_DURATION = 600
    const val DECELERATION_FACTOR = 2
    const val INVALID_CARD_POSITION = -1
  }

  private val mScreenHeight: Int
  private val dp30: Int
  private val maxDistanceToConsiderAsClick: Float

  // Settings for the adapter from layout
  protected var cardGapBottom: Float = 0F
  private var mCardGap: Float = 0F
  private var mParallaxScale: Int = 0
  private var mParallaxEnabled: Boolean = false
  private var mShowInitAnimation: Boolean = false
  private var mScrollParent: CardStackLayout? = null
  private var mFrame: FrameLayout? = null
  private var mCardViews: ArrayList<View> = arrayListOf()

  var fullCardHeight: Int = 0
    private set

  /**
   * Returns true if no animation is in progress currently. Can be used to disable any events
   * if they are not allowed during an animation. Returns false if an animation is in progress.
   *
   * @return - true if animation in progress, false otherwise
   */
  var isScreenTouchable = false
    private set
  private var mTouchFirstY = -1f
  private var mTouchPrevY = -1f
  private var mTouchDistance = 0f
  /**
   * Returns the position of selected card. If no card
   * is selected, returns [.INVALID_CARD_POSITION]
   */
  var selectedCardPosition = INVALID_CARD_POSITION
    private set
  private val scaleFactorForElasticEffect: Float
  private var mParentPaddingTop = 0
  private var mCardPaddingInternal = 0

  /**
   * Defines the number of cards that are present in the [CardStackLayout]
   *
   * @return cardCount - Number of views in the related [CardStackLayout]
   */
  abstract val count: Int

  protected val scrollOffset: Int
    get() = mScrollParent?.scrollY ?: 0

  /**
   * Returns false if all the cards are in their initial position i.e. no card is selected
   *
   *
   * Returns true if the [CardStackLayout] has a card selected and all other cards are
   * at the bottom of the screen.
   *
   * @return true if any card is selected, false otherwise
   */
  val isCardSelected: Boolean
    get() = selectedCardPosition != INVALID_CARD_POSITION

  init {
    val resources = context.resources

    val dm = Resources.getSystem()
        .displayMetrics
    mScreenHeight = dm.heightPixels
    dp30 = resources.getDimension(R.dimen.dp30)
        .toInt()
    scaleFactorForElasticEffect = resources.getDimension(R.dimen.dp8)
        .toInt()
        .toFloat()
    maxDistanceToConsiderAsClick = resources.getDimension(R.dimen.dp8)
        .toInt()
        .toFloat()
  }

  /**
   * Defines and initializes the view to be shown in the [CardStackLayout]
   * Provides two parameters to the sub-class namely -
   *
   * @param position
   * @param container
   * @return View corresponding to the position and parent container
   */
  abstract fun createView(
    position: Int,
    container: ViewGroup?
  ): View

  internal fun addView(position: Int) {
    val root = createView(position, mFrame)
    root.setOnTouchListener(this)
    root.setTag(R.id.cardstack_internal_position_tag, position)
    root.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    root.isFocusable = true
    root.isFocusableInTouchMode = true

    mCardPaddingInternal = root.paddingTop

    val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullCardHeight)
    root.layoutParams = lp
    if (mShowInitAnimation) {
      root.y = getCardFinalY(position)
      isScreenTouchable = false
    } else {
      root.y = getCardOriginalY(position) - mParentPaddingTop
      isScreenTouchable = true
    }

    mCardViews[position] = root

    mFrame?.addView(root)
  }

  protected fun getCardFinalY(position: Int): Float {
    return mScreenHeight.toFloat() - dp30.toFloat() - (count - position) * cardGapBottom - mCardPaddingInternal.toFloat()
  }

  protected fun getCardOriginalY(position: Int): Float {
    return mParentPaddingTop + mCardGap * position
  }

  /**
   * Resets all cards in [CardStackLayout] to their initial positions
   *
   * @param r Execute r.run() once the reset animation is done
   */
  @JvmOverloads
  fun resetCards(r: Runnable? = null) {
    val animations = ArrayList<Animator>(count)
    for (i in 0 until count) {
      val child = mCardViews[i]
      animations.add(ObjectAnimator.ofFloat<View>(child, View.Y, child.y, getCardOriginalY(i)))
    }
    startAnimations(animations, r, true)
  }

  /**
   * Plays together all animations passed in as parameter. Once animation is completed, r.run() is
   * executed. If parameter isReset is set to true, [.mSelectedCardPosition] is set to [.INVALID_CARD_POSITION]
   *
   * @param animations
   * @param r
   * @param isReset
   */
  private fun startAnimations(
    animations: List<Animator>,
    r: Runnable?,
    isReset: Boolean
  ) {
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(animations)
    animatorSet.duration = ANIM_DURATION.toLong()
    animatorSet.interpolator = DecelerateInterpolator(DECELERATION_FACTOR.toFloat())
    animatorSet.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        r?.run()

        isScreenTouchable = true

        if (isReset) {
          selectedCardPosition = INVALID_CARD_POSITION
          mScrollParent?.setScrollingEnabled(true)
        }
      }
    })
    animatorSet.start()
  }

  override fun onTouch(
    v: View,
    event: MotionEvent
  ): Boolean {
    if (!isScreenTouchable) {
      return false
    }

    val y = event.rawY
    val positionOfCardToMove = v.getTag(R.id.cardstack_internal_position_tag) as Int

    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        if (mTouchFirstY != -1f) {
          return false
        }
        mTouchFirstY = y
        mTouchPrevY = mTouchFirstY
        mTouchDistance = 0f
      }
      MotionEvent.ACTION_MOVE -> {
        if (selectedCardPosition == INVALID_CARD_POSITION)
          moveCards(positionOfCardToMove, y - mTouchFirstY)
        mTouchDistance += Math.abs(y - mTouchPrevY)
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        if (mTouchDistance < maxDistanceToConsiderAsClick && Math.abs(y - mTouchFirstY) < maxDistanceToConsiderAsClick) {
          if (selectedCardPosition == INVALID_CARD_POSITION) {
            onClick(v)
          } else {
            resetCards()
          }
        }
        mTouchFirstY = -1f
        mTouchPrevY = mTouchFirstY
        mTouchDistance = 0f
        return false
      }
    }
    return true
  }

  override fun onClick(v: View) {

    if (!isScreenTouchable) {
      return
    }
    isScreenTouchable = false

    mScrollParent?.setScrollingEnabled(false)
    if (selectedCardPosition == INVALID_CARD_POSITION) {
      selectedCardPosition = v.getTag(R.id.cardstack_internal_position_tag) as Int

      val animations = ArrayList<Animator>(count)
      for (i in 0 until count) {
        val child = mCardViews[i]
        animations.add(getAnimatorForView(child, i, selectedCardPosition))
      }
      startAnimations(animations, Runnable {
        isScreenTouchable = true
        if (mScrollParent?.onCardSelectedListener != null) {
          mScrollParent?.onCardSelectedListener?.onCardSelected(v, selectedCardPosition)
        }
      }, false)

    }
  }

  /**
   * This method can be overridden to have different animations for each card when a click event
   * happens on any card view. This method will be called for every
   *
   * @param view                 The view for which this method needs to return an animator
   * @param selectedCardPosition Position of the card that was clicked
   * @param currentCardPosition  Position of the current card
   * @return animator which has to be applied on the current card
   */
  protected fun getAnimatorForView(
    view: View,
    currentCardPosition: Int,
    selectedCardPosition: Int
  ): Animator {
    val offsetTop = scrollOffset

    return if (currentCardPosition != selectedCardPosition) {
      ObjectAnimator.ofFloat(view, View.Y, view.y, offsetTop + getCardFinalY(currentCardPosition))
    } else {
      ObjectAnimator.ofFloat(view, View.Y, view.y, offsetTop + getCardOriginalY(0))
    }
  }

  private fun moveCards(
    positionOfCardToMove: Int,
    diff: Float
  ) {
    if (diff < 0 || positionOfCardToMove < 0 || positionOfCardToMove >= count) return
    for (i in positionOfCardToMove until count) {
      val child = mCardViews[i]
      var diffCard = diff / scaleFactorForElasticEffect
      diffCard = if (mParallaxEnabled) {
        if (mParallaxScale > 0) {
          diffCard * (mParallaxScale / 3).toFloat() * (count + 1 - i).toFloat()
        } else {
          val scale = mParallaxScale * -1
          diffCard * (i * (scale / 3) + 1)
        }
      } else
        diffCard * (count * 2 + 1)
      child.y = getCardOriginalY(i) + diffCard
    }
  }

  /**
   * Provides an API to [CardStackLayout] to set the parameters provided to it in its XML
   *
   * @param cardStackLayout Parent of all cards
   */
  internal fun setAdapterParams(cardStackLayout: CardStackLayout) {
    mScrollParent = cardStackLayout
    mFrame = cardStackLayout.frame

    cardGapBottom = cardStackLayout.cardGapBottom
    mCardGap = cardStackLayout.cardGap
    mParallaxScale = cardStackLayout.parallaxScale
    mParallaxEnabled = cardStackLayout.isParallaxEnabled
    if (mParallaxEnabled && mParallaxScale == 0)
      mParallaxEnabled = false
    mShowInitAnimation = cardStackLayout.isShowInitAnimation

    mParentPaddingTop = cardStackLayout.paddingTop
    fullCardHeight = (mScreenHeight.toFloat() - dp30.toFloat() - count * cardGapBottom).toInt()
  }

  /**
   * Since there is no view recycling in [CardStackLayout], we maintain an instance of every
   * view that is set for every position. This method returns a view at the requested position.
   *
   * @param position Position of card in [CardStackLayout]
   * @return View at requested position
   */
  fun getCardView(position: Int) = mCardViews[position]
}