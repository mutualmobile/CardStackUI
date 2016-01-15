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

    Logger log = new Logger(CardStackAdapter.class.getSimpleName());

    public static final int ANIM_DURATION = 600;
    public static final int DECELERATION_FACTOR = 2;


    private float mCardGapBottom;
    private float mCardGap;
    private int mParallaxScale;
    private boolean mParallaxEnabled;


    private final int mScreenHeight;
    private final int fullCardHeight;
    private final int dp30;
    View[] cardViews;


    private CardStackLayout mParent;
    private boolean mScreenTouchable = false;
    private float mTouchFirstY = -1;
    private float mTouchPrevY = -1;
    private float mTouchDistance = 0;
    private int mSelectedCardPosition = -1;
    private boolean mShowInitAnimation;
    private float dp8;
    private float scaleFactorForElasticEffect;


    public abstract View getView(int position, ViewGroup container);

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
        int dp80 = (int) resources.getDimension(R.dimen.dp80);

        fullCardHeight = mScreenHeight - dp80;

        cardViews = new View[getCount()];
    }

    public void addView(final int position) {
        View root = getView(position, mParent);
        root.setOnTouchListener(this);
        root.setTag(R.id.cardstack_internal_position_tag, position);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullCardHeight);
        root.setLayoutParams(lp);
        if (mShowInitAnimation) {
            root.setY(getCardFinalY(position));
            setScreenTouchable(false);
        } else {
            root.setY(getCardOriginalY(position));
        }

        cardViews[position] = root;

        mParent.addView(root);
    }

    public float getCardFinalY(int position) {
        return mScreenHeight - dp30 - ((getCount() - position) * mCardGapBottom);
    }

    private float getCardOriginalY(int position) {
        return dp8 + mCardGap * position;
    }

    public void resetCards(Runnable r) {
        List<Animator> animations = new ArrayList<>(getCount());
        for (int i = 0; i < getCount(); i++) {
            final View child = cardViews[i];
            animations.add(ObjectAnimator.ofFloat(child, View.Y, (int) child.getY(), getCardOriginalY(i)));
        }
        startAnimations(animations, r);
    }

    private void startAnimations(List<Animator> animations, final Runnable r) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animations);
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.setInterpolator(new DecelerateInterpolator(DECELERATION_FACTOR));
        if (r != null) {
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    r.run();
                }
            });
        }
        animatorSet.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isScreenTouchable()) {
            log.e("onTouch: Invalid touch registered. Ignoring");
            return false;
        }

        float y = event.getY();
        int positionOfCardToMove = (int) v.getTag(R.id.cardstack_internal_position_tag);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                log.d("ACTION_DOWN: firstY=" + mTouchFirstY + ", y=" + event.getY());
                if (mTouchFirstY != -1 || mSelectedCardPosition != -1) {
                    log.e("firstY=" + mTouchFirstY + ", mSelectedCardPosition=" + mSelectedCardPosition);
                    return false;
                }
                mTouchPrevY = mTouchFirstY = y;
                mTouchDistance = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                log.d("ACTION_MOVE: firstY=" + mTouchFirstY + ", y=" + event.getY());
                moveCards(positionOfCardToMove, y - mTouchFirstY);
                mTouchDistance += Math.abs(y - mTouchPrevY);
                break;
            case MotionEvent.ACTION_UP:
                log.d("ACTION_UP: firstY=" + mTouchFirstY + ", y=" + event.getY());
                resetCards(null);
                if (mTouchDistance < dp8 && Math.abs(y - mTouchFirstY) < dp8) {
                    onClick(v);
                }
                mTouchPrevY = mTouchFirstY = -1;
                break;
        }
        return true;
    }

    @Override
    public void onClick(final View v) {
        log.d("y=" + v.getY() + ", selected=" + mSelectedCardPosition + ", vtag=" + v.getTag(R.id.cardstack_internal_position_tag));

        if (!isScreenTouchable()) {
            log.e("Invalid touch registered. Ignoring");
            return;
        }
        setScreenTouchable(false);
        if (mSelectedCardPosition == -1) {
            mSelectedCardPosition = (int) v.getTag(R.id.cardstack_internal_position_tag);
            log.d("selected=" + mSelectedCardPosition);

            List<Animator> animations = new ArrayList<>(getCount());
            for (int i = 0; i < getCount(); i++) {
                View child = cardViews[i];
                if (i != mSelectedCardPosition) {
                    animations.add(ObjectAnimator.ofFloat(child, View.Y, (int) child.getY(), getCardFinalY(i)));
                } else {
                    animations.add(ObjectAnimator.ofFloat(child, View.Y, (int) child.getY(), getCardOriginalY(0)));
                }
            }
            startAnimations(animations, new Runnable() {
                @Override
                public void run() {
                    if (mParent.getOnCardSelectedListener() != null) {
                        mParent.getOnCardSelectedListener().onCardSelected(v, mSelectedCardPosition);
                    }
                }
            });

        }
    }

    public void moveCards(int positionOfCardToMove, float diff) {
        if (diff < 0 || positionOfCardToMove < 0 || positionOfCardToMove >= getCount()) return;
        for (int i = positionOfCardToMove; i < getCount(); i++) {
            final View child = cardViews[i];
            float diffCard = diff / scaleFactorForElasticEffect;
            if (mParallaxEnabled)
                diffCard = diffCard * mParallaxScale * (getCount() + 1 - i);
            else diffCard = diffCard * (getCount() * 2 + 1);
            child.setY(getCardOriginalY(i) + diffCard);
        }
    }

    public void setAdapterParams(CardStackLayout cardStackLayout, float cardGapBottom, float cardGap, int parallaxScale, boolean parallaxEnabled, boolean showInitAnimation) {
        mParent = cardStackLayout;
        mCardGapBottom = cardGapBottom;
        mCardGap = cardGap;
        mParallaxScale = parallaxScale;
        mParallaxEnabled = parallaxEnabled;
        mShowInitAnimation = showInitAnimation;
    }

    public void resetCards() {
        resetCards(new Runnable() {
            @Override
            public void run() {
                mSelectedCardPosition = -1;
                setScreenTouchable(true);
            }
        });
    }
}
