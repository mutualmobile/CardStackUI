package com.mutualmobile.cardstack.sample;

import com.tramsun.libs.prefcompat.Pref;

/**
 * Created by tushar on 1/16/16.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Pref.init(this);
    }
}
