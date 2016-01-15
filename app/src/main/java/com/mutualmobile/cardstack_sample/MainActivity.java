package com.mutualmobile.cardstack_sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mutualmobile.cardstack.CardStackLayout;

;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardStackLayout cardStack = (CardStackLayout) findViewById(R.id.cardStack);
        cardStack.setAdapter(new MyCardStackAdapter(this));
    }

}
