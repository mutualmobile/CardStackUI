package com.mutualmobile.cardstack;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

/**
 * Displays a list of cards as a stack on the screen.
 * <p>
 * <b>XML attributes</b>
 * <p>
 * See {@link R.styleable#CardStackLayout CardStackLayout Attributes}
 * <p>
 * {@link R.styleable#CardStackLayout_showInitAnimation}
 * {@link R.styleable#CardStackLayout_card_gap}
 * {@link R.styleable#CardStackLayout_card_gap_bottom}
 * {@link R.styleable#CardStackLayout_parallax_enabled}
 * {@link R.styleable#CardStackLayout_parallax_scale}
 */
public class CardStackLayout extends ScrollView {
    public static final boolean PARALLAX_ENABLED_DEFAULT = false;
    public static final boolean SHOW_INIT_ANIMATION_DEFAULT = true;

    private float mCardGapBottom;
    private float mCardGap;
    private boolean mShowInitAnimation;
    private boolean mParallaxEnabled;
    private int mParallaxScale;
    private OnCardSelected mOnCardSelectedListener = null;

    private boolean mScrollable = true;

    private CardStackAdapter mAdapter = null;
    private FrameLayout mFrame;

    public CardStackLayout(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public CardStackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CardStackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardStackLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        resetDefaults();

        if (attrs != null) {
            final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardStackLayout, defStyleAttr, defStyleRes);
            mParallaxEnabled = a.getBoolean(R.styleable.CardStackLayout_parallax_enabled, PARALLAX_ENABLED_DEFAULT);
            mShowInitAnimation = a.getBoolean(R.styleable.CardStackLayout_showInitAnimation, SHOW_INIT_ANIMATION_DEFAULT);
            mParallaxScale = a.getInteger(R.styleable.CardStackLayout_parallax_scale, getResources().getInteger(R.integer.parallax_scale_default));
            mCardGap = a.getDimension(R.styleable.CardStackLayout_card_gap, getResources().getDimension(R.dimen.card_gap));
            mCardGapBottom = a.getDimension(R.styleable.CardStackLayout_card_gap_bottom, getResources().getDimension(R.dimen.card_gap_bottom));
            a.recycle();
        } else {
            mParallaxEnabled = PARALLAX_ENABLED_DEFAULT;
            mShowInitAnimation = SHOW_INIT_ANIMATION_DEFAULT;
            mParallaxScale = getResources().getInteger(R.integer.parallax_scale_default);
            mCardGap = getResources().getDimension(R.dimen.card_gap);
            mCardGapBottom = getResources().getDimension(R.dimen.card_gap_bottom);
        }

        setFillViewport(true);
        setVerticalScrollBarEnabled(false);

        mFrame = new CardFrameLayout(this);

        addView(mFrame, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public boolean isScrollable() {
        return mScrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mScrollable) return super.onTouchEvent(ev);

                // only continue to handle the touch event if scrolling enabled
                return false; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        return mScrollable && super.onInterceptTouchEvent(ev);
    }

    /**
     * package restricted
     */
    OnCardSelected getOnCardSelectedListener() {
        return mOnCardSelectedListener;
    }

    /**
     * Listen on card selection events for {@link CardStackLayout}. Sends clicked view and it's
     * corresponding position in the callback.
     *
     * @param onCardSelectedListener listener
     */
    public void setOnCardSelectedListener(OnCardSelected onCardSelectedListener) {
        this.mOnCardSelectedListener = onCardSelectedListener;
    }

    private void resetDefaults() {
        mOnCardSelectedListener = null;
    }

    public FrameLayout getFrame() {
        return mFrame;
    }

    /**
     * @return adapter of type {@link CardStackAdapter} that is set for this view.
     */
    public CardStackAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Set the adapter for this {@link CardStackLayout}
     *
     * @param adapter Should extend {@link CardStackAdapter}
     */
    public void setAdapter(CardStackAdapter adapter) {
        this.mAdapter = adapter;
        mAdapter.setAdapterParams(this);
        for (int i = 0; i < mAdapter.getCount(); i++) {
            mAdapter.addView(i);
        }

        if (mShowInitAnimation) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    restoreCards();
                }
            }, 500);
        }
    }

    /**
     * @return currently set parallax scale value.
     */
    public int getParallaxScale() {
        return mParallaxScale;
    }

    /**
     * Sets the value of parallax scale. Parallax scale is the factor which decides how much
     * distance a card will scroll when the user drags it down.
     */
    public void setParallaxScale(int mParallaxScale) {
        this.mParallaxScale = mParallaxScale;
    }

    public boolean isParallaxEnabled() {
        return mParallaxEnabled;
    }

    public void setParallaxEnabled(boolean mParallaxEnabled) {
        this.mParallaxEnabled = mParallaxEnabled;
    }

    public boolean isShowInitAnimation() {
        return mShowInitAnimation;
    }

    public void setShowInitAnimation(boolean mShowInitAnimation) {
        this.mShowInitAnimation = mShowInitAnimation;
    }

    /**
     * @return the gap (in pixels) between two consecutive cards
     */
    public float getCardGap() {
        return mCardGap;
    }

    /**
     * Set the gap (in pixels) between two consecutive cards
     */
    public void setCardGap(float mCardGap) {
        this.mCardGap = mCardGap;
    }

    /**
     * @return gap between the two consecutive cards when collapsed to the bottom of the screen
     */
    public float getCardGapBottom() {
        return mCardGapBottom;
    }

    public void setCardGapBottom(float mCardGapBottom) {
        this.mCardGapBottom = mCardGapBottom;
    }

    /**
     * @return true if a card is selected, false otherwise
     */
    public boolean isCardSelected() {
        return mAdapter.isCardSelected();
    }

    /**
     * Removes the adapter that was previously set using {@link #setAdapter(CardStackAdapter)}
     */
    public void removeAdapter() {
        if (mFrame.getChildCount() > 0) {
            mFrame.removeAllViews();
        }

        mAdapter = null;
        mOnCardSelectedListener = null;
    }

    /**
     * Animates the cards to their initial position in the layout.
     */
    public void restoreCards(Runnable r) {
        mAdapter.resetCards(r);
    }

    public void restoreCards() {
        restoreCards(null);
    }

    /**
     * Intimates the implementing class about the selection of a card
     */
    public interface OnCardSelected {
        void onCardSelected(View v, int position);
    }

}
