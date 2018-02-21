package com.dewnaveen.olaplaystudios.App;

import android.app.Application;


import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by naveendewangan on 21/12/17.
 */

public class App extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
         Realm.init(this);

/*
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
*/
    }
}