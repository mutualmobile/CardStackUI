package com.mutualmobile.cardstack_sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mutualmobile.cardstack.CardStackLayout;

;

public class MainActivity extends AppCompatActivity {

    private CardStackLayout mCardStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCardStack = (CardStackLayout) findViewById(R.id.cardStack);
        mCardStack.setAdapter(new MyCardStackAdapter(this));
    }

    @Override
    public void onBackPressed() {
        if (mCardStack.isCardSelected()) {
            mCardStack.restoreCards();
        } else {
            super.onBackPressed();
        }
    }
}
