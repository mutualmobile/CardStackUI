package com.mutualmobile.cardstack_sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mutualmobile.cardstack.CardStackAdapter;

/**
 * Created by tushar on 12/16/15.
 */
public class MyCardStackAdapter extends CardStackAdapter {

    private final LayoutInflater mInfalter;
    private static int[] bgColorIds = {
            R.color.rose,
            R.color.pastel_magenta,
            R.color.dark_green,
            R.color.dirty_green,
            R.color.teal,
            R.color.dark_blue
    };

    @Override
    public View getView(int position, ViewGroup container) {
        View root = mInfalter.inflate(R.layout.card, container, false);
        root.setBackgroundResource(bgColorIds[position]);
        TextView cardTitle = (TextView) root.findViewById(R.id.card_title);
        cardTitle.setText("Card " + position);
        return root;
    }

    @Override
    public int getCount() {
        return bgColorIds.length;
    }

    public MyCardStackAdapter(Context context) {
        super(context);
        mInfalter = LayoutInflater.from(context);
    }
}
