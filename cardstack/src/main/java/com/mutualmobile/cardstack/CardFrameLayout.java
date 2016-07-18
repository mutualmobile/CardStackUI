package com.mutualmobile.cardstack;

import android.annotation.SuppressLint;
import android.widget.FrameLayout;

/**
 * Created by tushar on 7/18/16.
 */
@SuppressLint("ViewConstructor")
class CardFrameLayout extends FrameLayout {
    private final CardStackLayout parent;

    public CardFrameLayout(CardStackLayout cardStackLayout) {
        super(cardStackLayout.getContext());
        this.parent = cardStackLayout;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CardStackAdapter mAdapter = parent.getAdapter();
        if (mAdapter != null) {
            int height = (int) (mAdapter.getFullCardHeight() + (mAdapter.getCount() - 1) * parent.getCardGap());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}