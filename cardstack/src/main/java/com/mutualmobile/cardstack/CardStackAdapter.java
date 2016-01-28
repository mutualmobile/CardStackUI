package com.mutualmobile.cardstack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class CardStackAdapter implements View.OnTouchListener, View.OnClickListener {


    public static final int ANIM_DURATION = 600;
    public static final int DECELERATION_FACTOR = 2;

    // Settings for the adapter from layout
    private float mCardGapBottom;
    private float mCardGap;
    private int mParallaxScale;
    private boolean mParallaxEnabled;
    private boolean mShowInitAnimation;

    private final int mScreenHeight;
    private int fullCardHeight;

    private View[] mCardViews;

    private float dp8;
    private final int dp30;

    private CardStackLayout mParent;

    private boolean mScreenTouchable = false;
    private float mTouchFirstY = -1;
    private float mTouchPrevY = -1;
    private float mTouchDistance = 0;
    private int mSelectedCardPosition = -1;
    private float scaleFactorForElasticEffect;
    private int mParentPaddingTop = 0;
    private int mCardPaddingInternal = 0;

    public View getCardView(int position) {
        if (mCardViews == null) return null;

        return mCardViews[position];
    }

    public abstract View createView(int position, ViewGroup container);

    public abstract int getCount();

    public void setScreenTouchable(boolean screenTouchable) {
        this.mScreenTouchable = screenTouchable;
    }

    public boolean isScreenTouchable() {
        return mScreenTouchable;
    }

    public CardStackAdapter(Context context) {
        Resources resources = context.getResources();

        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        mScreenHeight = dm.heightPixels;
        dp30 = (int) resources.getDimension(R.dimen.dp30);
        scaleFactorForElasticEffect = (int) resources.getDimension(R.dimen.dp8);
        dp8 = (int) resources.getDimension(R.dimen.dp8);

        mCardViews = new View[getCount()];
    }

    public void addView(final int position) {
        View root = createView(position, mParent);
        root.setOnTouchListener(this);
        root.setTag(R.id.cardstack_internal_position_tag, position);
        root.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mCardPaddingInternal = root.getPaddingTop();

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullCardHeight);
        root.setLayoutParams(lp);
        if (mShowInitAnimation) {
            root.setY(getCardFinalY(position));
            setScreenTouchable(false);
        } else {
            root.setY(getCardOriginalY(position) - mParentPaddingTop);
            setScreenTouchable(true);
        }

        mCardViews[position] = root;

        mParent.addView(root);
    }

    public float getCardFinalY(int position) {
        return mScreenHeight - dp30 - ((getCount() - position) * mCardGapBottom) - mCardPaddingInternal;
    }

    private float getCardOriginalY(int position) {
        return mParentPaddingTop + mCardGap * position;
    }

    public void resetCards(Runnable r) {
        List<Animator> animations = new ArrayList<>(getCount());
        for (int i = 0; i < getCount(); i++) {
            final View child = mCardViews[i];
            animations.add(ObjectAnimator.ofFloat(child, View.Y, (int) child.getY(), getCardOriginalY(i)));
        }
        startAnimations(animations, r, true);
    }

    private void startAnimations(List<Animator> animations, final Runnable r, final boolean isReset) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animations);
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.setInterpolator(new DecelerateInterpolator(DECELERATION_FACTOR));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (r != null) r.run();
                setScreenTouchable(true);
                if (isReset)
                    mSelectedCardPosition = -1;
            }
        });
        animatorSet.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isScreenTouchable()) {
            return false;
        }

        float y = event.getRawY();
        int positionOfCardToMove = (int) v.getTag(R.id.cardstack_internal_position_tag);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mTouchFirstY != -1) {
                    return false;
                }
                mTouchPrevY = mTouchFirstY = y;
                mTouchDistance = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSelectedCardPosition == -1)
                    moveCards(positionOfCardToMove, y - mTouchFirstY);
                mTouchDistance += Math.abs(y - mTouchPrevY);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchDistance < dp8 && Math.abs(y - mTouchFirstY) < dp8 && mSelectedCardPosition == -1) {
                    onClick(v);
                } else {
                    resetCards();
                }
                mTouchPrevY = mTouchFirstY = -1;
                mTouchDistance = 0;
                return false;
        }
        return true;
    }

    @Override
    public void onClick(final View v) {

        if (!isScreenTouchable()) {
            return;
        }
        setScreenTouchable(false);
        if (mSelectedCardPosition == -1) {
            mSelectedCardPosition = (int) v.getTag(R.id.cardstack_internal_position_tag);

            List<Animator> animations = new ArrayList<>(getCount());
            for (int i = 0; i < getCount(); i++) {
                View child = mCardViews[i];
                if (i != mSelectedCardPosition) {
                    animations.add(ObjectAnimator.ofFloat(child, View.Y, (int) child.getY(), getCardFinalY(i)));
                } else {
                    animations.add(ObjectAnimator.ofFloat(child, View.Y, (int) child.getY(), getCardOriginalY(0)));
                }
            }
            startAnimations(animations, new Runnable() {
                @Override
                public void run() {
                    setScreenTouchable(true);
                    if (mParent.getOnCardSelectedListener() != null) {
                        mParent.getOnCardSelectedListener().onCardSelected(v, mSelectedCardPosition);
                    }
                }
            }, false);

        }
    }

    public void moveCards(int positionOfCardToMove, float diff) {
        if (diff < 0 || positionOfCardToMove < 0 || positionOfCardToMove >= getCount()) return;
        for (int i = positionOfCardToMove; i < getCount(); i++) {
            final View child = mCardViews[i];
            float diffCard = diff / scaleFactorForElasticEffect;
            if (mParallaxEnabled) {
                if (mParallaxScale > 0) {
                    diffCard = diffCard * (mParallaxScale / 3) * (getCount() + 1 - i);
                } else {
                    int scale = mParallaxScale * -1;
                    diffCard = diffCard * (i * (scale / 3) + 1);
                }
            } else diffCard = diffCard * (getCount() * 2 + 1);
            child.setY(getCardOriginalY(i) + diffCard);
        }
    }

    public void setAdapterParams(CardStackLayout cardStackLayout) {
        mParent = cardStackLayout;
        mCardGapBottom = cardStackLayout.getCardGapBottom();
        mCardGap = cardStackLayout.getCardGap();
        mParallaxScale = cardStackLayout.getParallaxScale();
        mParallaxEnabled = cardStackLayout.isParallaxEnabled();
        if (mParallaxEnabled && mParallaxScale == 0)
            mParallaxEnabled = false;
        mShowInitAnimation = cardStackLayout.isShowInitAnimation();
        mParentPaddingTop = cardStackLayout.getPaddingTop();
        fullCardHeight = (int) (mScreenHeight - dp30 - dp8 - getCount() * mCardGapBottom);
    }

    public void resetCards() {
        resetCards(null);
    }

    public boolean isCardSelected() {
        return mSelectedCardPosition != -1;
    }
}
