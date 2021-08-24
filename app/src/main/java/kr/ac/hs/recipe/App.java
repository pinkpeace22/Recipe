package kr.ac.hs.recipe;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.tag("young");
        Timber.plant(new Timber.DebugTree());
    }
}
