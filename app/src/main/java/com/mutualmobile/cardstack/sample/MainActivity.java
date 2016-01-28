package com.mutualmobile.cardstack.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mutualmobile.cardstack.CardStackLayout;
import com.mutualmobile.cardstack.sample.interfaces.OnRestartRequest;
import com.mutualmobile.cardstack.sample.utils.Logger;
import com.mutualmobile.cardstack.utils.Units;

public class MainActivity extends AppCompatActivity implements OnRestartRequest {

    private CardStackLayout mCardStackLayout;
    private Logger log = new Logger(MainActivity.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCardStackLayout = (CardStackLayout) findViewById(R.id.cardStack);
        setupAdapter();
    }

    private void setupAdapter() {

        mCardStackLayout.setShowInitAnimation(Prefs.isShowInitAnimationEnabled());

        mCardStackLayout.setParallaxEnabled(Prefs.isParallaxEnabled());
        mCardStackLayout.setParallaxScale(Prefs.getParallaxScale(this));

        mCardStackLayout.setCardGap(Units.dpToPx(this, Prefs.getCardGap(this)));
        mCardStackLayout.setCardGapBottom(Units.dpToPx(this, Prefs.getCardGapBottom(this)));

        mCardStackLayout.setAdapter(new MyCardStackAdapter(this));
    }

    @Override
    public void onBackPressed() {
        if (mCardStackLayout.isCardSelected()) {
            mCardStackLayout.restoreCards();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void requestRestart() {
        log.d("Restarting MainActivity..");
        mCardStackLayout.removeAdapter();

        mCardStackLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupAdapter();
            }
        }, 200);
    }
}
