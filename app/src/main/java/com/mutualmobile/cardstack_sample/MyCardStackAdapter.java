package com.mutualmobile.cardstack_sample;

import android.content.Context;
import android.support.v7.widget.CardView;
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
            R.color.violet,
            R.color.indigo,
            R.color.blue,
            R.color.green,
            R.color.yellow,
            R.color.orange,
            R.color.red
    };
    private final Context mContext;

    @Override
    public View getView(int position, ViewGroup container) {
        CardView root = (CardView) mInfalter.inflate(R.layout.card, container, false);
        root.setCardBackgroundColor(mContext.getResources().getColor(bgColorIds[position]));
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
        mContext = context;
        mInfalter = LayoutInflater.from(context);
    }
}
