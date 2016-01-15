package com.mutualmobile.cardstack;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by tushar on 12/15/15.
 */
public class CardStackLayout extends FrameLayout {
    Logger log = new Logger(CardStackLayout.class.getSimpleName());

    private float mCardGapBottom;
    private boolean mRoundedEdgeEnabled;
    private float mCardGap;
    private boolean mShowInitAnimation;
    private boolean mParallaxEnabled;
    private int mParallaxScale;
    private OnCardSelected mOnCardSelectedListener = null;

    private Path mClipPath = new Path();
    private RectF mClipRect = new RectF();

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
        mParallaxEnabled = a.getBoolean(R.styleable.CardStackLayout_parallax_enabled, false);
        mShowInitAnimation = a.getBoolean(R.styleable.CardStackLayout_showInitAnimation, true);
        mParallaxScale = a.getInteger(R.styleable.CardStackLayout_parallax_scale, getResources().getInteger(R.integer.parallax_sacle_default));
        mCardGap = a.getDimension(R.styleable.CardStackLayout_card_gap, getResources().getDimension(R.dimen.card_gap));
        mCardGapBottom = a.getDimension(R.styleable.CardStackLayout_card_gap_bottom, getResources().getDimension(R.dimen.card_gap_bottom));
        mRoundedEdgeEnabled = a.getBoolean(R.styleable.CardStackLayout_roundedEdge, false);
        a.recycle();

    }

    public CardStackAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(CardStackAdapter adapter) {
        this.mAdapter = adapter;
        mAdapter.setAdapterParams(this, mCardGapBottom, mCardGap, mParallaxScale, mParallaxEnabled, mShowInitAnimation);
        for (int i = 0; i < mAdapter.getCount(); i++) {
            mAdapter.addView(i);
        }

        if (mShowInitAnimation) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    doEnterAnimation();
                }
            }, 500);
        }
    }

    private void doEnterAnimation() {
        mAdapter.resetCards();
    }

    public static interface OnCardSelected {
        void onCardSelected(View v, int position);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mRoundedEdgeEnabled) {
            // compute the mClipPath
            mClipPath.reset();
            mClipRect.set(0, 0, w, h);
            float radius = getResources().getDimension(R.dimen.card_radius);
            mClipPath.addRoundRect(mClipRect, radius, radius, Path.Direction.CW);
            mClipPath.close();
        }

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int save = -1;
        if (mRoundedEdgeEnabled) {
            save = canvas.save();
            canvas.clipPath(mClipPath);
        }
        super.dispatchDraw(canvas);
        if (mRoundedEdgeEnabled) {
            canvas.restoreToCount(save);
        }
    }

}
