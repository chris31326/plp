package com.example.xin.fileprotector.app;

import android.app.Application;
import android.os.StrictMode;

public class PlpApp extends Application {
    @Override
    public void onCreate() {
        final StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        super.onCreate();
    }
}
