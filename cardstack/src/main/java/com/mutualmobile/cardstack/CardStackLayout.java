package com.mutualmobile.cardstack;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class CardStackLayout extends FrameLayout {
    public static final boolean PARALLAX_ENABLED_DEFAULT = false;
    public static final boolean SHOW_INIT_ANIMATION_DEFAULT = true;

    private float mCardGapBottom;
    private float mCardGap;
    private boolean mShowInitAnimation;
    private boolean mParallaxEnabled;
    private int mParallaxScale;
    private OnCardSelected mOnCardSelectedListener = null;

    private CardStackAdapter mAdapter = null;

    public CardStackLayout(Context context) {
        super(context);
        resetDefaults();
    }

    public OnCardSelected getOnCardSelectedListener() {
        return mOnCardSelectedListener;
    }

    public void setOnCardSelected(OnCardSelected onCardSelectedListener) {
        this.mOnCardSelectedListener = onCardSelectedListener;
    }

    private void resetDefaults() {
        mOnCardSelectedListener = null;
    }

    public CardStackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardStackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardStackLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        resetDefaults();
        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CardStackLayout, defStyleAttr, defStyleRes);
        mParallaxEnabled = a.getBoolean(R.styleable.CardStackLayout_parallax_enabled, PARALLAX_ENABLED_DEFAULT);
        mShowInitAnimation = a.getBoolean(R.styleable.CardStackLayout_showInitAnimation, SHOW_INIT_ANIMATION_DEFAULT);
        mParallaxScale = a.getInteger(R.styleable.CardStackLayout_parallax_scale, getResources().getInteger(R.integer.parallax_scale_default));
        mCardGap = a.getDimension(R.styleable.CardStackLayout_card_gap, getResources().getDimension(R.dimen.card_gap));
        mCardGapBottom = a.getDimension(R.styleable.CardStackLayout_card_gap_bottom, getResources().getDimension(R.dimen.card_gap_bottom));
        a.recycle();

    }

    public CardStackAdapter getAdapter() {
        return mAdapter;
    }

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

    public int getParallaxScale() {
        return mParallaxScale;
    }

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

    public float getCardGap() {
        return mCardGap;
    }

    public void setCardGap(float mCardGap) {
        this.mCardGap = mCardGap;
    }

    public float getCardGapBottom() {
        return mCardGapBottom;
    }

    public void setCardGapBottom(float mCardGapBottom) {
        this.mCardGapBottom = mCardGapBottom;
    }

    public boolean isCardSelected() {
        return mAdapter.isCardSelected();
    }

    public void removeAdapter() {
        if (getChildCount() > 0)
            removeAllViews();
        mAdapter = null;
        mOnCardSelectedListener = null;
    }

    public void restoreCards() {
        mAdapter.resetCards();
    }

    public interface OnCardSelected {
        void onCardSelected(View v, int position);
    }

}
