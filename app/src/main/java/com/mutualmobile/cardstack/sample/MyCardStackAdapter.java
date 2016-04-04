package com.mutualmobile.cardstack.sample;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.mutualmobile.cardstack.CardStackAdapter;
import com.mutualmobile.cardstack.sample.interfaces.OnRestartRequest;
import com.mutualmobile.cardstack.sample.utils.Logger;
import com.tramsun.libs.prefcompat.Pref;

public class MyCardStackAdapter extends CardStackAdapter implements CompoundButton.OnCheckedChangeListener {
    private static int[] bgColorIds;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private Logger log = new Logger(MyCardStackAdapter.class.getSimpleName());
    private OnRestartRequest mCallback;
    private Runnable updateSettingsView;

    public MyCardStackAdapter(MainActivity activity) {
        super(activity);
        mContext = activity;
        mInflater = LayoutInflater.from(activity);
        mCallback = activity;
        bgColorIds = new int[]{
                R.color.card1_bg,
                R.color.card2_bg,
                R.color.card3_bg,
                R.color.card4_bg,
                R.color.card5_bg,
                R.color.card6_bg,
                R.color.card7_bg,
        };
    }

    @Override
    public int getCount() {
        return bgColorIds.length;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        log.d("onCheckedChanged() called with: " + "buttonView = [" + buttonView + "], isChecked = [" + isChecked + "]");
        switch (buttonView.getId()) {
            case R.id.parallax_enabled:
                Pref.putBoolean(Prefs.PARALLAX_ENABLED, isChecked);
                break;
            case R.id.show_init_animation:
                Pref.putBoolean(Prefs.SHOW_INIT_ANIMATION, isChecked);
                break;
        }
        updateSettingsView.run();
    }

    @Override
    public View createView(int position, ViewGroup container) {
        if (position == 0) return getSettingsView(container);

        CardView root = (CardView) mInflater.inflate(R.layout.card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[position]));
        TextView cardTitle = (TextView) root.findViewById(R.id.card_title);
        cardTitle.setText(mContext.getResources().getString(R.string.card_title, position));
        return root;
    }

    private View getSettingsView(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.settings_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[0]));

        final Switch showInitAnimation = (Switch) root.findViewById(R.id.show_init_animation);
        final Switch parallaxEnabled = (Switch) root.findViewById(R.id.parallax_enabled);
        final EditText parallaxScale = (EditText) root.findViewById(R.id.parallax_scale);
        final EditText cardGap = (EditText) root.findViewById(R.id.card_gap);
        final EditText cardGapBottom = (EditText) root.findViewById(R.id.card_gap_bottom);

        updateSettingsView = new Runnable() {
            @Override
            public void run() {
                showInitAnimation.setChecked(Prefs.isShowInitAnimationEnabled());
                showInitAnimation.setOnCheckedChangeListener(MyCardStackAdapter.this);

                boolean isParallaxEnabled = Prefs.isParallaxEnabled();
                parallaxEnabled.setChecked(isParallaxEnabled);
                parallaxEnabled.setOnCheckedChangeListener(MyCardStackAdapter.this);

                parallaxScale.setText("" + Prefs.getParallaxScale(mContext));
                parallaxScale.setEnabled(isParallaxEnabled);

                cardGap.setText("" + Prefs.getCardGap(mContext));

                cardGapBottom.setText("" + Prefs.getCardGapBottom(mContext));
            }
        };

        updateSettingsView.run();

        Button restartActivityButton = (Button) root.findViewById(R.id.restart_activity);
        restartActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePrefsIfRequired(parallaxScale);
                updatePrefsIfRequired(cardGap);
                updatePrefsIfRequired(cardGapBottom);
                mCallback.requestRestart();
            }
        });

        Button resetDefaultsButton = (Button) root.findViewById(R.id.reset_defaults);
        resetDefaultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.resetDefaults(mContext);
                updateSettingsView.run();
            }
        });

        return root;
    }

    private void updatePrefsIfRequired(EditText view) {
        String text = view.getText().toString();
        int value;

        try {
            value = Integer.parseInt(text);
        } catch (Exception e) {
            value = Integer.MIN_VALUE;
        }
        if (value == Integer.MIN_VALUE) {
            log.e("Invalid value for " + view.getResources().getResourceName(view.getId()));
            return;
        }

        switch (view.getId()) {
            case R.id.parallax_scale:
                log.d("parallax_scale now is " + Integer.parseInt(text));
                Pref.putInt(Prefs.PARALLAX_SCALE, Integer.parseInt(text));
                break;
            case R.id.card_gap:
                log.d("card_gap now is " + Integer.parseInt(text));
                Pref.putInt(Prefs.CARD_GAP, Integer.parseInt(text));
                break;
            case R.id.card_gap_bottom:
                log.d("card_gap_bottom now is " + Integer.parseInt(text));
                Pref.putInt(Prefs.CARD_GAP_BOTTOM, Integer.parseInt(text));
                break;
        }
    }

}
