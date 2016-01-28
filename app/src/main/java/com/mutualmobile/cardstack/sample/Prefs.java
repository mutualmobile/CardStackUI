package com.mutualmobile.cardstack.sample;

import android.content.Context;

import com.mutualmobile.cardstack.CardStackLayout;
import com.tramsun.libs.prefcompat.Pref;

public class Prefs {

    public static final String SHOW_INIT_ANIMATION = "showInitAnimation";
    public static final String PARALLAX_ENABLED = "parallaxEnabled";
    public static final String PARALLAX_SCALE = "parallaxScale";
    public static final String CARD_GAP = "cardGap";
    public static final String CARD_GAP_BOTTOM = "cardGapBottom";

    public static boolean isShowInitAnimationEnabled() {
        return Pref.getBoolean(SHOW_INIT_ANIMATION, CardStackLayout.SHOW_INIT_ANIMATION_DEFAULT);
    }

    public static boolean isParallaxEnabled() {
        return Pref.getBoolean(PARALLAX_ENABLED, CardStackLayout.PARALLAX_ENABLED_DEFAULT);
    }

    public static int getParallaxScale(Context context) {
        return Pref.getInt(PARALLAX_SCALE, context.getResources().getInteger(com.mutualmobile.cardstack.R.integer.parallax_scale_default));
    }

    public static int getCardGap(Context context) {
        int cardGapDimenInDp = (int) (context.getResources().getDimension(R.dimen.card_gap) / context.getResources().getDisplayMetrics().density);
        return Pref.getInt(CARD_GAP, cardGapDimenInDp);
    }

    public static int getCardGapBottom(Context context) {
        int cardGapBottomDimenInDp = (int) (context.getResources().getDimension(R.dimen.card_gap_bottom) / context.getResources().getDisplayMetrics().density);
        return Pref.getInt(CARD_GAP_BOTTOM, cardGapBottomDimenInDp);
    }

    public static void resetDefaults(Context context) {
        int cardGapDimenInDp = (int) (context.getResources().getDimension(R.dimen.card_gap) / context.getResources().getDisplayMetrics().density);
        int cardGapBottomDimenInDp = (int) (context.getResources().getDimension(R.dimen.card_gap_bottom) / context.getResources().getDisplayMetrics().density);

        Pref.putBoolean(SHOW_INIT_ANIMATION, CardStackLayout.SHOW_INIT_ANIMATION_DEFAULT);
        Pref.putBoolean(PARALLAX_ENABLED, CardStackLayout.PARALLAX_ENABLED_DEFAULT);
        Pref.putInt(PARALLAX_SCALE, context.getResources().getInteger(com.mutualmobile.cardstack.R.integer.parallax_scale_default));
        Pref.putInt(CARD_GAP, cardGapDimenInDp);
        Pref.putInt(CARD_GAP_BOTTOM, cardGapBottomDimenInDp);
    }
}
