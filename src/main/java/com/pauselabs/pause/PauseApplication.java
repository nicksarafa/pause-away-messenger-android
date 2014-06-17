package com.pauselabs.pause;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

public class PauseApplication extends Application {

    private static PauseApplication instance;
    private static final String TAG = PauseApplication.class.getSimpleName();

    public PauseApplication() {

    }

    public PauseApplication(final Context context) {
        this();
        attachBaseContext(context);
    }

    public PauseApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // Perform injection
        Injector.init(getRootModule(), this);

    }

    private Object getRootModule() {
        return new RootModule();
    }

        public static PauseApplication getInstance() {
            return instance;
        }
}
