package com.mutualmobile.cardstack.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by tushar on 1/16/16.
 */
public class Units {
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
